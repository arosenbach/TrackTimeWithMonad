package logmonad.example;

import logmonad.Timed;
import logmonad.TimerCollector;
import logmonad.util.DoStuff;

import java.util.List;

public class Main {
    public static void main(String... args) {
        final ServiceA serviceA = new ServiceA();
        final ServiceB serviceB = new ServiceB();

        final Timed<List<User>> adultUsers = checkAuthentication()
                .flatMap(serviceA::getUserIds)
                .flatMap(serviceB::getUsers)
                .flatMap(serviceB::filterAdults);

        System.out.println(adultUsers);
        System.out.println("getUser total -> " + adultUsers.getTimes("getUser").stream().mapToLong(Long::longValue).sum() + "ms");

        adultUsers.getTimes("getUser")
                .stream()
                .mapToLong(Long::longValue)
                .average()
                .ifPresent(avg -> System.out.println("getUser average -> " + avg + "ms"));


    }

    private static Timed<Class<Void>> checkAuthentication() {
        final long startTime = System.nanoTime();
        DoStuff.run();
        final long endTime = System.nanoTime();
        return Timed.of(TimerCollector.of("checkAuthentication", endTime - startTime), Void.TYPE);
    }

}
