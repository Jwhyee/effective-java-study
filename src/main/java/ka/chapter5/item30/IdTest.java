package ka.chapter5.item30;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class IdTest {
    @Test
    void functionTest() {
        String[] strs = {"삼베", "대마", "나일론"};
        UnaryOperator<String> sameString = identityFunction();
        for (String s : strs) {
            String apply = sameString.apply(s);
            System.out.println(sameString.apply(s));
        }

        Number[] numbers = {1, 2.0, 3L};
        UnaryOperator<Number> sameNumber = identityFunction();
        for (Number number : numbers) {
            System.out.println(sameNumber.apply(number));
        }
    }

    public static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    @Test
    void maxTest() {

    }

    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if(c.isEmpty()) throw new IllegalArgumentException("빈 컬렉션");

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }

        return result;
    }
}
