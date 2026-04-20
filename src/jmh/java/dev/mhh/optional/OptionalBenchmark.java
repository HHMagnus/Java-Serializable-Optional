package dev.mhh.optional;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(2)
public class OptionalBenchmark {

    private static final String VALUE = "hello";
    private static final String FALLBACK = "fallback";

    // ------------------------------------------------------------------ //
    //  State: toggle between present / empty / mixed via @Param            //
    // ------------------------------------------------------------------ //

    @Param({"present", "empty", "mixed"})
    private String scenario;

    private int counter;          // used for mixed scenario alternation

    // ------------------------------------------------------------------ //
    //  1. Construction                                                      //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void jdk_of(Blackhole bh) {
        bh.consume(java.util.Optional.of(VALUE));
    }

    @Benchmark
    public void custom_of(Blackhole bh) {
        bh.consume(Optional.of(VALUE));
    }

    @Benchmark
    public void jdk_ofNullable(Blackhole bh) {
        bh.consume(java.util.Optional.ofNullable(nullableValue()));
    }

    @Benchmark
    public void custom_ofNullable(Blackhole bh) {
        bh.consume(Optional.ofNullable(nullableValue()));
    }

    @Benchmark
    public void jdk_empty(Blackhole bh) {
        bh.consume(java.util.Optional.empty());
    }

    @Benchmark
    public void custom_empty(Blackhole bh) {
        bh.consume(Optional.empty());
    }

    // ------------------------------------------------------------------ //
    //  2. Presence checks                                                   //
    // ------------------------------------------------------------------ //

    @Benchmark
    public boolean jdk_isPresent() {
        return jdkOptional().isPresent();
    }

    @Benchmark
    public boolean custom_isPresent() {
        return customOptional().isPresent();
    }

    @Benchmark
    public boolean jdk_isEmpty() {
        return jdkOptional().isEmpty();
    }

    @Benchmark
    public boolean custom_isEmpty() {
        return customOptional().isEmpty();
    }

    // ------------------------------------------------------------------ //
    //  3. Value retrieval                                                   //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void jdk_get(Blackhole bh) {
        var opt = jdkOptional();
        if (opt.isPresent()) bh.consume(opt.get());
    }

    @Benchmark
    public void custom_get(Blackhole bh) {
        var opt = customOptional();
        if (opt.isPresent()) bh.consume(opt.get());
    }

    // ------------------------------------------------------------------ //
    //  4. orElse / orElseGet / orElseThrow                                 //
    // ------------------------------------------------------------------ //

    @Benchmark
    public String jdk_orElse() {
        return jdkOptional().orElse(FALLBACK);
    }

    @Benchmark
    public String custom_orElse() {
        return customOptional().orElse(FALLBACK);
    }

    @Benchmark
    public String jdk_orElseGet() {
        return jdkOptional().orElseGet(() -> FALLBACK);
    }

    @Benchmark
    public String custom_orElseGet() {
        return customOptional().orElseGet(() -> FALLBACK);
    }

    @Benchmark
    public void jdk_orElseThrow(Blackhole bh) {
        var opt = jdkOptional();
        if (opt.isPresent()) bh.consume(opt.orElseThrow());
    }

    @Benchmark
    public void custom_orElseThrow(Blackhole bh) {
        var opt = customOptional();
        if (opt.isPresent()) bh.consume(opt.orElseThrow());
    }

    // ------------------------------------------------------------------ //
    //  5. map / flatMap                                                     //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void jdk_map(Blackhole bh) {
        bh.consume(jdkOptional().map(String::toUpperCase));
    }

    @Benchmark
    public void custom_map(Blackhole bh) {
        bh.consume(customOptional().map(String::toUpperCase));
    }

    @Benchmark
    public void jdk_flatMap(Blackhole bh) {
        bh.consume(jdkOptional().flatMap(v -> java.util.Optional.of(v.toUpperCase())));
    }

    @Benchmark
    public void custom_flatMap(Blackhole bh) {
        bh.consume(customOptional().flatMap(v -> Optional.of(v.toUpperCase())));
    }

    // ------------------------------------------------------------------ //
    //  6. filter                                                            //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void jdk_filter(Blackhole bh) {
        bh.consume(jdkOptional().filter(v -> v.startsWith("h")));
    }

    @Benchmark
    public void custom_filter(Blackhole bh) {
        bh.consume(customOptional().filter(v -> v.startsWith("h")));
    }

    // ------------------------------------------------------------------ //
    //  7. ifPresent / ifPresentOrElse                                      //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void jdk_ifPresent(Blackhole bh) {
        jdkOptional().ifPresent(bh::consume);
    }

    @Benchmark
    public void custom_ifPresent(Blackhole bh) {
        customOptional().ifPresent(bh::consume);
    }

    @Benchmark
    public void jdk_ifPresentOrElse(Blackhole bh) {
        jdkOptional().ifPresentOrElse(bh::consume, () -> bh.consume(FALLBACK));
    }

    @Benchmark
    public void custom_ifPresentOrElse(Blackhole bh) {
        customOptional().ifPresentOrElse(bh::consume, () -> bh.consume(FALLBACK));
    }

    // ------------------------------------------------------------------ //
    //  8. or (returns another Optional)                                    //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void jdk_or(Blackhole bh) {
        bh.consume(jdkOptional().or(() -> java.util.Optional.of(FALLBACK)));
    }

    @Benchmark
    public void custom_or(Blackhole bh) {
        bh.consume(customOptional().or(() -> Optional.of(FALLBACK)));
    }

    // ------------------------------------------------------------------ //
    //  9. stream                                                            //
    // ------------------------------------------------------------------ //

    @Benchmark
    public void jdk_stream(Blackhole bh) {
        jdkOptional().stream().forEach(bh::consume);
    }

    @Benchmark
    public void custom_stream(Blackhole bh) {
        customOptional().stream().forEach(bh::consume);
    }

    // ------------------------------------------------------------------ //
    //  10. Chained pipeline — the most revealing test                      //
    //      filter -> map -> orElse                                         //
    // ------------------------------------------------------------------ //

    @Benchmark
    public String jdk_chain() {
        return jdkOptional()
                .filter(v -> !v.isEmpty())
                .map(String::toUpperCase)
                .orElse(FALLBACK);
    }

    @Benchmark
    public String custom_chain() {
        return customOptional()
                .filter(v -> !v.isEmpty())
                .map(String::toUpperCase)
                .orElse(FALLBACK);
    }

    // ------------------------------------------------------------------ //
    //  11. Longer chain — flatMap -> filter -> map -> orElseGet            //
    // ------------------------------------------------------------------ //

    @Benchmark
    public String jdk_longChain() {
        return jdkOptional()
                .flatMap(v -> java.util.Optional.of(v.trim()))
                .filter(v -> v.length() > 2)
                .map(v -> v + "_processed")
                .orElseGet(() -> FALLBACK);
    }

    @Benchmark
    public String custom_longChain() {
        return customOptional()
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

    private Optional<String> customOptional() {
        return switch (scenario) {
            case "present" -> Optional.of(VALUE);
            case "empty"   -> Optional.empty();
            default        -> (++counter % 2 == 0)
                    ? Optional.of(VALUE)
                    : Optional.empty();
        };
    }
}