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

        final Timed<Integer> timedResult = privateStuff()
                .flatMap(subservice1::operation1)
                .flatMap(subservice2::operation2)
                .flatMap(subservice2::operation3);

        System.out.println("value: " + timedResult.get() + ", Total time: " + timedResult.elapsed(TimeUnit.MILLISECONDS) + "ms");
        System.out.println("Details: ");
        timedResult.getStopwatches().forEach(stopwatch -> System.out.println("**"+stopwatch.getName() + ": " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms"));
    }

    private Timed<Class<Void>> privateStuff() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        return Timed.of(Void.TYPE, NamedStopwatch.of("Service2::privateStuff", stopwatch));
    }

}
