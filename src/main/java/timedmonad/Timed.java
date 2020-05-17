package timedmonad;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Timed<A> {

    private final Map<String, List<Stopwatch>> stopwatches;
    private final A value;

    private Timed(A value, Map<String, List<Stopwatch>> stopwatches) {
        this.stopwatches = ImmutableMap.copyOf(stopwatches);
        this.value = value;
    }

    public static <A> Timed<A> of(A value, Stopwatch stopwatch) {
        return new Timed<>(value, Stream.of(stopwatch).collect(groupingBy(Stopwatch::getId)));
    }

    public static <A> Timed<A> empty(A emptyValue) {
        return new Timed<>(emptyValue, Collections.emptyMap());
    }

    public static <A> Supplier<Timed<A>> lift(final String id, final Supplier<A> supplier) {
        return () -> {
            final com.google.common.base.Stopwatch stopwatch = com.google.common.base.Stopwatch.createStarted();
            final A value = supplier.get();
            stopwatch.stop();
            return Timed.of(value, Stopwatch.of(id, stopwatch));
        };
    }

    public static <A, B> Function<A, Timed<B>> lift(final String id, final Function<A, B> function) {
        return (arg) -> {
            final com.google.common.base.Stopwatch stopwatch = com.google.common.base.Stopwatch.createStarted();
            final B value = function.apply(arg);
            stopwatch.stop();
            return Timed.of(value, Stopwatch.of(id, stopwatch));
        };
    }

    public static <A, B, C> BiFunction<A, B, Timed<C>> lift(final String id, final BiFunction<A, B, C> biFunction) {
        return (arg1, arg2) -> {
            final com.google.common.base.Stopwatch stopwatch = com.google.common.base.Stopwatch.createStarted();
            final C value = biFunction.apply(arg1, arg2);
            stopwatch.stop();
            return Timed.of(value, Stopwatch.of(id, stopwatch));
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

    private Map<String, List<Stopwatch>> mergeStopwatches(final Map<String, List<Stopwatch>> otherStopwatches) {
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

    /**
     * Provides the sum of the times for a given stopwatch id
     *
     * @param id       id of a stopwatch
     * @param timeUnit
     * @return
     */
    public OptionalLong elapsed(final String id, final TimeUnit timeUnit) {
        final List<Stopwatch> stopwatches = this.stopwatches.get(id);
        if (stopwatches == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(stopwatches
                .stream()
                .map(Stopwatch::getStopwatch)
                .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                .sum());
    }

    /**
     * Provides the sum of all times
     *
     * @param timeUnit
     * @return
     */
    public long elapsed(final TimeUnit timeUnit) {
        return this.stopwatches.values()
                .stream()
                .flatMap(Collection::stream)
                .map(Stopwatch::getStopwatch)
                .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                .sum();
    }

    public OptionalDouble average(final String id, final TimeUnit timeUnit) {
        final List<Stopwatch> stopwatches = this.stopwatches.get(id);
        if (stopwatches == null) {
            return OptionalDouble.empty();
        }
        return stopwatches
                .stream()
                .map(Stopwatch::getStopwatch)
                .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                .average();
    }

    public OptionalLong min(final String id, final TimeUnit timeUnit) {
        final List<Stopwatch> stopwatches = this.stopwatches.get(id);
        if (stopwatches == null) {
            return OptionalLong.empty();
        }
        return stopwatches
                .stream()
                .map(Stopwatch::getStopwatch)
                .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                .min();
    }

    public OptionalLong max(final String id, final TimeUnit timeUnit) {
        final List<Stopwatch> stopwatches = this.stopwatches.get(id);
        if (stopwatches == null) {
            return OptionalLong.empty();
        }
        return stopwatches
                .stream()
                .map(Stopwatch::getStopwatch)
                .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                .max();
    }

    public OptionalInt count(String id){
        final List<Stopwatch> stopwatches = this.stopwatches.get(id);
        if (stopwatches == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(stopwatches.size());
    }

    public OptionalLong percentile(int percent, String id, final TimeUnit timeUnit) {
        if (percent <= 0 || percent > 100) {
            throw new IllegalArgumentException("Invalid percentile: " + percent);
        }
        final List<Stopwatch> stopwatches = this.stopwatches.get(id);
        if (stopwatches == null) {
            return OptionalLong.empty();
        }
        final List<Long> sorted = stopwatches.stream()
                .map(Stopwatch::getStopwatch)
                .map(stopwatch -> stopwatch.elapsed(timeUnit))
                .sorted()
                .collect(toList());

        final double position = (sorted.size() - 1) * (percent / 100f);
        final int base = (int) Math.floor(position);
        final long rest = (long) position - base;
        if (base < sorted.size() - 1) {
            return OptionalLong.of(sorted.get(base) + rest * (sorted.get(base + 1) - sorted.get(base)));
        }
        return OptionalLong.of(sorted.get(base));
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

    public static class Stopwatch {

        private String id;
        private com.google.common.base.Stopwatch stopwatch;

        private Stopwatch(final String id, final com.google.common.base.Stopwatch stopwatch) {
            this.id = id;
            this.stopwatch = stopwatch;
        }

        public static Stopwatch of(final String id, final com.google.common.base.Stopwatch stopwatch) {
            return new Stopwatch(id, stopwatch);
        }

        private String getId() {
            return id;
        }

        private com.google.common.base.Stopwatch getStopwatch() {
            return this.stopwatch;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Stopwatch that = (Stopwatch) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, stopwatch);
        }

        @Override
        public String toString() {
            return "Stopwatch{" +
                    "id='" + id + '\'' +
                    ", stopwatch=" + stopwatch +
                    '}';
        }
    }
}
