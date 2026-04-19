package dev.mhh.optional;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class OptionalTest {
    private static final Optional<Long> empty = Optional.empty();
    private static final Optional<Long> present10 = Optional.of(10L);

    @Test
    void empty() {
        final var empty = Optional.empty();

        assertTrue(empty.isEmpty());
        assertThrows(NoSuchElementException.class, empty::get);
        assertFalse(empty.isPresent());
    }

    @Test
    void of() {
        final var of = Optional.of(10L);

        assertTrue(of.isPresent());
        assertEquals(10L, of.get());
        assertFalse(of.isEmpty());
    }

    @Test
    void ofNull() {
        assertThrows(NullPointerException.class, () -> Optional.of(null));
    }

    @Test
    void ofNullable() {
        final var ofNullable = Optional.of(10L);

        assertTrue(ofNullable.isPresent());
        assertEquals(10L, ofNullable.get());
        assertFalse(ofNullable.isEmpty());
    }

    @Test
    void ofNullableNull() {
        final var ofNullable = Optional.ofNullable(null);

        assertTrue(ofNullable.isEmpty());
    }

    @Test
    void ifPresentWhenEmpty() {
        final var ifPresentCalled = new AtomicBoolean(false);

        empty.ifPresent(_x -> ifPresentCalled.set(true));

        assertFalse(ifPresentCalled.get());

    }

    @Test
    void ifPresentWhenPresent() {
        final var ifPresentCalled = new AtomicBoolean(false);

        present10.ifPresent(x -> ifPresentCalled.set(x == 10));

        assertTrue(ifPresentCalled.get());
    }

    @Test
    void ifPresentOrElseWhenEmpty() {
        final var ifPresentCalled = new AtomicBoolean(false);
        final var orElseCalled = new AtomicBoolean(false);

        empty.ifPresentOrElse(_x -> ifPresentCalled.set(true), () -> orElseCalled.set(true));

        assertTrue(orElseCalled.get());
        assertFalse(ifPresentCalled.get());
    }

    @Test
    void ifPresentOrElseWhenPresent() {
        final var ifPresentCalled = new AtomicBoolean(false);
        final var orElseCalled = new AtomicBoolean(false);

        present10.ifPresentOrElse(x -> ifPresentCalled.set(x == 10), () -> orElseCalled.set(true));

        assertFalse(orElseCalled.get());
        assertTrue(ifPresentCalled.get());
    }

    @Test
    void filterWhenEmpty() {
        final var filterCalled = new AtomicBoolean(false);
        final var filtered = empty.filter(x -> {
            filterCalled.set(true);
            return true;
        });

        assertTrue(filtered.isEmpty());
        assertFalse(filterCalled.get());
    }

    @Test
    void filterTrueWhenPresent() {
        final var filterCalled = new AtomicBoolean(false);
        final var filtered = present10.filter(x -> {
            filterCalled.set(true);
            return true;
        });

        assertTrue(filtered.isPresent());
        assertTrue(filterCalled.get());

        assertEquals(present10, filtered);
    }

    @Test
    void filterFalseWhenPresent() {
        final var filterCalled = new AtomicBoolean(false);
        final var filtered = present10.filter(x -> {
            filterCalled.set(true);
            return false;
        });

        assertTrue(filtered.isEmpty());
        assertTrue(filterCalled.get());

        assertEquals(empty, filtered);
    }

    @Test
    void mapWhenEmpty() {
        final var mapped = empty.map(x -> x + 1);

        assertTrue(mapped.isEmpty());
    }

    @Test
    void mapWhenPresent() {
        final var mapped = present10.map(x -> x + 1);

        assertEquals(Optional.of(11L), mapped);
    }

    @Test
    void flatMapWhenEmpty() {
        final var mapped = empty.flatMap(x -> Optional.of(x + 1));

        assertTrue(mapped.isEmpty());
    }

    @Test
    void flatMapToEmptyWhenPresent() {
        final var mapped = present10.flatMap(x -> Optional.empty());

        assertTrue(mapped.isEmpty());
    }

    @Test
    void flatMapToPresentWhenPresent() {
        final var mapped = present10.flatMap(x -> Optional.of(x + 1));

        assertEquals(Optional.of(11L), mapped);
    }

    @Test
    void orWhenEmpty() {
        final var or = empty.or(() -> Optional.of(10L));

        assertEquals(Optional.of(10L), or);
    }

    @Test
    void orWhenPresent() {
        final var orCalled = new AtomicBoolean(false);
        final var or = present10.or(() -> {
            orCalled.set(true);
            return Optional.of(10L);
        });

        assertEquals(present10, or);
        assertFalse(orCalled.get());
    }

    @Test
    void streamWhenEmpty() {
        final var stream = empty.stream();

        assertEquals(0, stream.count());
    }

    @Test
    void streamWhenPresent() {
        final var list = present10.stream()
                .toList();

        assertEquals(1, list.size());
        assertEquals(10L, list.get(0));
    }

    @Test
    void orElseWhenEmpty() {
        final var orElse = empty.orElse(10L);

        assertEquals(10L, orElse);
    }

    @Test
    void orElseWhenPresent() {
        final var orElse = present10.orElse(250L);

        assertEquals(10L, orElse);
    }

    @Test
    void orElseGetWhenEmpty() {
        final var orElseGet = empty.orElseGet(() -> 10L);

        assertEquals(10L, orElseGet);
    }

    @Test
    void orElseGetWhenPresent() {
        final var orElseGetCalled = new AtomicBoolean(false);
        final var orElseGet = present10.orElseGet(() -> {
            orElseGetCalled.set(true);
            return 250L;
        });

        assertEquals(10L, orElseGet);

        assertFalse(orElseGetCalled.get());
    }

    @Test
    void orElseThrowWhenEmpty() {
        assertThrows(NoSuchElementException.class, empty::orElseThrow);
    }

    @Test
    void orElseThrowWhenPresent() {
        assertEquals(10L, present10.orElseThrow());
    }

    @Test
    void orElseThrowSupplierWhenEmpty() {
        final var called = new AtomicBoolean(false);
        assertThrows(NoSuchElementException.class, () -> empty.orElseThrow(() -> {
            called.set(true);
            return new NoSuchElementException();
        }));
        assertTrue(called.get());
    }

    @Test
    void orElseThrowSupplierWhenPresent() {
        final var called = new AtomicBoolean(false);
        final var value = present10.orElseThrow(() -> {
            called.set(true);
            return new NoSuchElementException();
        });

        assertEquals(10L, value);
        assertFalse(called.get());
    }

    @Test
    void testToString() {
        assertEquals("Optional[10]", present10.toString());
        assertEquals("Optional.empty", empty.toString());
    }
}
