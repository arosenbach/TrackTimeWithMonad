package logmonad;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class Service2 {

    private Subservice1 subservice1;
    private Subservice2 subservice2;

    public Service2(final Subservice1 subservice1, final Subservice2 subservice2) {
        this.subservice1 = subservice1;
        this.subservice2 = subservice2;
    }

    public void run() {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        DoStuff.run();
        final Timed<Integer> subservice1Result = subservice1.operation1()
                .flatMap(s -> subservice2.operation2(s));

        stopwatch.stop();
        System.out.println("value: " + subservice1Result.get() + " time: " + subservice1Result.elapsed(TimeUnit.MILLISECONDS) + "ms");
        System.out.println("Total Elapsed time: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

}
