package logmonad.example;

import com.google.common.base.Stopwatch;
import logmonad.Timed;
import logmonad.TimerCollector;
import logmonad.util.DoStuff;
import logmonad.util.ListFunction;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ServiceB {
    public Timed<List<User>> getUsers(final List<String> ids) {
        return ids.stream()
                .map(Timed.lift("getUser", this::getUser))
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

    public User getUser(final String userId) {
        DoStuff.run();
        return new User(userId);
    }

//    public Timed<User> getUser(final String userId) {
//        final Stopwatch stopwatch = Stopwatch.createStarted();
//        DoStuff.run();
//        stopwatch.stop();
//        return Timed.of(
//                new User(userId), TimerCollector.of("getUser", stopwatch)
//        );
//    }

    public Timed<List<User>> filterAdults(final List<User> users) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(users.stream().filter(User::isAdult).collect(toList()), TimerCollector.of("filterAdults", stopwatch)
        );
    }

}
