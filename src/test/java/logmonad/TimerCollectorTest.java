package logmonad;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("TimerCollector")
class TimerCollectorTest {

    @Nested
    @DisplayName("is a monoid with TimerCollector::append")
    class Monoid {

        @Test
        @DisplayName("equality by value")
        void equals() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", 11L);
            final TimerCollector namedStopwatch2 = TimerCollector.of("Acme", 22L);
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("right identity: x <> mempty = x)")
        void rightIdentity() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", 11L);
            final TimerCollector namedStopwatch2 = namedStopwatch1.append(TimerCollector.empty());
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("left identity: mempty <> x = x)")
        void leftIdentity() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", 11L);
            final TimerCollector namedStopwatch2 = TimerCollector.empty().append(namedStopwatch1);
            assertEquals(namedStopwatch1, namedStopwatch2);
        }

        @Test
        @DisplayName("associativity")
        void associativity() {
            final TimerCollector namedStopwatch1 = TimerCollector.of("Acme", 11L);
            final TimerCollector namedStopwatch2 = TimerCollector.of("Acme2", 22L);
            final TimerCollector namedStopwatch3 = TimerCollector.of("Acme", 33L);
            assertEquals((namedStopwatch1.append(namedStopwatch2)).append(namedStopwatch3), namedStopwatch1.append((namedStopwatch2).append(namedStopwatch3)));
        }
    }
}