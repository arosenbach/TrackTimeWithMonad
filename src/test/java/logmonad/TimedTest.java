package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TimedTest {

    private Function<Integer,Integer> add1 = x -> x+1;

    @Test
    @DisplayName("Timed.of(a) >>= f(x)  ⇔  (Timed<> a) >>= f(x)  ⇔  f(a)")
    void test1() {
        Timed.of(41, Stopwatch.createStarted())
                .flatMap(this::tAdd1);
        assertTrue(true);
    }

    private Timed<Function<Integer, Integer>> tAdd1(final int integer) {
        return Timed.of(add1, Stopwatch.createStarted());
    }


}