# Serializable Optional&lt;T&gt; using sealed interface

This library is a reimplementation of an `Optional<T>` that is `Serializable`. It aims to behave exactly like the JDK's `Optional<T>`, with the added benefit of being `Serializable`, and implemented using a `sealed interface`.

With this implementation you can switch over the `Optional<T>`:
```java
final var message = switch (optional) {
    case Empty -> "No message";
    case Present(var message) -> message;
};
```

Or use an early deconstruction (JDK 21+):

```java
if (!(optional instanceof Present(var message))) {
    return;
}
System.out.println(message);
```

This implementation validates all argument functions before they are used. This differs from the JDK's `Optional<T>` for `flatMap`, `map`, `filter`, and `or`, where they will throw an exception if the function is null.

Manual construction of `Empty` and `Present` is **not** recommended.

## Usage

Import into a gradle project using:
```groovy
implementation 'dev.mhh:optional:1.0.3'
```

## Performance

TL;DR: There is no noticeable performance difference between this implementation and the JDK's.

The project contains a JMH Benchmark (`OptionalBenchmark`) that tests the performance difference between this `Optional` implementation and the JDK's.

It tests three different scenarios:
- Present case. The value is present.
- Empty case. The value is empty.
- Mixed case. The value switches between being present and empty.

These are their results:
- [Middle run performance benchmark](benchmark_results_2026_04_20.txt)
- [Long run performance benchmark](benchmark_long_run_2026_04_21.txt)
- [Short run performance benchmark](benchmark_short_run_2026_04_21.txt)

Each of these runs shows this implementation as faster on some operations and the JDK's as faster on others. However, this is inconsistent across runs. Therefore, I concluded that there is no noticeable performance difference between this implementation and the JDK's. Additionally, the difference is in nanoseconds, which projects considering serializability would not care about.
