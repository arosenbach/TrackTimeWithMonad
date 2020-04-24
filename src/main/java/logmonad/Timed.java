package logmonad;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Timed<T> {
    private T value;
    private Set<NamedStopwatch> stopwatches;

    private Timed(T value, final List<NamedStopwatch> stopwatches) {
        this.value = value;
        this.stopwatches = ImmutableSet.copyOf(stopwatches);
    }

    public static <U> Timed<U> of(U value, final NamedStopwatch stopwatch) {
        return new Timed<>(value, Collections.singletonList(stopwatch));
    }

    public static <U> Timed<Function<U, U>> of(final Function<U, U> f) {
        return new Timed<>(f, Collections.emptyList());
    }

    public T get() {
        return this.value;
    }

    public long elapsed(final TimeUnit timeUnit) {
        return this.stopwatches.stream()
                .mapToLong(s -> s.elapsed(timeUnit))
                .sum();
    }

    public <U> Timed<U> flatMap(Function<T, Timed<U>> mapper) {
        final Timed<U> mapped = mapper.apply(value);
        final ImmutableList<NamedStopwatch> newStopwatches = ImmutableList.copyOf(Iterables.concat(this.stopwatches, mapped.stopwatches));
        return new Timed<>(mapped.value, newStopwatches);
    }

    public <U> Timed<U> append(BiFunction<T, U, U> append, final Timed<U> other) {
        return new Timed<>(append.apply(this.value, other.value), ImmutableList.copyOf(Iterables.concat(stopwatches, other.stopwatches)));
    }

    public Set<NamedStopwatch> getStopwatches() {
        return this.stopwatches;
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
