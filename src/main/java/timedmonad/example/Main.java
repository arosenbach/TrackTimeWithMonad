package timedmonad.example;

import timedmonad.Timed;
import timedmonad.example.util.DoStuff;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    private static final String CHECK_AUTHENTICATION = "checkAuthentication";
    private static final String GET_USER_IDS = "ServiceA::getUserIds";
    private static final String FILTER_ADULTS = "ServiceB::filterAdults";

    public static void main(String... args) {
        final ServiceA serviceA = new ServiceA();
        final ServiceB serviceB = new ServiceB();

        final Timed<List<User>> adultUsers = checkAuthentication()
                .flatMap(Timed.lift(GET_USER_IDS, serviceA::getUserIds))
                .flatMap(serviceB::getUsers)
                .flatMap(Timed.lift(FILTER_ADULTS, serviceB::filterAdults));

        System.out.println("====== Use the value =======");
        System.out.println("List of adult users: " + adultUsers.getValue()
                .stream()
                .map(User::getId)
                .collect(Collectors.joining(", ")));

        printTimes(adultUsers);
    }

    private static Timed<Class<Void>> checkAuthentication() {
        final com.google.common.base.Stopwatch stopwatch = com.google.common.base.Stopwatch.createStarted();
        DoStuff.sleep();
        stopwatch.stop();
        return Timed.of(Void.TYPE, Timed.Stopwatch.of(CHECK_AUTHENTICATION, stopwatch));
    }

    private static void printTimes(final Timed<List<User>> adultUsers) {
        System.out.println("====== Times =======");

        // Elapsed
        adultUsers.elapsed(CHECK_AUTHENTICATION, TimeUnit.MILLISECONDS)
                .ifPresent(value -> System.out.println(CHECK_AUTHENTICATION + " time : " + value + " ms"));
        adultUsers.elapsed(GET_USER_IDS, TimeUnit.MILLISECONDS)
                .ifPresent(value -> System.out.println(GET_USER_IDS + " time : " + value + " ms"));
        adultUsers.elapsed(ServiceB.GET_USER, TimeUnit.MILLISECONDS)
                .ifPresent(value -> System.out.println(ServiceB.GET_USER + " time : " + value + " ms"));
        adultUsers.elapsed(FILTER_ADULTS, TimeUnit.MILLISECONDS)
                .ifPresent(value -> System.out.println(FILTER_ADULTS + " time : " + value + " ms"));

        // Average
        adultUsers.average(ServiceB.GET_USER, TimeUnit.MILLISECONDS)
                .ifPresent(avg -> System.out.println(ServiceB.GET_USER + " average time : " + avg + " ms"));

        // Min / Max
        adultUsers.min(ServiceB.GET_USER, TimeUnit.MILLISECONDS)
                .ifPresent(min -> System.out.println(ServiceB.GET_USER + " min time : " + min + " ms"));
        adultUsers.max(ServiceB.GET_USER, TimeUnit.MILLISECONDS)
                .ifPresent(max -> System.out.println(ServiceB.GET_USER + " max time : " + max + " ms"));

        // Count
        adultUsers.count(ServiceB.GET_USER)
                .ifPresent(cnt -> System.out.println(ServiceB.GET_USER + " count : " + cnt));

        // Percentile
        adultUsers.percentile(50, ServiceB.GET_USER, TimeUnit.MILLISECONDS)
                .ifPresent(p50 -> System.out.println(ServiceB.GET_USER + " median time : " + p50 + " ms"));
        adultUsers.percentile(95, ServiceB.GET_USER, TimeUnit.MILLISECONDS)
                .ifPresent(p95 -> System.out.println(ServiceB.GET_USER + " p95 time : " + p95 + " ms"));

        // Total elapsed
        System.out.println("Total time: " + adultUsers.elapsed(TimeUnit.MILLISECONDS) + " ms");
    }

}
