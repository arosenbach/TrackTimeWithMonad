package timedmonad.example;

import com.google.common.base.Stopwatch;
import timedmonad.Timed;
import timedmonad.example.util.DoStuff;
import timedmonad.example.util.ListFunction;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ServiceB {
    public Timed<List<User>> getUsers(final List<String> ids) {
        return ids.stream()
                .map(Timed.lift("getUser", this::getUser))
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

    public List<User> filterAdults(final List<User> users) {
      return users.stream().filter(User::isAdult).collect(toList());
    }

}
