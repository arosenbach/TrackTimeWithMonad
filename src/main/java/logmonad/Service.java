package logmonad;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class Service {
    private Log<String> getId() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        // DO STUFF
        try {
            Thread.sleep(111);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String id = "anID";
        //
        stopwatch.stop();
        return Log.trace(id, "getId (" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms)");
    }

    private Log<Integer> operation2(String id) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        // DO STUFF
        try {
            Thread.sleep(222);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final int value = 2;
        //
        stopwatch.stop();
        return Log.trace(value, "operation2 (" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms)");
    }

    private Log<Double> operation3(Integer val) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        // DO STUFF
        try {
            Thread.sleep(333);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        stopwatch.stop();
        return Log.trace(val / 4d, "operation3 (" + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms)");
    }

    public void run() {
        Log<Double> log = getId()
                .flatMap(this::operation2)
                .flatMap(this::operation3)
                .flatMap(val ->
                        Log.trace(val * 2, "Multiplied by two")
                );

        System.out.println("Value: " + log.getValue());
        System.out.println("Trace: " + log.getTrace());
    }
}
