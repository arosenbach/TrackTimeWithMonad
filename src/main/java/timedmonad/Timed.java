package timedmonad;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Timed<A> {

    private final Map<String, List<NamedStopwatch>> stopwatches;
    private final A value;

    private Timed(A value, Map<String, List<NamedStopwatch>> stopwatches) {
        this.stopwatches = ImmutableMap.copyOf(stopwatches);
        this.value = value;
    }

    public static <A> Timed<A> of(A value, NamedStopwatch namedStopwatch) {
        return new Timed<>(value, Stream.of(namedStopwatch).collect(groupingBy(NamedStopwatch::getName)));
    }

    public static <A> Timed<A> empty(A emptyValue) {
        return new Timed<>(emptyValue, Collections.emptyMap());
    }

    public static <A> Supplier<Timed<A>> lift(final String name, final Supplier<A> supplier) {
        return () -> {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final A value = supplier.get();
            stopwatch.stop();
            return Timed.of(value, NamedStopwatch.of(name, stopwatch));
        };
    }

    public static <A, B> Function<A, Timed<B>> lift(final String name, final Function<A, B> function) {
        return (arg) -> {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final B value = function.apply(arg);
            stopwatch.stop();
            return Timed.of(value, NamedStopwatch.of(name, stopwatch));
        };
    }

    public static <A, B, C> BiFunction<A, B, Timed<C>> lift(final String name, final BiFunction<A, B, C> biFunction) {
        return (arg1, arg2) -> {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final C value = biFunction.apply(arg1, arg2);
            stopwatch.stop();
            return Timed.of(value, NamedStopwatch.of(name, stopwatch));
        };
    }

    public <B> Timed<B> flatMap(Supplier<Timed<B>> f) {
        Timed<B> mappedTimed = f.get();
        return new Timed<>(mappedTimed.value, mergeStopwatches(mappedTimed.stopwatches));
    }

    public <B> Timed<B> flatMap(Function<A, Timed<B>> f) {
        Timed<B> mappedTimed = f.apply(value);
        return new Timed<>(mappedTimed.value, mergeStopwatches(mappedTimed.stopwatches));
    }

    public <B> Timed<A> append(final Timed<B> other, BiFunction<A, B, A> mergeFunction) {
        return new Timed<>(mergeFunction.apply(value, other.value), mergeStopwatches(other.stopwatches));
    }

    private Map<String, List<NamedStopwatch>> mergeStopwatches(final Map<String, List<NamedStopwatch>> otherStopwatches) {
        return Stream.concat(stopwatches.entrySet().stream(), otherStopwatches.entrySet().stream())
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        this::concatenateLists));
    }

    private <T> List<T> concatenateLists(List<T> listA, List<T> listB) {
        return Stream.concat(listA.stream(), listB.stream())
                .collect(toList());
    }

    public A getValue() {
        return value;
    }

    public Map<String, List<Stopwatch>> getAllStopwatches() {
        return stopwatches.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(toMap(
                        NamedStopwatch::getName,
                        namedStopwatch -> Collections.singletonList(namedStopwatch.getStopwatch()),
                        this::concatenateLists));
    }

    public List<Stopwatch> getStopwatches(String name) {
        return stopwatches.getOrDefault(name, Collections.emptyList())
                .stream()
                .map(NamedStopwatch::getStopwatch)
                .collect(toList());
    }

    public Optional<Long> elapsed(final String name, final TimeUnit timeUnit) {
        return Optional.ofNullable(stopwatches.get(name))
                .map(list -> list.stream().map(NamedStopwatch::getStopwatch)
                        .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                        .sum());
    }

    @Override
    public String toString() {
        return "Timed{" +
                "stopwatches=" + stopwatches +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(final Object runnable) {
        if (this == runnable) return true;
        if (runnable == null || getClass() != runnable.getClass()) return false;
        final Timed<?> timed = (Timed<?>) runnable;
        return Objects.equals(stopwatches, timed.stopwatches) &&
                Objects.equals(value, timed.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopwatches, value);
    }

    public static class NamedStopwatch {

        private String name;
        private Stopwatch stopwatch;

        private NamedStopwatch(final String name, final Stopwatch stopwatch) {
            this.name = name;
            this.stopwatch = stopwatch;
        }

        public static NamedStopwatch of(final String name, final Stopwatch stopwatch) {
            return new NamedStopwatch(name, stopwatch);
        }

        private String getName() {
            return name;
        }

        private Stopwatch getStopwatch() {
            return this.stopwatch;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final NamedStopwatch that = (NamedStopwatch) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, stopwatch);
        }

        @Override
        public String toString() {
            return "NamedStopwatch{" +
                    "name='" + name + '\'' +
                    ", stopwatch=" + stopwatch +
                    '}';
        }
    }
}
