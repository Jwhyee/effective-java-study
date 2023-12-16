package ka.chapter5.item32;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VarargsTest {
    @Test
    void argsTest() {
        printArgs(1, 2, 3, 4, 5);

        printListArgs(List.of("Hello", "World"), List.of("olleH", "dlrow"));

        pickTwo("좋은", "빠른", "저렴한");
    }

    public void printArgs(int... args) {
        for (int arg : args) {
            System.out.printf("%d ", arg);
        }
    }

    public void printListArgs(List<String>... args) {
        List<Integer> intList = List.of(42);
        Object[] objects = args;
        // 힙 오염 발생
        objects[0] = intList;
        // 컴파일 에러 발생 : ClassCastException
        String s = args[0].get(0);
    }

    static <T> T[] toArray(T... args) {
        return args;
    }

    static <T> T[] pickTwo(T a, T b, T c) {
        return switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0 -> toArray(a, b);
            case 1 -> toArray(a, c);
            case 2 -> toArray(b, b);
            default -> throw new AssertionError();
        };
    }

    @SafeVarargs
    static <T> List<T> flatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }
}
