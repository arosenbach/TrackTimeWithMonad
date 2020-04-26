package logmonad;

import logmonad.example.Record;
import logmonad.util.DoStuff;

import java.util.List;

import static java.util.stream.Collectors.toMap;

public class Main {
    public static void main(String... args) {
        final ServiceA serviceA = new ServiceA();
        final ServiceB serviceB = new ServiceB();

        final Timed<List<Record>> timed = checkAuthentication()
                .flatMap(serviceA::getIds)
                .flatMap(serviceB::getRecords);
//                .flatMap(serviceB::operation3);
        System.out.println(timed);
        System.out.println("getRecord total -> " + timed.getTimes("getRecord").stream().mapToLong(Long::longValue).sum() + "ms");
        System.out.println("getRecord average -> " + timed.getTimes("getRecord").stream().mapToLong(Long::longValue).average().getAsDouble() + "ms");

    }

    public static Timed<Class<Void>> checkAuthentication() {
        final long startTime = System.nanoTime();
        DoStuff.run();
        final long endTime = System.nanoTime();
        return Timed.of(TimerCollector.of("checkAuthentication", endTime - startTime), Void.TYPE);
    }

    private static Timed<String> loop5(final String s) {
//        TimerCollector timerCollector = TimerCollector.empty();
//        final StringBuilder result = new StringBuilder();
//        for(int i = 0; i < 5;i++){
//            timerCollector = timerCollector.append(TimerCollector.of("loop5", i));
//            result.append(i);
//        }
//        return Writer.of(timerCollector,result.toString());
        Timed<String> result = Timed.empty("");
        for (int i = 0; i < 5; i++) {
            result = result.append(TimerCollector.of("loop5", i), result.getValue() + i);
        }
        return result;
    }

    private static Timed<String> add1(final Integer integer) {
        final TimerCollector time = TimerCollector.of("add1", 342);
        return Timed.of(time, integer + 1 + "");
    }


}
