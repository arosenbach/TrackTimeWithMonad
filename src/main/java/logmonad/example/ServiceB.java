package logmonad.example;

import com.google.common.base.Stopwatch;
import logmonad.Timed;
import logmonad.TimerCollector;
import logmonad.example.util.DoStuff;
import logmonad.example.util.ListFunction;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ServiceB {
    public Timed<List<User>> getUsers(final List<String> ids) {
        return ids.stream()
                .map(Timed.trackTime("getUser", this::getUser))
                .reduce(Timed.empty(Collections.emptyList()),
                        (acc, next) -> acc.append(next, ListFunction::add),
                        (timedListA, timedListB) -> timedListA.append(timedListB, this::concatenateLists));

        /* This does the same, using a for-loop */
//        Timed<List<Record>> result = Timed.empty(Collections.emptyList());
//        for (String id : ids) {
//            result = result.append(getUser(id), ListFunction::add);
//        }
//        return result;
    }

    private <T> List<T> concatenateLists(List<T> listA, List<T> listB) {
        return Stream.concat(listA.stream(), listB.stream()).collect(toList());
    }

    public User getUser(final String userId) {
        DoStuff.sleep();
        return new User(userId);
    }

    public Timed<List<User>> filterAdults(final List<User> users) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        DoStuff.sleep();
        stopwatch.stop();
        return Timed.of(users.stream().filter(User::isAdult).collect(toList()), TimerCollector.of("filterAdults", stopwatch)
        );
    }

}
