package logmonad;

import com.google.common.base.Stopwatch;
import logmonad.util.ListFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class TimerCollector {

    final private Map<String, List<Stopwatch>> stopwatches;

    private TimerCollector(final Map<String, List<Stopwatch>> stopwatches) {
        this.stopwatches = stopwatches;
    }

    public static TimerCollector of(final String timerName, final Stopwatch stopwatch) {
        return new TimerCollector(Collections.singletonMap(timerName, Collections.singletonList(stopwatch)));
    }

    public static TimerCollector empty() {
        return new TimerCollector(Collections.emptyMap());
    }

    public TimerCollector append(final TimerCollector other) {
        final Map<String, List<Stopwatch>> newTimers =
                Stream.concat(stopwatches.entrySet().stream(), other.stopwatches.entrySet().stream())
                        .collect(
                                toMap(Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        ListFunction::concat));
        return new TimerCollector(newTimers);
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
        final TimerCollector that = (TimerCollector) runnable;
        return Objects.equals(stopwatches.keySet(), that.stopwatches.keySet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopwatches);
    }

    public List<Stopwatch> get(final String name) {
        return stopwatches.getOrDefault(name, Collections.emptyList());
    }

    public long elapsed(final String name, final TimeUnit timeUnit) {
        return get(name)
                .stream()
                .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                .sum();
    }
}
