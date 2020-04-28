package logmonad.example;

import logmonad.Timed;
import logmonad.TimerCollector;
import logmonad.example.User;
import logmonad.util.DoStuff;
import logmonad.util.ListFunction;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class ServiceB {
    public Timed<List<User>> getUsers(final List<String> ids) {
        return ids.stream()
                .map(this::getUser)
                .reduce(Timed.empty(Collections.emptyList()),
                        (acc, next) -> acc.append(next, ListFunction::add),
                        (timedListA, timedListB) -> timedListA.append(timedListB, ListFunction::concat));

        /* This does the same, using a for-loop */
//        Timed<List<Record>> result = Timed.empty(Collections.emptyList());
//        for (String id : ids) {
//            result = result.append(getUser(id), ListFunction::add);
//        }
//        return result;
    }

    public Timed<User> getUser(final String userId) {
        final long startTime = System.nanoTime();
        DoStuff.run();
        final long endTime = System.nanoTime();
        return Timed.of(
                TimerCollector.of("getUser", endTime - startTime),
                new User(userId));
    }

    public Timed<List<User>> filterAdults(final List<User> users) {
        final long startTime = System.nanoTime();
        DoStuff.run();
        final long endTime = System.nanoTime();
        return Timed.of(TimerCollector.of("filterAdults", endTime - startTime),
                users.stream().filter(User::isAdult).collect(toList()));
    }

}
