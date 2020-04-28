package logmonad;

import com.google.common.base.Stopwatch;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    public <B> Timed<B> flatMap(Function<A, Timed<B>> f) {
        Timed<B> mappedTimed = f.apply(value);
        return new Timed<>(mappedTimed.value, timerCollector.append(mappedTimed.timerCollector));
    }

    public <B> Timed<A> append(final Timed<B> other, BiFunction<A,B,A> mergeFunction) {
        return Timed.of(mergeFunction.apply(value, other.value), this.timerCollector.append(other.timerCollector));
    }

    public A getValue() {
        return value;
    }

    public List<Stopwatch> getStopwatches(String name){
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
