package timedmonad.example;

import com.google.common.base.Stopwatch;
import timedmonad.Timed;
import timedmonad.example.util.DoStuff;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String... args) {
        final ServiceA serviceA = new ServiceA();
        final ServiceB serviceB = new ServiceB();

        final Timed<List<User>> adultUsers = checkAuthentication()
                .flatMap(Timed.lift("ServiceA::getUserIds", serviceA::getUserIds))
                .flatMap(serviceB::getUsers)
                .flatMap(Timed.lift("ServiceB::filterAdults", serviceB::filterAdults));

        System.out.println(adultUsers);
        System.out.println("getUser total -> " + adultUsers.getStopwatches("getUser")
                .stream()
                .mapToLong(stopwatch -> stopwatch.elapsed(TimeUnit.MILLISECONDS))
                .sum() + " ms");

        System.out.println(adultUsers.getValue());

        adultUsers.getStopwatches("getUser")
                .stream()
                .mapToLong(stopwatch -> stopwatch.elapsed(TimeUnit.MILLISECONDS))
                .average()
                .ifPresent(avg -> System.out.println("getUser average -> " + avg + " ms"));


    }

    private static Timed<Class<Void>> checkAuthentication() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.sleep();
        stopwatch.stop();
        return Timed.of(Void.TYPE, Timed.NamedStopwatch.of("checkAuthentication", stopwatch));
    }

}
