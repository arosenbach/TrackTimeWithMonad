package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("NamedStopwatch")
class NamedStopwatchTest {

    @Nested
    @DisplayName("NamedStopwatch::elapsed")
    class Elapsed {

        @Test
        @DisplayName("provides the sum of n stopwatches")
        void sumOfStopwatches() {
            final List<Stopwatch> stopwatches = Arrays.asList(Stopwatch.createStarted(),
                    Stopwatch.createStarted(),
                    Stopwatch.createStarted(),
                    Stopwatch.createStarted());
            final long expectedElapsedTime = stopwatches.stream()
                    .map(Stopwatch::stop)
                    .mapToLong(s -> s.elapsed(TimeUnit.NANOSECONDS))
                    .sum();

            final NamedStopwatch sut = stopwatches.stream()
                    .map(s -> NamedStopwatch.of("Acme", s))
                    .reduce(NamedStopwatch.EMPTY, NamedStopwatch::append);

            final long elapsedTime = sut.elapsed(TimeUnit.NANOSECONDS);
            assertEquals(expectedElapsedTime, elapsedTime);
        }
    }


    @Nested
    @DisplayName("is a monoid with NamedStopwatch::append")
    class Monoid {

        @Test
        @DisplayName("equality by value")
        void equals() {
            final NamedStopwatch namedStopwatch1 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            final NamedStopwatch namedStopwatch2 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("right identity: x <> mempty = x)")
        void rightIdentity() {
            final NamedStopwatch namedStopwatch1 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            final NamedStopwatch namedStopwatch2 = namedStopwatch1.append(NamedStopwatch.EMPTY);
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        void leftIdentity() {
            final NamedStopwatch namedStopwatch1 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            final NamedStopwatch namedStopwatch2 = NamedStopwatch.EMPTY.append(namedStopwatch1);
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("associativity")
        void associativity() {
            final NamedStopwatch namedStopwatch1 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            final NamedStopwatch namedStopwatch2 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            final NamedStopwatch namedStopwatch3 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            assertEquals((namedStopwatch1.append(namedStopwatch2)).append(namedStopwatch3), namedStopwatch1.append((namedStopwatch2).append(namedStopwatch3)));
        }
    }
}