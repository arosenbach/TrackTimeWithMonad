package logmonad.example;

import logmonad.util.DoStuff;
import logmonad.util.Random;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class ServiceA {

    public List<String> getUserIds(final Class<Void> __) {
        DoStuff.run();
        return IntStream.range(Random.getRandomInt(10, 15), Random.getRandomInt(25, 35))
                .boxed().map(n -> "user" + n)
                .collect(toList());
    }

}
