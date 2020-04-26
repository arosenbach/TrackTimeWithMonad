package logmonad.util;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ListFunction {

    public static <T> List<T> add(List<T> list, T element) {
        return Stream.concat(list.stream(), Stream.of(element)).collect(toList());
    }

    public static <T> List<T> concat(List<T> listA, List<T> listB) {
        return Stream.concat(listA.stream(), listB.stream()).collect(toList());
    }
}
