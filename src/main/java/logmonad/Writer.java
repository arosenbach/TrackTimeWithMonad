package logmonad;

import java.util.List;
import java.util.function.Function;

public class Writer<A> {

    private TimerCollector logs;
    private A value;

    private Writer(TimerCollector logs, A value) {
        this.logs = logs;
        this.value = value;
    }

    public static <A> Writer<A> of(TimerCollector log, A value) {
        return new Writer<>(log, value);
    }

    public static <A> Writer<A> empty(A emptyValue) {
        return new Writer<>(TimerCollector.empty(), emptyValue);
    }

    public <B> Writer<B> map(Function<A, B> f) {
        return new Writer<>(logs, f.apply(value));
    }

    public <B> Writer<B> flatMap(Function<A, Writer<B>> f) {
        Writer<B> mappedWriter = f.apply(value);
        return new Writer<>(logs.append(mappedWriter.logs), mappedWriter.value);
    }

    public <U> Writer<U> append(final TimerCollector timerCollector, U value) {
        return Writer.of(this.logs.append(timerCollector), value);
    }

    public TimerCollector getLogs() {
        return logs;
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
