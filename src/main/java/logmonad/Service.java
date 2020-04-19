package logmonad;

import com.google.common.base.Stopwatch;

public class Service {
    private Log<Integer> start(int val) {
        return Log.trace(val, "initial value");
    }

    private Log<Integer> operation2(Integer val) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        final int value = privateOperation2(val);
        stopwatch.stop();
        return Log.trace(value, "operation2", stopwatch);
    }

    private int privateOperation2(final Integer val) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return val + 2;
    }

    private Log<Double> operation3(Integer val) {
        return Log.trace(val/4d, "divided by 4");
    }

    public void run() {
        Log<Double> log = start(5)
                .flatMap(this::operation2)
                .flatMap(this::operation3)
                .flatMap( val ->
                        Log.trace( val * 2, "Multiplied by two")
                );

        System.out.println("Value: " + log.getValue());
        System.out.println("Trace: " + log.getTrace());
    }
}
