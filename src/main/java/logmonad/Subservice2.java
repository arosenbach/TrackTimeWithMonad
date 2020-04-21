package logmonad;

import com.google.common.base.Stopwatch;

import java.util.Collections;

public class Subservice2 {
    public Timed<Integer> operation2(String aString) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(42, stopwatch);
    }

    public Timed<Integer> operation3(int param) {
        Timed<Integer> result = Timed.of(param, Stopwatch.createStarted());
        for(int i = param; i< param+10; i++){
            result = result.append(Integer::sum, privateOperation(i));
        }
        return result;
    }


    private Timed<Integer> privateOperation(int i) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(1, stopwatch);
    }
}
