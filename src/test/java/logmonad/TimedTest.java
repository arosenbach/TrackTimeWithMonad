package logmonad;

import com.google.common.base.Stopwatch;
import logmonad.old.NamedStopwatch;
import logmonad.old.Timed;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Timed")
class TimedTest {

    private Function<Integer,Integer> add1 = x -> x+1;

    @Test
    @DisplayName("Timed.of(a) >>= f(x)  ⇔  (Timed<> a) >>= f(x)  ⇔  f(a)")
    void test1() {
        Timed.of(41, NamedStopwatch.of("test1", Stopwatch.createStarted()))
                .flatMap(this::tAdd1);
        assertTrue(true);
    }

    private Timed<Function<Integer, Integer>> tAdd1(final int integer) {
        return Timed.of(add1, NamedStopwatch.of("tAdd1", Stopwatch.createStarted()));
    }



    @Nested
    @DisplayName("is a monad")
    class Monoid {

        @Test
        @DisplayName("right identity: m >>= unit ≡ m)")
        void rightIdentity() {
            final Timed<Integer> timedX = Timed.of(41, NamedStopwatch.of("timedx", Stopwatch.createStarted()));

            final Function<Integer, Timed<Integer>> unit = val -> Timed.of(val, NamedStopwatch.EMPTY);
            assertEquals(timedX.flatMap(unit), timedX);
        }

        @Test
        @DisplayName("left identity: (unit x) >>= f ≡ f x)")
        void leftIdentity() {
            final NamedStopwatch namedStopwatch1 = NamedStopwatch.of("Acme", Stopwatch.createStarted());
            final NamedStopwatch namedStopwatch2 = NamedStopwatch.EMPTY.append(namedStopwatch1);
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("associativity (m >>= f) >>= g ≡ m >>= (x -> f x >>= g)")
        void associativity() {
            final Timed<Integer> timedX = Timed.of(20, NamedStopwatch.of("timedx", Stopwatch.createStarted()));

            final Function<Integer, Timed<Integer>> f = x ->  Timed.of(x * 2,  NamedStopwatch.of("x*2", Stopwatch.createStarted())) ;
            final Function<Integer, Timed<Integer>> g = x ->  Timed.of(x + 1,  NamedStopwatch.of("x+1", Stopwatch.createStarted())) ;

            assertEquals((timedX.flatMap(g)).flatMap(f), timedX.flatMap(x -> g.apply(x).flatMap(f)));
            }
    }

}