package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DisplayName("TimerCollector")
class TimerCollectorTest {

    @Nested
    @DisplayName("TimerCollector::elapsed")
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

            final TimerCollector sut = stopwatches.stream()
                    .map(s -> TimerCollector.of("Acme", s))
                    .reduce(TimerCollector.empty(), TimerCollector::append);

            final long elapsedTime = sut.elapsed("Acme", TimeUnit.NANOSECONDS);
            assertEquals(expectedElapsedTime, elapsedTime);
        }
    }

    @Nested
    @DisplayName("is a monoid with TimerCollector::append")
    class Monoid {

        @Test
        @DisplayName("equality by value")
        void equals() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", Stopwatch.createStarted());
            final TimerCollector namedStopwatch2 = TimerCollector.of("Acme", Stopwatch.createStarted());
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("right identity: x <> mempty = x)")
        void rightIdentity() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", Stopwatch.createStarted());
            final TimerCollector namedStopwatch2 = namedStopwatch1.append(TimerCollector.empty());
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        void leftIdentity() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", Stopwatch.createStarted());
            final TimerCollector namedStopwatch2 = TimerCollector.empty().append(namedStopwatch1);
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("associativity")
        void associativity() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", Stopwatch.createStarted());
            final TimerCollector namedStopwatch2 = TimerCollector.of("Acme2", Stopwatch.createStarted());
            final TimerCollector namedStopwatch3 = TimerCollector.of("Acme", Stopwatch.createStarted());
            assertEquals((namedStopwatch1.append(namedStopwatch2)).append(namedStopwatch3), namedStopwatch1.append((namedStopwatch2).append(namedStopwatch3)));
        }
    }
}