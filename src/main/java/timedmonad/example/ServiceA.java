package timedmonad.example;

import timedmonad.example.util.DoStuff;
import timedmonad.example.util.Random;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class ServiceA {

    public List<String> getUserIds() {
        DoStuff.sleep();
        return IntStream.range(Random.getRandomInt(10, 15), Random.getRandomInt(25, 35))
                .boxed().map(n -> "user" + n)
                .collect(toList());
    }

}
