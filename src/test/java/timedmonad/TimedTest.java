package timedmonad;

import com.google.common.testing.FakeTicker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import timedmonad.Timed.Stopwatch;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Timed")
class TimedTest {

    @Nested
    @DisplayName("Timed::equals")
    class Equals {

        @ParameterizedTest(name = "Timed({0},\"{1}\") ≠ Timed({2},\"{3}\")")
        @CsvSource({"41,timedX, 42, timedX", "42,timedX, 42, timedY"})
        void notEquals(int valueX, String idX, int valueY, String idY) {
            final Timed<Integer> timedX = makeTimed(idX, valueX);
            final Timed<Integer> timedY = makeTimed(idY, valueY);
            assertNotEquals(timedX, timedY);
        }

        @Test
        @DisplayName("compares only values and stopwatch ids")
        void equals() {
            final Timed<Integer> timedX = makeTimed("timedX", 41);
            final Timed<Integer> timedY = makeTimed("timedX", 41);
            assertEquals(timedX, timedY);
        }


        @Test
        @DisplayName("compares only values and stopwatch ids - multiple stopwatches")
        void equalsMultiple() {
            final Timed<Integer> timedX = makeTimed("foo", "X")
                    .flatMap(() -> makeTimed("bar", 42));
            final Timed<Integer> timedY = makeTimed("foo", "Y")
                    .flatMap(() -> makeTimed("bar", 42));
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
            final Timed<Integer> timed = makeTimed("foo", 42);
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
            assertEquals(expected, actual.orElse(0));
        }


