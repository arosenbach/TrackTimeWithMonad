package logmonad.old.services;

import com.google.common.base.Stopwatch;
import logmonad.old.NamedStopwatch;
import logmonad.old.Timed;
import logmonad.util.DoStuff;

public class Subservice1 {
    public Timed<String> operation1(Class<Void> v) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of("42", NamedStopwatch.of("Subservice1:operation1",stopwatch));
    }
}
