package logmonad;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Timed<A> {

    private TimerCollector logs;
    private A value;

    private Timed(TimerCollector logs, A value) {
        this.logs = logs;
        this.value = value;
    }

    public static <A> Timed<A> of(TimerCollector log, A value) {
        return new Timed<>(log, value);
    }

    public static <A> Timed<A> empty(A emptyValue) {
        return new Timed<>(TimerCollector.empty(), emptyValue);
    }

    public <B> Timed<B> flatMap(Function<A, Timed<B>> f) {
        Timed<B> mappedTimed = f.apply(value);
        return new Timed<>(logs.append(mappedTimed.logs), mappedTimed.value);
    }

    public <U> Timed<U> append(final TimerCollector timerCollector, U value) {
        return Timed.of(this.logs.append(timerCollector), value);
    }

    public <B> Timed<A> append(final Timed<B> other, BiFunction<A,B,A> mergeFunction) {
        return Timed.of(this.logs.append(other.logs), mergeFunction.apply(value, other.value));
    }



    public A getValue() {
        return value;
    }

    public List<Long> getTimes(String name){
        return logs.get(name);
    }

    @Override
    public String toString() {
        return "Writer("+logs+", "+value+")";
    }
}
