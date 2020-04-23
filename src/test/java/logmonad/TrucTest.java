package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Truc")
class TrucTest {

    @Nested
    @DisplayName("Truc::elapsed")
    class Elapsed {

        @Test
        @DisplayName("provides the sum of n stopwatches")
        public void sumOfStopwatches() {
            final List<Stopwatch> stopwatches = Arrays.asList(Stopwatch.createStarted(),
                    Stopwatch.createStarted(),
                    Stopwatch.createStarted(),
                    Stopwatch.createStarted());
            final long expectedElapsedTime = stopwatches.stream()
                    .map(Stopwatch::stop)
                    .mapToLong(s -> s.elapsed(TimeUnit.NANOSECONDS))
                    .sum();

            final Truc sut = stopwatches.stream()
                    .map(s -> Truc.of("acme", s))
                    .reduce(Truc.EMPTY, Truc::append);

            final long elapsedTime = sut.elapsed(TimeUnit.NANOSECONDS);
            assertEquals(expectedElapsedTime, elapsedTime);
        }
    }


    @Nested
    @DisplayName("is a monoid with Truc::append")
    class Monoid {

        @Test
        @DisplayName("equality by value")
        public void equals() throws Exception {
            final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
            final Truc truc2 = Truc.of("Acme", Stopwatch.createStarted());
            assertEquals(truc1, truc2);
        }

        @Test
        @DisplayName("right identity: x <> mempty = x)")
        public void rightIdentity() {
            final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
            final Truc truc2 = truc1.append(Truc.EMPTY);
            assertEquals(truc1, truc2);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        public void lefttIdentity() {
            final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
            final Truc truc2 = Truc.EMPTY.append(truc1);
            assertEquals(truc1, truc2);
        }

        @Test
        @DisplayName("associativity")
        public void associativity() {
            final Truc truc1 = Truc.of("Acme", Stopwatch.createStarted());
            final Truc truc2 = Truc.of("Acme", Stopwatch.createStarted());
            final Truc truc3 = Truc.of("Acme", Stopwatch.createStarted());
            assertEquals((truc1.append(truc2)).append(truc3), truc1.append((truc2).append(truc3)));
        }
    }
}