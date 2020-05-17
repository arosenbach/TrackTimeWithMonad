package timedmonad;

import com.google.common.testing.FakeTicker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import timedmonad.Timed.Stopwatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Timed")
class TimedTest {

    @Nested
    @DisplayName("Timed::equals")
    class Equals {

        @ParameterizedTest(name = "Timed({0},\"{1}\") ≠ Timed({2},\"{3}\")")
        @CsvSource({"41,timedX, 42, timedX", "42,timedX, 42, timedY"})
        void notEquals(int valueX, String idX, int valueY, String idY) {
            final Timed<Integer> timedX = Timed.of(valueX, Stopwatch.of(idX, makeStopwatch()));
            final Timed<Integer> timedY = Timed.of(valueY, Stopwatch.of(idY, makeStopwatch()));
            assertNotEquals(timedX, timedY);
        }

        @Test
        @DisplayName("compares only values and stopwatch ids")
        void equals() {
            final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", makeStopwatch()));
            final Timed<Integer> timedY = Timed.of(41, Stopwatch.of("timedX", makeStopwatch()));
            assertEquals(timedX, timedY);
        }


        @Test
        @DisplayName("compares only values and stopwatch ids - multiple stopwatches")
        void equalsMultiple() {
            final Timed<Integer> timedX = Timed.of("X", Stopwatch.of("foo", makeStopwatch()))
                    .flatMap(() -> Timed.of(42, Stopwatch.of("bar", makeStopwatch())));
            final Timed<Integer> timedY = Timed.of("Y", Stopwatch.of("foo", makeStopwatch()))
                    .flatMap(() -> Timed.of(42, Stopwatch.of("bar", makeStopwatch())));
            assertEquals(timedX, timedY);
        }
    }

    @Nested
    @DisplayName("TimedTest::elapsed")
    class Elapsed {
        @Test
        @DisplayName("with no 'id' parameter returns total time")
        void totalElapsed() {
            final Timed<Integer> timed = makeTimed("timed1", 42, 300)
                    .flatMap(() -> makeTimed("timed2", 42, 100));
            assertEquals(300 + 100,
                    timed.elapsed(TimeUnit.MILLISECONDS));
        }

        @Test
        @DisplayName("with 'id' parameter returns the sum the times of the corresponding stopwatch")
        void elapsed() {
            final Timed<Integer> timed = makeTimed("timed", 42, 300)
                    .flatMap(() -> makeTimed("timed", 42, 100));
            final OptionalLong actual = timed.elapsed("timed", TimeUnit.MILLISECONDS);
            assertEquals(300 + 100,
                    actual.orElse(0L));
        }

