package timedmonad.example;

import timedmonad.Timed;
import timedmonad.example.util.DoStuff;
import timedmonad.example.util.ListFunction;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ServiceB {

    public static final String GET_USER = "ServiceB::getUser";

    public Timed<List<User>> getUsers(final List<String> ids) {
        return ids.stream()
                .map(Timed.lift(GET_USER, this::getUser))
                .reduce(Timed.empty(Collections.emptyList()),
                        (acc, next) -> acc.append(next, ListFunction::add),
                        (timedListA, timedListB) -> timedListA.append(timedListB, ListFunction::concatenate));

        /* This does the same, using a for-loop */
//        Timed<List<User>> result = Timed.empty(Collections.emptyList());
//        for (String id : ids) {
//            result = result.append(
//                    Timed.lift(GET_USER, this::getUser).apply(id),
//                    ListFunction::add);
//        }
//        return result;
    }

    public User getUser(final String userId) {
        DoStuff.takeSomeTime();
        return new User(userId);
    }

    public List<User> filterAdults(final List<User> users) {
        return users.stream().filter(User::isAdult).collect(toList());
    }

}
