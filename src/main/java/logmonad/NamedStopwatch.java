package logmonad;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;

public class NamedStopwatch {

    public static final NamedStopwatch EMPTY = new NamedStopwatch("", Collections.emptyList());
    private String name;
    private List<Stopwatch> stopwatches;

    private NamedStopwatch(String name, List<Stopwatch> stopwatches) {
        this.name = name;
        this.stopwatches = stopwatches;
    }

    public static NamedStopwatch of(final String name, final Stopwatch stopwatch) {
        return new NamedStopwatch(name, Collections.singletonList(stopwatch));
    }

    public NamedStopwatch append(NamedStopwatch other) {
        return this.equals(EMPTY) ? other :
                new NamedStopwatch(this.name, ImmutableList.copyOf(Iterables.concat(stopwatches, other.stopwatches)));
    }

    public long elapsed(final TimeUnit timeUnit) {
        return stopwatches.stream()
                .mapToLong(stopwatch -> stopwatch.elapsed(timeUnit))
                .sum();
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final NamedStopwatch that = (NamedStopwatch) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "NamedStopwatch{" +
                "name='" + name + '\'' +
                ", stopwatches=" + stopwatches +
                '}';
    }
}