        @Test
        @DisplayName("invalid 'id' parameter returns empty result")
        void elapsedUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42, 300);
            final OptionalLong actual = timed.elapsed("bar", TimeUnit.MILLISECONDS);
            assertFalse(actual.isPresent());
        }
    }

    @Nested
    @DisplayName("TimedTest::average ")
    class Average {
        @Test
        @DisplayName("returns the average time of a stopwatch")
        void average() {
            final Timed<Integer> timed = makeTimed("timed", 42, 100)
                    .flatMap(() -> makeTimed("timed", 42, 200))
                    .flatMap(() -> makeTimed("timed", 42, 100))
                    .flatMap(() -> makeTimed("timed", 42, 100))
                    .flatMap(() -> makeTimed("timed", 42, 100));
            final OptionalDouble actual = timed.average("timed", TimeUnit.MILLISECONDS);
            final int expected = (100 + 200 + 100 + 100 + 100) / 5;
            assertEquals(expected,
                    (long) actual.orElse(0));
        }


        @Test
        @DisplayName("invalid 'id' parameter returns empty result")
        void averageUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42, 300);
            final OptionalDouble actual = timed.average("bar", TimeUnit.MILLISECONDS);
            assertFalse(actual.isPresent());
        }
    }

    @Nested
    @DisplayName("Timed::min, Timed::max")
    class MinMax {
        @Test
        void min() {
            final Timed<Integer> timed = makeTimed("timed", 42, 100)
                    .flatMap(() -> makeTimed("timed", 42, 300));
            final OptionalLong actual = timed.min("timed", TimeUnit.MILLISECONDS);
            assertEquals(100,
                    actual.orElse(0));
        }

        @Test
        @DisplayName("min: invalid 'id' parameter returns empty result")
        void minUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42, 300);
            final OptionalLong actual = timed.min("bar", TimeUnit.MILLISECONDS);
            assertFalse(actual.isPresent());
        }

        @Test
        void max() {
            final Timed<Integer> timed = makeTimed("timed", 42, 100)
                    .flatMap(() -> makeTimed("timed", 42, 300));
            final OptionalLong actual = timed.max("timed", TimeUnit.MILLISECONDS);
            assertEquals(300,
                    actual.orElse(0));
        }


        @Test
        @DisplayName("max: invalid 'id' parameter returns empty result")
        void maxUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42, 300);
            final OptionalLong actual = timed.max("bar", TimeUnit.MILLISECONDS);
            assertFalse(actual.isPresent());
        }
    }

    @Nested
    @DisplayName("Timed::count")
    class Count {
        @Test
        void count() {
            final Timed<String> timed = makeTimed("timed", 42, 100)
                    .flatMap(() -> makeTimed("timed", "foobar", 300));
            final OptionalInt actual = timed.count("timed");
            assertEquals(OptionalInt.of(2), actual);
        }

        @Test
        @DisplayName("count: invalid 'id' parameter returns empty result")
        void countUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42, 300);
            final OptionalInt actual = timed.count("bar");
            assertFalse(actual.isPresent());
        }
    }


    @Nested
    @DisplayName("Timed::percentile")
    class Percentile {

        @ParameterizedTest(name = "p{0}")
        @CsvSource({"10,100", "25,117", "50,350", "75, 537", "90, 670"})
        void percentile(int p, long expected) {
            final Timed<String> timed = makeTimed("timed", 42, 100)
                    .flatMap(() -> makeTimed("timed", "foobar", 200))
                    .flatMap(() -> makeTimed("timed", "foobar", 300))
                    .flatMap(() -> makeTimed("timed", "foobar", 400))
                    .flatMap(() -> makeTimed("timed", "foobar", 500))
                    .flatMap(() -> makeTimed("timed", "foobar", 600));
            final OptionalLong actual = timed.percentile(p, "timed", TimeUnit.SECONDS);
            assertEquals(expected,
                    actual.orElse(0));
        }

        @Test
        @DisplayName("percentile: invalid 'id' parameter returns empty result")
        void percentileUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42, 300);
            final OptionalLong actual = timed.percentile(50, "bar", TimeUnit.MILLISECONDS);
            assertFalse(actual.isPresent());
        }

    }

    @Nested
    @DisplayName("is a monoid with Timed::append")
    class Monoid {

        @Test
        @DisplayName("right identity: x <> mempty = x)")
        void rightIdentity() {
            final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", makeStopwatch()));
            final Timed<Integer> timedY = timedX.append(Timed.empty(0), Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        void leftIdentity() {
            final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", makeStopwatch()));
            final Timed<Integer> timedY = Timed.empty(0).append(timedX, Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("associativity: (x <> y) <> z = x <> (y <> z)")
        void associativity() {
            final Timed<Integer> timed1 = Timed.of(2, Stopwatch.of("timed1", makeStopwatch()));
            final Timed<Integer> timed2 = Timed.of(3, Stopwatch.of("timed1", makeStopwatch()));
            final Timed<Integer> timed3 = Timed.of(4, Stopwatch.of("timed1", makeStopwatch()));
            assertEquals((timed1.append(timed2, Integer::sum)).append(timed3, Integer::sum),
                    timed1.append((timed2.append(timed3, Integer::sum)), Integer::sum));
        }
    }


    @Nested
    @DisplayName("is a monad with Timed::flatMap")
    class Monad {

        @Test
        @DisplayName("right identity: m >>= unit ≡ m)")
        void rightIdentity() {
            final Timed<Integer> timedX = Timed.of(41, Stopwatch.of("timedX", makeStopwatch()));
            assertEquals(timedX.flatMap(Timed::empty), timedX);
        }

        @Test
        @DisplayName("left identity: (unit x) >>= f ≡ f x)")
        void leftIdentity() {
            final Timed<Integer> timedX = Timed.of(42, Stopwatch.of("add1", makeStopwatch()));
            final Function<Integer, Timed<Integer>> add1 = x -> Timed.of(x + 1, Stopwatch.of("add1", makeStopwatch()));
            assertEquals(Timed.empty(41).flatMap(add1), timedX);
        }

        @Test
        @DisplayName("associativity (m >>= f) >>= g ≡ m >>= (x -> f x >>= g)")
        void associativity() {
            final Timed<Integer> timedX = Timed.of(20, Stopwatch.of("timedx", makeStopwatch()));

            final Function<Integer, Timed<Integer>> f = x -> Timed.of(x * 2, Stopwatch.of("x*2", makeStopwatch()));
            final Function<Integer, Timed<Integer>> g = x -> Timed.of(x + 1, Stopwatch.of("x+1", makeStopwatch()));

            assertEquals((timedX.flatMap(g)).flatMap(f),
                    timedX.flatMap(x -> g.apply(x).flatMap(f)));
        }
    }


    @Nested
    @DisplayName("Timed::lift")
    class Lift {

        static final String STOPWATCH_ID = "anId";

        private int returns42In300ms() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        }

        private Timed<Integer> returnsTimed42In300ms() {
            return makeTimed(STOPWATCH_ID, 42, 300);
        }

        @Test
        @DisplayName("transforms a Suplier<T> into of Supplier<Timed<T>>")
        void supplier() {
            final Timed<Integer> actual = Timed.lift("anId", (Supplier<Integer>) this::returns42In300ms).get();
            final Timed<Integer> expected = this.returnsTimed42In300ms();
            assertAll(
                    () -> assertEquals(expected, actual),
                    () -> assertEquals(
                            roundMillis(expected.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L)),
                            roundMillis(actual.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L)))
            );

        }


        private int returns42In300ms(final int x) {
            return returns42In300ms();
        }

        private Timed<Integer> returnsTimed42In300ms(final int i) {
            return returnsTimed42In300ms();
        }

        @Test
        @DisplayName("transforms a Function<A,T> into of Function<A,<Timed<T>>")
        void function() {
            final Timed<Integer> actual = Timed.lift("anId", (Function<Integer, Integer>) this::returns42In300ms).apply(42);
            final Timed<Integer> expected = this.returnsTimed42In300ms(42);
            assertAll(
                    () -> assertEquals(expected, actual),
                    () -> assertEquals(
                            roundMillis(expected.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L)),
                            roundMillis(actual.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L)))
            );

        }

        private int returns42In300ms(final int x, final int y) {
            return returns42In300ms();
        }

        private Timed<Integer> returnsTimed42In300ms(final int x, final int y) {
            return returnsTimed42In300ms();
        }

        @Test
        @DisplayName("transforms a BiFunction<A,B,T> into of BiFunction<A,B,<Timed<T>>")
        void biFunction() {
            final Timed<Integer> actual = Timed.lift("anId", (BiFunction<Integer, Integer, Integer>) this::returns42In300ms).apply(42, 42);
            final Timed<Integer> expected = this.returnsTimed42In300ms(42, 42);
            assertAll(
                    () -> assertEquals(expected, actual),
                    () -> assertEquals(
                            roundMillis(expected.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L)),
                            roundMillis(actual.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L)))
            );

        }

        private float roundMillis(final long millis) {
            final BigDecimal bd = new BigDecimal(millis / 1000F).setScale(2, RoundingMode.HALF_UP);
            return bd.floatValue();
        }

    }

    private <T> Timed<T> makeTimed(final String id, final T value, final int millis) {
        final com.google.common.base.Stopwatch stopwatch = makeStopwatch(millis);
        stopwatch.stop();
        return Timed.of(value, Stopwatch.of(id, stopwatch));
    }

    private com.google.common.base.Stopwatch makeStopwatch() {
        return makeStopwatch(111);
    }

    private com.google.common.base.Stopwatch makeStopwatch(long millis) {
        final FakeTicker ticker = new FakeTicker();
        final com.google.common.base.Stopwatch stopwatch = com.google.common.base.Stopwatch.createStarted(ticker);
        ticker.advance(millis, TimeUnit.MILLISECONDS);
        return stopwatch;
    }

}