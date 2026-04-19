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

This implementation validates all used argument functions before they are used. This differs from the JDK's `Optional<T>` for `flatMap`, `map`, `filter` and `or` where they will throw an exception if the function is null either way.

Manual construction of `Empty` and `Present` is **not** recommended.
