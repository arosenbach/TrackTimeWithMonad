package logmonad;

import com.google.common.base.Stopwatch;

public class Subservice1 {
    public Timed<String> operation1(Class<Void> v) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of("42", Truc.of("Subservice1:operation1",stopwatch));
    }
}
