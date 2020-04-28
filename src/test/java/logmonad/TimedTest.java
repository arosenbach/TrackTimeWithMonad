package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Timed")
class TimedTest {

    @Nested
    @DisplayName("is a monad")
    class Monad {

        @Test
        @DisplayName("equality by value")
        void equals() {
            final Timed<Integer> timedX = Timed.of(TimerCollector.of("timedX", Stopwatch.createStarted()), 41);
            final Timed<Integer> timedY = Timed.of(TimerCollector.of("timedX", Stopwatch.createStarted()), 41);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("right identity: m >>= unit ≡ m)")
        void rightIdentity() {
            final Timed<Integer> timedX = Timed.of(TimerCollector.of("timedX", Stopwatch.createStarted()), 41);

            final Function<Integer, Timed<Integer>> unit = val -> Timed.of(TimerCollector.empty(), val);
            assertEquals(timedX.flatMap(unit), timedX);
        }

        @Test
        @DisplayName("left identity: (unit x) >>= f ≡ f x)")
        void leftIdentity() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", Stopwatch.createStarted());
            final TimerCollector namedStopwatch2 = TimerCollector.empty().append(namedStopwatch1);
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("associativity (m >>= f) >>= g ≡ m >>= (x -> f x >>= g)")
        void associativity() {
            final Timed<Integer> timedX = Timed.of(TimerCollector.of("timedx", Stopwatch.createStarted()), 20);

            final Function<Integer, Timed<Integer>> f = x -> Timed.of(TimerCollector.of("x*2", Stopwatch.createStarted()), x * 2);
            final Function<Integer, Timed<Integer>> g = x -> Timed.of(TimerCollector.of("x+1", Stopwatch.createStarted()), x + 1);

            assertEquals((timedX.flatMap(g)).flatMap(f), timedX.flatMap(x -> g.apply(x).flatMap(f)));
        }
    }

}