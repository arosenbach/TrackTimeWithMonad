package logmonad.example;

import com.google.common.base.Stopwatch;
import logmonad.Timed;
import logmonad.TimerCollector;
import logmonad.aspect.AppConfig;
import logmonad.util.DoStuff;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String... args) {

        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();

        final ServiceA serviceA = ctx.getBean(ServiceA.class);
        final ServiceB serviceB = ctx.getBean(ServiceB.class);

        final Timed<List<User>> adultUsers = checkAuthentication()
                .flatMap(serviceA::getUserIds)
                .flatMap(serviceB::getUsers)
                .flatMap(serviceB::filterAdults);

        System.out.println(adultUsers);
        System.out.println("getUser total -> " + adultUsers.getTimes("getUser")
                .stream()
                .mapToLong(stopwatch -> stopwatch.elapsed(TimeUnit.MILLISECONDS))
                .sum() + " ms");

        System.out.println(adultUsers.getValue());

        adultUsers.getTimes("getUser")
                .stream()
                .mapToLong(stopwatch -> stopwatch.elapsed(TimeUnit.MILLISECONDS))
                .average()
                .ifPresent(avg -> System.out.println("getUser average -> " + avg + " ms"));

        final List<String> userIds = serviceA.getUserIds();
        System.out.println(userIds);


    }

    private static Timed<Class<Void>> checkAuthentication() {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(TimerCollector.of("checkAuthentication", stopwatch), Void.TYPE);
    }

}
