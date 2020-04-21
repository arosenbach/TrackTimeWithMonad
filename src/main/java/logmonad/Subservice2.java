package logmonad;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class Subservice2 {
    public Timed<Integer> operation2(String aString) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(42, Truc.of("Subservice::operation2",stopwatch));
    }

    public Timed<Integer> operation3(int param) {
        Timed<Integer> result = Timed.of(param, Truc.of("operation3", Stopwatch.createStarted()));
        final int randomInt = DoStuff.getRandomInt(10, 50);
        System.out.println(">>>>"+randomInt);
        for(int i = param; i< param+10; i++){
            result = result.append(Integer::sum, privateOperation(i, randomInt));
        }
        return result;
    }


    private Timed<Integer> privateOperation(int iint, int randomInt) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run(randomInt);
        System.out.println(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        return Timed.of(1, Truc.of("operation3 -> Subservice2::privateOperation",stopwatch));
    }
}
