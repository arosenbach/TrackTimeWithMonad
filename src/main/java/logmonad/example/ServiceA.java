package logmonad.example;

import logmonad.Timed;
import logmonad.TimerCollector;
import logmonad.util.DoStuff;
import logmonad.util.Random;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class ServiceA {


    public Timed<List<String>> getUserIds(final Class<Void> __) {
        final long startTime = System.nanoTime();
        DoStuff.run();
        final long endTime = System.nanoTime();

        final List<String> userIds = IntStream.range(Random.getRandomInt(10, 15), Random.getRandomInt(25, 35))
                .boxed().map(n -> "user" + n)
                .collect(toList());
        return Timed.of(TimerCollector.of("getUserIds", endTime - startTime), userIds);
    }

}
