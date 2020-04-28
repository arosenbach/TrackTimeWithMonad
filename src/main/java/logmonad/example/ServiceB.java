package logmonad.example;

import com.google.common.base.Stopwatch;
import logmonad.Timed;
import logmonad.TimerCollector;
import logmonad.util.DoStuff;
import logmonad.util.ListFunction;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
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
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(
                TimerCollector.of("getUser", stopwatch),
                new User(userId));
    }

    public Timed<List<User>> filterAdults(final List<User> users) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.run();
        stopwatch.stop();
        return Timed.of(TimerCollector.of("filterAdults", stopwatch),
                users.stream().filter(User::isAdult).collect(toList()));
    }

}
