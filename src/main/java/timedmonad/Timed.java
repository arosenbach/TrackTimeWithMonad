package timedmonad;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class Timed<A> {

    private final NamedStopwatch namedStopwatch;
    private final A value;

    private Timed(A value, NamedStopwatch namedStopwatch) {
        this.namedStopwatch = namedStopwatch;
        this.value = value;
    }

    public static <A> Timed<A> of(A value, NamedStopwatch namedStopwatch) {
        return new Timed<>(value, namedStopwatch);
    }

    public static <A> Timed<A> empty(A emptyValue) {
        return new Timed<>(emptyValue, NamedStopwatch.empty());
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
        return new Timed<>(mappedTimed.value, namedStopwatch.append(mappedTimed.namedStopwatch));
    }

    public <B> Timed<B> flatMap(Function<A, Timed<B>> f) {
        Timed<B> mappedTimed = f.apply(value);
        return new Timed<>(mappedTimed.value, namedStopwatch.append(mappedTimed.namedStopwatch));
    }

    public <B> Timed<A> append(final Timed<B> other, BiFunction<A, B, A> mergeFunction) {
        return Timed.of(mergeFunction.apply(value, other.value), this.namedStopwatch.append(other.namedStopwatch));
    }

    public A getValue() {
        return value;
    }

    public List<Stopwatch> getStopwatches(String name) {
        return namedStopwatch.get(name);
    }

    @Override
    public String toString() {
        return "Timed{" +
                "timerCollector=" + namedStopwatch +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(final Object runnable) {
        if (this == runnable) return true;
        if (runnable == null || getClass() != runnable.getClass()) return false;
        final Timed<?> timed = (Timed<?>) runnable;
        return Objects.equals(namedStopwatch, timed.namedStopwatch) &&
                Objects.equals(value, timed.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namedStopwatch, value);
    }

    public static class NamedStopwatch {

        final private Map<String, List<Stopwatch>> stopwatches;

        private NamedStopwatch(final Map<String, List<Stopwatch>> stopwatches) {
            this.stopwatches = ImmutableMap.copyOf(stopwatches);
        }

        public static NamedStopwatch of(final String timerName, final Stopwatch stopwatch) {
            return new NamedStopwatch(Collections.singletonMap(timerName, Collections.singletonList(stopwatch)));
        }

        private static NamedStopwatch empty() {
            return new NamedStopwatch(Collections.emptyMap());
        }

        private NamedStopwatch append(final NamedStopwatch other) {
            final Map<String, List<Stopwatch>> newTimers =
                    Stream.concat(stopwatches.entrySet().stream(), other.stopwatches.entrySet().stream())
                            .collect(
                                    toMap(Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            this::concatenateLists));
            return new NamedStopwatch(newTimers);
        }

        private <T> List<T> concatenateLists(List<T> listA, List<T> listB) {
            return Stream.concat(listA.stream(), listB.stream()).collect(toList());
        }

        @Override
        public String toString() {
            return "TimerCollector{" +
                    "stopwatches=" + stopwatches +
                    '}';
        }

        @Override
        public boolean equals(final Object runnable) {
            if (this == runnable) return true;
            if (runnable == null || getClass() != runnable.getClass()) return false;
            final NamedStopwatch that = (NamedStopwatch) runnable;
            return Objects.equals(stopwatches.keySet(), that.stopwatches.keySet());
        }

        @Override
        public int hashCode() {
            return Objects.hash(stopwatches);
        }

        private List<Stopwatch> get(final String name) {
            return stopwatches.getOrDefault(name, Collections.emptyList());
        }

        private long elapsed(final String name, final TimeUnit timeUnit) {
            return get(name)
                    .stream()
                    .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                    .sum();
        }
    }
}
