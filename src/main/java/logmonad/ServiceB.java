package logmonad;

import logmonad.example.Record;
import logmonad.util.DoStuff;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class ServiceB {
    public Timed<List<Record>> getRecords(final List<String> ids) {
//        final long startTime = System.nanoTime();
//        DoStuff.run();
//        final long endTime = System.nanoTime();
//        return Timed.of(TimerCollector.of("checkAuthentication", endTime - startTime), Arrays.asList("id1","id2","id3"));

        Timed<List<Record>> result = Timed.empty(Collections.emptyList());
        for (String id : ids) {
            final Timed<Record> record = getRecord(id);
            result = result.append(record, (res, rec) -> Stream.concat(res.stream(), Stream.of(rec)).collect(toList()));
        }
        return result;


//        final List<Timed<Record>> collect = ids.stream().map(this::getRecord).collect(toList());


////        ids.map(this::getRecord).reduce(Timed.empty(""), );
//        final BiFunction<List<Record>, Record, List<Record>> addToList = (l, v)-> Stream.concat(l.stream(), Stream.of(v))
//                .collect(Collectors.toList());
//        final BiFunction<? super Timed<List<Record>>, ? super Timed<Record>, ? super Timed<List<Record>>> bb =
//                (acc, next) -> acc.append(next, addToList);
//        final BinaryOperator<Timed<Record>> cc = (x,y) -> x.append(y, x.getValue());
//        return ids.stream()
//                .map(this::getRecord)
//                .reduce(Timed.empty(Collections.<Record>emptyList()), bb, cc);
////                .reduce(result, (acc, next)-> acc.);
    }

    public Timed<Record> getRecord(final String s) {
        final long startTime = System.nanoTime();
        DoStuff.run();
        final long endTime = System.nanoTime();
        return Timed.of(
                TimerCollector.of("getRecord", endTime - startTime),
                new Record(s));
    }
}
