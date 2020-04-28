package logmonad;

import com.google.common.base.Stopwatch;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Timed<A> {

    private TimerCollector timerCollector;
    private A value;

    private Timed(A value, TimerCollector timerCollector) {
        this.timerCollector = timerCollector;
        this.value = value;
    }

    public static <A> Timed<A> of(A value, TimerCollector timerCollector) {
        return new Timed<>(value, timerCollector);
    }

    public static <A> Timed<A> empty(A emptyValue) {
        return new Timed<>(emptyValue, TimerCollector.empty());
    }

    public static <A, B> Function<A, Timed<B>> lift(final String name, final Function<A, B> function) {
        return (x) -> {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final B value = function.apply(x);
            stopwatch.stop();
            return Timed.of(value, TimerCollector.of(name, stopwatch));
        };
    }

    public static <A, B, C> BiFunction<A, B, Timed<C>> lift(final String name, final BiFunction<A, B, C> biFunction) {
        return (arg1, arg2) -> {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final C value = biFunction.apply(arg1, arg2);
            stopwatch.stop();
            return Timed.of(value, TimerCollector.of(name, stopwatch));
        };
    }

    public static <A> Supplier<Timed<A>> lift(final String name, final Supplier<A> supplier) {
        return () -> {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            final A value = supplier.get();
            stopwatch.stop();
            return Timed.of(value, TimerCollector.of(name, stopwatch));
        };
    }


    public <B> Timed<B> flatMap(Function<A, Timed<B>> f) {
        Timed<B> mappedTimed = f.apply(value);
        return new Timed<>(mappedTimed.value, timerCollector.append(mappedTimed.timerCollector));
    }

    public <B> Timed<A> append(final Timed<B> other, BiFunction<A, B, A> mergeFunction) {
        return Timed.of(mergeFunction.apply(value, other.value), this.timerCollector.append(other.timerCollector));
    }

    public A getValue() {
        return value;
    }

    public List<Stopwatch> getStopwatches(String name) {
        return timerCollector.get(name);
    }

    @Override
    public String toString() {
        return "Timed{" +
                "timerCollector=" + timerCollector +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(final Object runnable) {
        if (this == runnable) return true;
        if (runnable == null || getClass() != runnable.getClass()) return false;
        final Timed<?> timed = (Timed<?>) runnable;
        return Objects.equals(timerCollector, timed.timerCollector) &&
                Objects.equals(value, timed.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timerCollector, value);
    }
}
