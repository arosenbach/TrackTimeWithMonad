package timedmonad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import timedmonad.Timed.Stopwatch;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Timed")
class TimedTest {

    @Test
    @DisplayName("is equal by value")
    void equals() {
        final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", com.google.common.base.Stopwatch.createStarted()));
        final Timed<Integer> timedY = Timed.of(41, Stopwatch.of("timedX", com.google.common.base.Stopwatch.createStarted()));
        assertEquals(timedX, timedY);
    }

    @Nested
    @DisplayName("is a monoid with Timed::append")
    class Monoid {

        @Test
        @DisplayName("right identity: x <> mempty = x)")
        void rightIdentity() {
            final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", com.google.common.base.Stopwatch.createStarted()));
            final Timed<Integer> timedY = timedX.append(Timed.empty(0), Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        void leftIdentity() {
            final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", com.google.common.base.Stopwatch.createStarted()));
            final Timed<Integer> timedY = Timed.empty(0).append(timedX, Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("associativity")
        void associativity() {
            final Timed<Integer> timed1 = Timed.of(2, Stopwatch.of("timed1", com.google.common.base.Stopwatch.createStarted()));
            final Timed<Integer> timed2 = Timed.of(3, Stopwatch.of("timed1", com.google.common.base.Stopwatch.createStarted()));
            final Timed<Integer> timed3 = Timed.of(4, Stopwatch.of("timed1", com.google.common.base.Stopwatch.createStarted()));
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
            final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", com.google.common.base.Stopwatch.createStarted()));
            assertEquals(timedX.flatMap(Timed::empty), timedX);
        }

        @Test
        @DisplayName("left identity: (unit x) >>= f ≡ f x)")
        void leftIdentity() {
            final Timed<Integer> timedX = Timed.of(42, Stopwatch.of("add1", com.google.common.base.Stopwatch.createStarted()));
            final Function<Integer, Timed<Integer>> add1 = x -> Timed.of(x+1, Stopwatch.of("add1", com.google.common.base.Stopwatch.createStarted()));
            assertEquals(Timed.empty(41).flatMap(add1), timedX);
        }

        @Test
        @DisplayName("associativity (m >>= f) >>= g ≡ m >>= (x -> f x >>= g)")
        void associativity() {
            final Timed<Integer> timedX = Timed.of(20, Stopwatch.of("timedx", com.google.common.base.Stopwatch.createStarted()));

            final Function<Integer, Timed<Integer>> f = x -> Timed.of(x * 2, Stopwatch.of("x*2", com.google.common.base.Stopwatch.createStarted()));
            final Function<Integer, Timed<Integer>> g = x -> Timed.of(x + 1, Stopwatch.of("x+1", com.google.common.base.Stopwatch.createStarted()));

            assertEquals((timedX.flatMap(g)).flatMap(f), timedX.flatMap(x -> g.apply(x).flatMap(f)));
        }
    }


    @Nested
    @DisplayName("Timed::lift")
    class LiftMethod {

        public static final String STOPWATCH_NAME = "aName";

        private int returns42In300ms() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        }

        private Timed<Integer> returnsTimed42In300ms() {
            final com.google.common.base.Stopwatch stopwatch = com.google.common.base.Stopwatch.createStarted();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopwatch.stop();
            return Timed.of(42, Stopwatch.of(STOPWATCH_NAME, stopwatch));
        }

        @Test
        @DisplayName("Timed.lift transforms a Suplier<T> into of Supplier<Timed<T>>")
        void supplier() {
            final Timed<Integer> actual = Timed.lift("aName", (Supplier<Integer>) this::returns42In300ms).get();
            final Timed<Integer> expected = this.returnsTimed42In300ms();
            assertEquals(expected, actual);
            assertEquals(roundedElapsedInMillis("aName", expected),
                    roundedElapsedInMillis("aName", actual));

        }


        private int returns42In300ms(final int x) {
            return returns42In300ms();
        }

        private Timed<Integer> returnsTimed42In300ms(final int i) {
            return returnsTimed42In300ms();
        }

        @Test
        @DisplayName("Timed.lift transforms a Function<A,T> into of Function<A,<Timed<T>>")
        void function() {
            final Timed<Integer> actual = Timed.lift("aName", (Function<Integer, Integer>) this::returns42In300ms).apply(42);
            final Timed<Integer> expected = this.returnsTimed42In300ms(42);
            assertEquals(expected, actual);
            assertEquals(roundedElapsedInMillis("aName", expected),
                    roundedElapsedInMillis("aName", actual));

        }

        private int returns42In300ms(final int x, final int y) {
            return returns42In300ms();
        }

        private Timed<Integer> returnsTimed42In300ms(final int x, final int y) {
            return returnsTimed42In300ms();
        }

        @Test
        @DisplayName("Timed.lift transforms a BiFunction<A,B,T> into of BiFunction<A,B,<Timed<T>>")
        void biFunction() {
            final Timed<Integer> actual = Timed.lift("aName", (BiFunction<Integer, Integer, Integer>) this::returns42In300ms).apply(42, 42);
            final Timed<Integer> expected = this.returnsTimed42In300ms(42, 42);
            assertEquals(expected, actual);
            assertEquals(roundedElapsedInMillis("aName", expected),
                    roundedElapsedInMillis("aName", actual));

        }


        private String roundedElapsedInMillis(final String name, final Timed<Integer> timed1) {
            return new DecimalFormat("0.0").format(timed1.getStopwatches(name).stream().mapToLong(s -> s.elapsed(TimeUnit.MILLISECONDS)).sum() / 1000F);
        }
    }

}