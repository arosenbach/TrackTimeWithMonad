package logmonad;

import logmonad.util.ListFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class TimerCollector {

    final private Map<String, List<Long>> timers;

    private TimerCollector(final Map<String, List<Long>> timers) {
        this.timers = timers;
    }

    public static TimerCollector of(final String timerName, final long timers) {
        return new TimerCollector(Collections.singletonMap(timerName, Collections.singletonList(timers)));
    }

    public static TimerCollector empty() {
        return new TimerCollector(Collections.emptyMap());
    }

    public TimerCollector append(final TimerCollector other) {
        final Map<String, List<Long>> newTimers =
                Stream.concat(timers.entrySet().stream(), other.timers.entrySet().stream())
                        .collect(
                                toMap(Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        ListFunction::concat));
        return new TimerCollector(newTimers);
    }


    @Override
    public String toString() {
        return "TimerCollector{" +
                "timers=" + timers +
                '}';
    }

    @Override
    public boolean equals(final Object runnable) {
        if (this == runnable) return true;
        if (runnable == null || getClass() != runnable.getClass()) return false;
        final TimerCollector that = (TimerCollector) runnable;
        return Objects.equals(timers.keySet(), that.timers.keySet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(timers);
    }

    public List<Long> get(final String name) {
        return timers.getOrDefault(name, Collections.emptyList());
    }
}
