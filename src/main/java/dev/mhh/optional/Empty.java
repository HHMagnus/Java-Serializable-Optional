package dev.mhh.optional;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public record Empty<T>() implements Optional<T>, Serializable {
    @Override
    public String toString() {
        return "Optional.empty";
    }

    /**
     * Common instance for {@code empty()}.
     */
    static final Optional<?> EMPTY = new dev.mhh.optional.Empty<>();

    @Override
    public T get() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void ifPresent(Consumer<? super T> action) {
        // Nothing as it is empty
    }

    @Override
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        emptyAction.run();
    }

    @Override
    public Optional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return this;
    }

    @Override
    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return Optional.empty();
    }

    @Override
    public <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        return Optional.empty();
    }

    @Override
    public Optional<T> or(Supplier<? extends Optional<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        return this;
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public T orElse(T other) {
        return other;
    }

    @Override
    public T orElseGet(Supplier<? extends T> supplier) {
        return supplier.get();
    }

    @Override
    public T orElseThrow() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        throw exceptionSupplier.get();
    }
}