        @Test
        @DisplayName("invalid 'id' parameter returns empty result")
        void averageUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42);
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
            final Timed<Integer> timed = makeTimed("foo", 42);
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
            final Timed<String> timed = makeTimed("timed", 42)
                    .flatMap(() -> makeTimed("timed", "foobar"));
            final OptionalInt actual = timed.count("timed");
            assertEquals(OptionalInt.of(2), actual);
        }

        @Test
        @DisplayName("count: invalid 'id' parameter returns empty result")
        void countUnknownId() {
            final Timed<Integer> timed = makeTimed("foo", 42);
            final OptionalInt actual = timed.count("bar");
            assertFalse(actual.isPresent());
        }
    }


    @Nested
    @DisplayName("Timed::percentile")
    class Percentile {

        @ParameterizedTest(name = "p{0}")
        @CsvSource({"10,150", "25,225", "50,350", "75, 475", "90, 550"})
        void percentile(int p, long expected) {
            final Timed<String> timed = makeTimed("timed", 42, 100)
                    .flatMap(() -> makeTimed("timed", "foobar", 200))
                    .flatMap(() -> makeTimed("timed", "foobar", 300))
                    .flatMap(() -> makeTimed("timed", "foobar", 400))
                    .flatMap(() -> makeTimed("timed", "foobar", 500))
                    .flatMap(() -> makeTimed("timed", "foobar", 600));
            final OptionalLong actual = timed.percentile(p, "timed", TimeUnit.MILLISECONDS);
            assertEquals(expected, actual.orElse(0));
        }

        @ParameterizedTest()
        @CsvSource({"-1", "0", "101"})
        @DisplayName("throws exception with bad percentile parameter values")
        void exception(final int p) {
            final Timed<String> timed = makeTimed("timed", 42, 100)
                    .flatMap(() -> makeTimed("timed", "foobar", 200))
                    .flatMap(() -> makeTimed("timed", "foobar", 300))
                    .flatMap(() -> makeTimed("timed", "foobar", 400))
                    .flatMap(() -> makeTimed("timed", "foobar", 500))
                    .flatMap(() -> makeTimed("timed", "foobar", 600));

            assertThrows(IllegalArgumentException.class, () -> timed.percentile(p, "timed", TimeUnit.MILLISECONDS));
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
            final Timed<Integer> timedX = makeTimed("timedX", 41);
            final Timed<Integer> timedY = timedX.append(Timed.empty(0), Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        void leftIdentity() {
            final Timed<Integer> timedX = makeTimed("timedX", 41);
            final Timed<Integer> timedY = Timed.empty(0).append(timedX, Integer::sum);
            assertEquals(timedX, timedY);
        }

        @Test
        @DisplayName("associativity: (x <> y) <> z = x <> (y <> z)")
        void associativity() {
            final Timed<Integer> timed1 = makeTimed("timed1", 2);
            final Timed<Integer> timed2 = makeTimed("timed2", 3);
            final Timed<Integer> timed3 = makeTimed("timed3", 4);
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
            final Timed<Integer> timedX = makeTimed("timedX", 41);
            assertEquals(timedX.flatMap(Timed::empty), timedX);
        }

        @Test
        @DisplayName("left identity: (unit x) >>= f ≡ f x)")
        void leftIdentity() {
            final Timed<Integer> timedX = makeTimed("add1", 42);
            final Function<Integer, Timed<Integer>> add1 = x -> makeTimed("add1", x + 1);
            assertEquals(Timed.empty(41).flatMap(add1), timedX);
        }

        @Test
        @DisplayName("associativity (m >>= f) >>= g ≡ m >>= (x -> f x >>= g)")
        void associativity() {
            final Timed<Integer> timedX = makeTimed("timedx", 20);

            final Function<Integer, Timed<Integer>> f = x -> makeTimed("x*2", x * 2);
            final Function<Integer, Timed<Integer>> g = x -> makeTimed("x+1", x + 1);

            assertEquals((timedX.flatMap(g)).flatMap(f),
                    timedX.flatMap(x -> g.apply(x).flatMap(f)));
        }
    }


    @Nested
    @DisplayName("Timed::lift")
    class Lift {

        static final String STOPWATCH_ID = "anId";
        private static final long EPSILON = 10L;

        private int returns42In300ms() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        }

        @Test
        @DisplayName("transforms a Suplier<T> into of Supplier<Timed<T>>")
        void supplier() {
            final Timed<Integer> actual = Timed.lift("anId", (Supplier<Integer>) this::returns42In300ms).get();
            final Timed<Integer> expected = makeTimed(STOPWATCH_ID, 42, 300);
            long expectedElapsed = expected.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L);
            long actualElapsed = actual.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L);
            assertAll(
                    () -> assertEquals(expected, actual),
                    () -> assertTrue(Math.abs(actualElapsed - expectedElapsed) < EPSILON)
            );

        }


        private int returns42In300ms(final int x) {
            return returns42In300ms();
        }

        @Test
        @DisplayName("transforms a Function<A,T> into of Function<A,<Timed<T>>")
        void function() {
            final Timed<Integer> actual = Timed.lift("anId", (Function<Integer, Integer>) this::returns42In300ms).apply(42);
            final Timed<Integer> expected = makeTimed(STOPWATCH_ID, 42, 300);
            long expectedElapsed = expected.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L);
            long actualElapsed = actual.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L);
            assertAll(
                    () -> assertEquals(expected, actual),
                    () -> assertTrue(Math.abs(actualElapsed - expectedElapsed) < EPSILON)
            );

        }

        private int returns42In300ms(final int x, final int y) {
            return returns42In300ms();
        }

        @Test
        @DisplayName("transforms a BiFunction<A,B,T> into of BiFunction<A,B,<Timed<T>>")
        void biFunction() {
            final Timed<Integer> actual = Timed.lift("anId", (BiFunction<Integer, Integer, Integer>) this::returns42In300ms).apply(42, 42);
            final Timed<Integer> expected = makeTimed(STOPWATCH_ID, 42, 300);
            long expectedElapsed = expected.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L);
            long actualElapsed = actual.elapsed("anId", TimeUnit.MILLISECONDS).orElse(0L);
            assertAll(
                    () -> assertEquals(expected, actual),
                    () -> assertTrue(Math.abs(actualElapsed - expectedElapsed) < EPSILON)
            );
        }

    }

    private <T> Timed<T> makeTimed(final String id, final T value) {
        final Random random = new Random();
        final int randomMillis = random.ints(1, 30, 250)
                .boxed()
                .findAny()
                .orElse(42);
        return makeTimed(id, value, randomMillis);
    }
        private <T> Timed<T> makeTimed(final String id, final T value, final int millis) {
            final FakeTicker ticker = new FakeTicker();
            final com.google.common.base.Stopwatch stopwatch = com.google.common.base.Stopwatch.createStarted(ticker);
            ticker.advance(millis, TimeUnit.MILLISECONDS);
            stopwatch.stop();
            return Timed.of(value, Stopwatch.of(id, stopwatch));
        }

    }
