package logmonad;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class Truc {

    public static final Truc EMPTY = new Truc("", Collections.emptyList());
    private String name;
    private List<Stopwatch> stopwatches;

    private Truc(String name, List<Stopwatch> stopwatches) {
        this.name = name;
        this.stopwatches = stopwatches;
    }

    public static Truc of(final String name, final Stopwatch stopwatch) {
        return new Truc(name, Collections.singletonList(stopwatch));
    }

    public Truc append(Truc other) {
        return this.equals(EMPTY) ? other :
                new Truc(this.name, ImmutableList.copyOf(Iterables.concat(stopwatches, other.stopwatches)));
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
        final Truc truc = (Truc) o;
        return Objects.equals(name, truc.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Truc{" +
                "name='" + name + '\'' +
                ", stopwatches=" + stopwatches +
                '}';
    }
}
