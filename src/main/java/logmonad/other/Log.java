package logmonad.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Log<T> {
    private T value;
    private List<String> trace;

    private Log(T value, List<String> newTrace) {
        this.value = value;
        this.trace = newTrace;
    }

    public static <U> Log<U> trace(U value, String log) {
        return new Log<>(value, Collections.singletonList(log + ": " + value));
    }

    public static <U> Log<U> unit(U value) {
        return new Log<>(value, new ArrayList<>());
    }

    public <U> Log<U> flatMap(Function<T, Log<U>> mapper) {
        final Log<U> mapped = mapper.apply(value);

        final ArrayList<String> newTrace = new ArrayList<String>() {{
            addAll(trace);
            addAll(mapped.trace);
        }};
        return new Log<>(mapped.value, newTrace);
    }

    public T getValue() {
        return value;
    }

    public List<String> getTrace() {
        return trace;
    }
}