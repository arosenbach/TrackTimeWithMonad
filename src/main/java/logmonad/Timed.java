package logmonad;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Timed<T> {
    private T value;
    private List<Truc> stopwatches;

    private Timed(T value, final List<Truc> stopwatches) {
        this.value = value;
        this.stopwatches = ImmutableList.copyOf(stopwatches);
    }

    public static <U> Timed<U> of(U value, final Truc stopwatch) {
        return new Timed<>(value, Collections.singletonList(stopwatch));
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
        return new Timed<>(mapped.value, ImmutableList.copyOf(Iterables.concat(stopwatches, mapped.stopwatches)));
    }

    public <U> Timed<U> append(BiFunction<T, U, U> append, final Timed<U> other) {
        return new Timed<>(append.apply(this.value, other.value), ImmutableList.copyOf(Iterables.concat(stopwatches, other.stopwatches)));
    }

    public List<Truc> getStopwatches() {
        return this.stopwatches;
    }
}
