package dev.mhh.optional;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.mhh.optional.Empty.EMPTY;

/**
 * This is a drop-in replacement of {@code java.utils.Optional} that is meant to provide the same methods, but allow to be serialized.
 *
 * <p>It can be used in fields and input types to mark a type as being {@code Optional}.
 * @param <T> The type of the value if it is present
 */
public sealed interface Optional<T> extends Serializable permits Empty, Present {
    /**
     * @see java.util.Optional#empty()
     */
    static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    /**
     * @see java.util.Optional#of(Object)
     */
    static <T> Optional<T> of(T value) {
        return new Present<>(value);
    }

    /**
     * @see java.util.Optional#ofNullable(Object)
     */
    @SuppressWarnings("unchecked")
    static <T> Optional<T> ofNullable(T value) {
        return value == null ? (Optional<T>) EMPTY
                : new Present<>(value);
    }

    /**
     * @see java.util.Optional#get()
     */
    T get();

    /**
     * @see java.util.Optional#isPresent()
     */
    boolean isPresent();

    /**
     * @see java.util.Optional#isEmpty()
     */
    boolean isEmpty();

    /**
     * @see java.util.Optional#ifPresent(Consumer)
     */
    void ifPresent(Consumer<? super T> action);

    /**
     * @see java.util.Optional#ifPresentOrElse(Consumer, Runnable)
     */
     void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction);

    /**
     * @see java.util.Optional#filter(Predicate)
     */
    Optional<T> filter(Predicate<? super T> predicate);

    /**
     * @see java.util.Optional#map(Function)
     */
    <U> Optional<U> map(Function<? super T, ? extends U> mapper);

    /**
     * @see java.util.Optional#flatMap(Function)
     */
    <U> Optional<U> flatMap(Function<? super T, ? extends Optional<? extends U>> mapper);

    /**
     * @see java.util.Optional#or(Supplier)
     */
    Optional<T> or(Supplier<? extends Optional<? extends T>> supplier);

    /**
     * @see java.util.Optional#stream()
     */
    Stream<T> stream();

    /**
     * @see java.util.Optional#orElse(Object)
     */
    T orElse(T other);

    /**
     * @see java.util.Optional#orElseGet(Supplier)
     */
    T orElseGet(Supplier<? extends T> supplier);

    /**
     * @see java.util.Optional#orElseThrow()
     */
    T orElseThrow();

    /**
     * @see java.util.Optional#orElseThrow(Supplier)
     */
    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;
}
