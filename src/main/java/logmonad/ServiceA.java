package logmonad;

import logmonad.util.DoStuff;

import java.util.Arrays;
import java.util.List;

public class ServiceA {


    public Timed<List<String>> getIds(final Class<Void> _void) {
        final long startTime = System.nanoTime();
        DoStuff.run();
        final long endTime = System.nanoTime();
        return Timed.of(TimerCollector.of("getIds", endTime - startTime), Arrays.asList("id1","id2","id3"));
    }

}
