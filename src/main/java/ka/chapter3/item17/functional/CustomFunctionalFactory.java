package ka.chapter3.item17.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CustomFunctionalFactory {

    public static <T> int findIdx(Set<T> set, Supplier<T> supplier) {
        int i = 0;
        for (T t : set) {
            if (t.equals(supplier.get())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static <T> void forEachRemaining(List<T> list, Consumer<T> consumer) {
        for (T t : list) {
            consumer.accept(t);
        }
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(function.apply(item));
        }
        return result;
    }
}