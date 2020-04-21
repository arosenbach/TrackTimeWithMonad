package logmonad;

import com.google.common.base.Stopwatch;

import java.util.Collections;

public class Subservice1 {
    public Timed<String> operation1(Class<Void> v) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of("42", stopwatch);
    }
}
