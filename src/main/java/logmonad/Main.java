package logmonad;

import static java.util.stream.Collectors.toMap;

public class Main {
    public static void main(String... args){
//        final Service service = new Service();
//        service.run();

//        final Service2 service2 = new Service2(new Subservice1(), new Subservice2());
//        service2.run();

        final Writer<String> writer = Writer.of(TimerCollector.of("method1", 23), 42)
                .flatMap(Main::add1)
                .flatMap(Main::loop5);
        System.out.println(writer);
    }

    private static  Writer<String> loop5(final String s) {
//        TimerCollector timerCollector = TimerCollector.empty();
//        final StringBuilder result = new StringBuilder();
//        for(int i = 0; i < 5;i++){
//            timerCollector = timerCollector.append(TimerCollector.of("loop5", i));
//            result.append(i);
//        }
//        return Writer.of(timerCollector,result.toString());
        Writer<String> result = Writer.empty("");
        for(int i = 0; i < 5;i++){
            result = result.append(TimerCollector.of("loop5", i), result.getValue() + i);
        }
        return result;
    }

    private static Writer<String> add1(final Integer integer) {
        final TimerCollector time = TimerCollector.of("add1", 342);
        return Writer.of(time,integer+1+"");
    }


}
