package timedmonad;

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
//
//    @Nested
//    @DisplayName("NamedStopwatch::elapsed")
//    class Elapsed {
//
//        @Test
//        @DisplayName("provides the sum of n stopwatches")
//        void sumOfStopwatches() {
//            final List<Stopwatch> stopwatches = Arrays.asList(Stopwatch.createStarted(),
//                    Stopwatch.createStarted(),
//                    Stopwatch.createStarted(),
//                    Stopwatch.createStarted());
//            final long expectedElapsedTime = stopwatches.stream()
//                    .map(Stopwatch::stop)
//                    .mapToLong(s -> s.elapsed(TimeUnit.NANOSECONDS))
//                    .sum();
//
//            final Timed.NamedStopwatch sut = stopwatches.stream()
//                    .map(s -> Timed.NamedStopwatch.of("Acme", s))
//                    .reduce(Timed.NamedStopwatch.empty(), Timed.NamedStopwatch::append);
//
//            final long elapsedTime = sut.elapsed("Acme", TimeUnit.NANOSECONDS);
//            assertEquals(expectedElapsedTime, elapsedTime);
//        }
//    }
//
//    @Nested
//    @DisplayName("is a monoid with NamedStopwatch::append")
//    class Monoid {
//
//        @Test
//        @DisplayName("equality by value")
//        void equals() {
//            final Timed.NamedStopwatch namedStopwatch1 = Timed.NamedStopwatch.of("Acme", Stopwatch.createStarted());
//            final Timed.NamedStopwatch namedStopwatch2 = Timed.NamedStopwatch.of("Acme", Stopwatch.createStarted());
//            assertEquals(namedStopwatch1, namedStopwatch2);
//        }
//
//        @Test
//        @DisplayName("right identity: x <> mempty = x)")
//        void rightIdentity() {
//            final Timed.NamedStopwatch namedStopwatch1 = Timed.NamedStopwatch.of("Acme", Stopwatch.createStarted());
//            final Timed.NamedStopwatch namedStopwatch2 = namedStopwatch1.append(Timed.NamedStopwatch.empty());
//            assertEquals(namedStopwatch1, namedStopwatch2);
//        }
//
//        @Test
//        @DisplayName("left identity: mempty <> x = x)")
//        void leftIdentity() {
//            final Timed.NamedStopwatch namedStopwatch1 = Timed.NamedStopwatch.of("Acme", Stopwatch.createStarted());
//            final Timed.NamedStopwatch namedStopwatch2 = Timed.NamedStopwatch.empty().append(namedStopwatch1);
//            assertEquals(namedStopwatch1, namedStopwatch2);
//        }
//
//        @Test
//        @DisplayName("associativity")
//        void associativity() {
//            final Timed.NamedStopwatch namedStopwatch1 = Timed.NamedStopwatch.of("Acme", Stopwatch.createStarted());
//            final Timed.NamedStopwatch namedStopwatch2 = Timed.NamedStopwatch.of("Acme2", Stopwatch.createStarted());
//            final Timed.NamedStopwatch namedStopwatch3 = Timed.NamedStopwatch.of("Acme", Stopwatch.createStarted());
//            assertEquals((namedStopwatch1.append(namedStopwatch2)).append(namedStopwatch3), namedStopwatch1.append((namedStopwatch2).append(namedStopwatch3)));
//        }
//    }
}