package logmonad;

import com.google.common.base.Stopwatch;

import java.util.Collections;

public class Subservice2 {
    public Timed<Integer> operation2(String aString) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(42, Collections.singletonList(stopwatch));
    }
}
