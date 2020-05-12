package timedmonad.example;

import com.google.common.base.Stopwatch;
import timedmonad.Timed;
import timedmonad.example.util.DoStuff;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class Main {

    public static final String CHECK_AUTHENTICATION = "checkAuthentication";

    public static void main(String... args) {
        final ServiceA serviceA = new ServiceA();
        final ServiceB serviceB = new ServiceB();

        final Timed<List<User>> adultUsers = checkAuthentication()
                .flatMap(Timed.lift("ServiceA::getUserIds", serviceA::getUserIds))
                .flatMap(serviceB::getUsers)
                .flatMap(Timed.lift("ServiceB::filterAdults", serviceB::filterAdults));

        System.out.println(adultUsers.getAllStopwatches());

//        System.out.println("List of adult users: " + adultUsers.getValue()
//                .stream()
//                .map(User::getId)
//                .collect(Collectors.joining(", ")));

        adultUsers.elapsed(CHECK_AUTHENTICATION, TimeUnit.MILLISECONDS)
                .ifPresent(value -> System.out.println(CHECK_AUTHENTICATION + "time : " + value + " ms"));
//
//        final long getUserTotalTime = adultUsers.getStopwatches(ServiceB.GET_USER)
//                .stream()
//                .mapToLong(stopwatch -> stopwatch.elapsed(TimeUnit.MILLISECONDS))
//                .sum();
//        System.out.println(ServiceB.GET_USER + " total time : " + getUserTotalTime + " ms");
//
//        adultUsers.getStopwatches(ServiceB.GET_USER)
//                .stream()
//                .mapToLong(stopwatch -> stopwatch.elapsed(TimeUnit.MILLISECONDS))
//                .average()
//                .ifPresent(avg -> System.out.println(ServiceB.GET_USER + " average time : " + avg + " ms"));


    }

    private static Timed<Class<Void>> checkAuthentication() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.sleep();
        stopwatch.stop();
        return Timed.of(Void.TYPE, Timed.NamedStopwatch.of(CHECK_AUTHENTICATION, stopwatch));
    }

}
