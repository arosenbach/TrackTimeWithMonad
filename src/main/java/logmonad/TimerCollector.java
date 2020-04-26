package logmonad;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class TimerCollector {
//Monoid

    //Map<String, List<Long>> { "name" -> [25,34] }

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
                                        this::concatList));
        return new TimerCollector(newTimers);
    }

    private <U> List<U> concatList(final List<U> listA, final List<U> listB) {
        return Stream.concat(listA.stream(), listB.stream()).collect(toList());
    }


    @Override
    public String toString() {
        return "TimerCollector{" +
                "timers=" + timers +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TimerCollector that = (TimerCollector) o;
        return Objects.equals(timers, that.timers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timers);
    }

    public List<Long> get(final String name) {
        return timers.getOrDefault(name, Collections.emptyList());
    }
}
