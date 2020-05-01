package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Timed")
class TimedTest {

    @Test
    @DisplayName("is equal by value")
    void equals() {
        final Timed<Integer> timedX = Timed.of(41, TimerCollector.of("timedX", Stopwatch.createStarted()));
        final Timed<Integer> timedY = Timed.of(41, TimerCollector.of("timedX", Stopwatch.createStarted()));
        assertEquals(timedX, timedY);
    }

    @Nested
    @DisplayName("is a monoid with Timed::append")
    class Monoid {

        @Test
        @DisplayName("right identity: x <> mempty = x)")
        void rightIdentity() {
            final Timed<Integer> timedX = Timed.of(41, TimerCollector.of("timedX", Stopwatch.createStarted()));
            final Timed<Integer> timedY = timedX.append(Timed.empty(0), Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        void leftIdentity() {
            final Timed<Integer> timedX = Timed.of(41, TimerCollector.of("timedX", Stopwatch.createStarted()));
            final Timed<Integer> timedY = Timed.empty(0).append(timedX, Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("associativity")
        void associativity() {
            final Timed<Integer> timed1 = Timed.of(2, TimerCollector.of("timed1", Stopwatch.createStarted()));
            final Timed<Integer> timed2 = Timed.of(3, TimerCollector.of("timed1", Stopwatch.createStarted()));
            final Timed<Integer> timed3 = Timed.of(4, TimerCollector.of("timed1", Stopwatch.createStarted()));
            assertEquals((timed1.append(timed2, Integer::sum)).append(timed3, Integer::sum),
                    timed1.append((timed2.append(timed3, Integer::sum)), Integer::sum));
        }
    }


    @Nested
    @DisplayName("is a monad")
    class Monad {

        @Test
        @DisplayName("right identity: m >>= unit ≡ m)")
        void rightIdentity() {
            final Timed<Integer> timedX = Timed.of(41, TimerCollector.of("timedX", Stopwatch.createStarted()));

            final Function<Integer, Timed<Integer>> unit = val -> Timed.of(val, TimerCollector.empty());
            assertEquals(timedX.flatMap(unit), timedX);
        }

        @Test
        @DisplayName("left identity: (unit x) >>= f ≡ f x)")
        void leftIdentity() {
            final TimerCollector timed1 = TimerCollector.of("Acme", Stopwatch.createStarted());
            final TimerCollector timed2 = TimerCollector.empty().append(timed1);
            assertEquals(timed1, timed2);
        }

        @Test
        @DisplayName("associativity (m >>= f) >>= g ≡ m >>= (x -> f x >>= g)")
        void associativity() {
            final Timed<Integer> timedX = Timed.of(20, TimerCollector.of("timedx", Stopwatch.createStarted()));

            final Function<Integer, Timed<Integer>> f = x -> Timed.of(x * 2, TimerCollector.of("x*2", Stopwatch.createStarted()));
            final Function<Integer, Timed<Integer>> g = x -> Timed.of(x + 1, TimerCollector.of("x+1", Stopwatch.createStarted()));

            assertEquals((timedX.flatMap(g)).flatMap(f), timedX.flatMap(x -> g.apply(x).flatMap(f)));
        }
    }


    @Nested
    @DisplayName("Timed::trackTime")
    class TrackTimeMethod {

        private int returns42In300ms() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        }

        private Timed<Integer> returnsTimed42In300ms() {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopwatch.stop();
            return Timed.of(42, TimerCollector.of("supplier", stopwatch));
        }

        @Test
        @DisplayName("Timed.trackTime transforms a Suplier<T> into of Supplier<Timed<T>>")
        void supplier() {
            final Timed<Integer> actual = Timed.trackTime("supplier", this::returns42In300ms).get();
            final Timed<Integer> expected = this.returnsTimed42In300ms();
            assertEquals(actual, expected);
            assertEquals(roundedElapsedInMillis("supplier", actual),
                    roundedElapsedInMillis("supplier", expected));

        }

        private String roundedElapsedInMillis(final String name, final Timed<Integer> timed1) {
            return new DecimalFormat("0.0").format(timed1.getStopwatches(name).stream().mapToLong(s -> s.elapsed(TimeUnit.MILLISECONDS)).sum() / 1000F);
        }
    }

}