package dev.mhh.optional;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
public class OptionalBenchmark {

    private static final String VALUE = "hello";
    private static final String FALLBACK = "fallback";

    // ------------------------------------------------------------------ //
    //  State: toggle between present / empty / mixed via @Param            //
    // ------------------------------------------------------------------ //

    @Param({"mixed", "present", "empty"})
    private String scenario;

    private int counter;          // used for mixed scenario alternation

    // ------------------------------------------------------------------ //
    //  1. Construction                                                      //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void of_jdk(Blackhole bh) {
        bh.consume(java.util.Optional.of(VALUE));
    }

    @Benchmark
    public void of_mhh(Blackhole bh) {
        bh.consume(Optional.of(VALUE));
    }

    @Benchmark
    public void ofNullable_jdk(Blackhole bh) {
        bh.consume(java.util.Optional.ofNullable(nullableValue()));
    }

    @Benchmark
    public void ofNullable_mhh(Blackhole bh) {
        bh.consume(Optional.ofNullable(nullableValue()));
    }

    @Benchmark
    public void empty_jdk(Blackhole bh) {
        bh.consume(java.util.Optional.empty());
    }

    @Benchmark
    public void empty_mhh(Blackhole bh) {
        bh.consume(Optional.empty());
    }

    // ------------------------------------------------------------------ //
    //  2. Presence checks                                                   //
    // ------------------------------------------------------------------ //

    @Benchmark
    public boolean isPresent_jdk() {
        return jdkOptional().isPresent();
    }

    @Benchmark
    public boolean isPresent_mhh() {
        return mhhOptional().isPresent();
    }

    @Benchmark
    public boolean isEmpty_jdk() {
        return jdkOptional().isEmpty();
    }

    @Benchmark
    public boolean isEmpty_mhh() {
        return mhhOptional().isEmpty();
    }

    // ------------------------------------------------------------------ //
    //  3. Value retrieval                                                   //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void get_jdk(Blackhole bh) {
        var opt = jdkOptional();
        if (opt.isPresent()) bh.consume(opt.get());
    }

    @Benchmark
    public void get_mhh(Blackhole bh) {
        var opt = mhhOptional();
        if (opt.isPresent()) bh.consume(opt.get());
    }

    // ------------------------------------------------------------------ //
    //  4. orElse / orElseGet / orElseThrow                                 //
    // ------------------------------------------------------------------ //

    @Benchmark
    public String orElse_jdk() {
        return jdkOptional().orElse(FALLBACK);
    }

    @Benchmark
    public String orElse_mhh() {
        return mhhOptional().orElse(FALLBACK);
    }

    @Benchmark
    public String orElseGet_jdk() {
        return jdkOptional().orElseGet(() -> FALLBACK);
    }

    @Benchmark
    public String orElseGet_mhh() {
        return mhhOptional().orElseGet(() -> FALLBACK);
    }

    @Benchmark
    public void orElseThrow_jdk(Blackhole bh) {
        var opt = jdkOptional();
        if (opt.isPresent()) bh.consume(opt.orElseThrow());
    }

    @Benchmark
    public void orElseThrow_mhh(Blackhole bh) {
        var opt = mhhOptional();
        if (opt.isPresent()) bh.consume(opt.orElseThrow());
    }

    // ------------------------------------------------------------------ //
    //  5. map / flatMap                                                     //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void map_jdk(Blackhole bh) {
        bh.consume(jdkOptional().map(String::toUpperCase));
    }

    @Benchmark
    public void map_mhh(Blackhole bh) {
        bh.consume(mhhOptional().map(String::toUpperCase));
    }

    @Benchmark
    public void flatMap_jdk(Blackhole bh) {
        bh.consume(jdkOptional().flatMap(v -> java.util.Optional.of(v.toUpperCase())));
    }

    @Benchmark
    public void flatMap_mhh(Blackhole bh) {
        bh.consume(mhhOptional().flatMap(v -> Optional.of(v.toUpperCase())));
    }

    // ------------------------------------------------------------------ //
    //  6. filter                                                            //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void filter_jdk(Blackhole bh) {
        bh.consume(jdkOptional().filter(v -> v.startsWith("h")));
    }

    @Benchmark
    public void filter_mhh(Blackhole bh) {
        bh.consume(mhhOptional().filter(v -> v.startsWith("h")));
    }

    // ------------------------------------------------------------------ //
    //  7. ifPresent / ifPresentOrElse                                      //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void ifPresent_jdk(Blackhole bh) {
        jdkOptional().ifPresent(bh::consume);
    }

    @Benchmark
    public void ifPresent_mhh(Blackhole bh) {
        mhhOptional().ifPresent(bh::consume);
    }

    @Benchmark
    public void ifPresentOrElse_jdk(Blackhole bh) {
        jdkOptional().ifPresentOrElse(bh::consume, () -> bh.consume(FALLBACK));
    }

    @Benchmark
    public void ifPresentOrElse_mhh(Blackhole bh) {
        mhhOptional().ifPresentOrElse(bh::consume, () -> bh.consume(FALLBACK));
    }

    // ------------------------------------------------------------------ //
    //  8. or (returns another Optional)                                    //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void or_jdk(Blackhole bh) {
        bh.consume(jdkOptional().or(() -> java.util.Optional.of(FALLBACK)));
    }

    @Benchmark
    public void or_mhh(Blackhole bh) {
        bh.consume(mhhOptional().or(() -> Optional.of(FALLBACK)));
    }

    // ------------------------------------------------------------------ //
    //  9. stream                                                            //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void stream_jdk(Blackhole bh) {
        jdkOptional().stream().forEach(bh::consume);
    }

    @Benchmark
    public void stream_mhh(Blackhole bh) {
        mhhOptional().stream().forEach(bh::consume);
    }

    // ------------------------------------------------------------------ //
    //  10. Chained pipeline — the most revealing test                      //
    //      filter -> map -> orElse                                         //
    // ------------------------------------------------------------------ //

    @Benchmark
    public String chain_jdk() {
        return jdkOptional()
                .filter(v -> !v.isEmpty())
                .map(String::toUpperCase)
                .orElse(FALLBACK);
    }

    @Benchmark
    public String chain_mhh() {
        return mhhOptional()
                .filter(v -> !v.isEmpty())
                .map(String::toUpperCase)
                .orElse(FALLBACK);
    }

    // ------------------------------------------------------------------ //
    //  11. Longer chain — flatMap -> filter -> map -> orElseGet            //
    // ------------------------------------------------------------------ //

    @Benchmark
    public String longChain_jdk() {
        return jdkOptional()
                .flatMap(v -> java.util.Optional.of(v.trim()))
                .filter(v -> v.length() > 2)
                .map(v -> v + "_processed")
                .orElseGet(() -> FALLBACK);
    }

    @Benchmark
    public String longChain_mhh() {
        return mhhOptional()
                .flatMap(v -> Optional.of(v.trim()))
                .filter(v -> v.length() > 2)
                .map(v -> v + "_processed")
                .orElseGet(() -> FALLBACK);
    }

    // ------------------------------------------------------------------ //
    //  Helpers                                                              //
    // ------------------------------------------------------------------ //

    private String nullableValue() {
        return switch (scenario) {
            case "present" -> VALUE;
            case "empty"   -> null;
            default        -> (++counter % 2 == 0) ? VALUE : null;  // mixed
        };
    }

    private java.util.Optional<String> jdkOptional() {
        return switch (scenario) {
            case "present" -> java.util.Optional.of(VALUE);
            case "empty"   -> java.util.Optional.empty();
            default        -> (++counter % 2 == 0)
                    ? java.util.Optional.of(VALUE)
                    : java.util.Optional.empty();
        };
    }

    private Optional<String> mhhOptional() {
        return switch (scenario) {
            case "present" -> Optional.of(VALUE);
            case "empty"   -> Optional.empty();
            default        -> (++counter % 2 == 0)
                    ? Optional.of(VALUE)
                    : Optional.empty();
        };
    }
}