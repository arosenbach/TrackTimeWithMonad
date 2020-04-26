package logmonad.old;

import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class Timed<T> {
    private T value;
    private Map<String, NamedStopwatch> stopwatches;

    private Timed(T value, final Map<String, NamedStopwatch> stopwatches) {
        this.value = value;
        this.stopwatches = stopwatches;
    }

    public static <U> Timed<U> of(U value, final NamedStopwatch stopwatch) {
        return new Timed<>(value, Collections.singletonMap(stopwatch.getName(), stopwatch));
    }

    public static <U> Timed<Function<U, U>> of(final Function<U, U> f) {
        return new Timed<>(f, Collections.emptyMap());
    }

    public T get() {
        return this.value;
    }

    public long elapsed(final TimeUnit timeUnit) {
        return this.stopwatches
                .values()
                .stream()
                .mapToLong(s -> s.elapsed(timeUnit))
                .sum();
    }

    public <U> Timed<U> flatMap(Function<T, Timed<U>> mapper) {
        final Timed<U> mapped = mapper.apply(value);
        final Map<String, NamedStopwatch> collect = Stream.concat(this.stopwatches.values().stream(), mapped.stopwatches.values().stream())
                .collect(toMap(NamedStopwatch::getName, Function.identity(), NamedStopwatch::append));
        return new Timed<>(mapped.value, collect);
    }

//    public <U> Timed<U> append(BiFunction<T, U, U> append, final Timed<U> other) {
//        return new Timed<>(append.apply(this.value, other.value), ImmutableList.copyOf(Iterables.concat(stopwatches.values(), other.stopwatches.values())));
//    }

    public Set<NamedStopwatch> getStopwatches() {
        return ImmutableSet.copyOf(this.stopwatches.values());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Timed<?> timed = (Timed<?>) o;
        return Objects.equals(value, timed.value) &&
                Objects.equals(stopwatches, timed.stopwatches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Timed{" +
                "value=" + value +
                ", stopwatches=" + stopwatches +
                '}';
    }
}
