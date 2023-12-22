package ka.chapter5.item32;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VarargsTest {
    @Test
    void argsTest() {
//        printArgs(1, 2, 3, 4, 5);
//
//        printListArgs(List.of("Hello", "World"), List.of("olleH", "dlrow"));
//
//        pickTwo("좋은", "빠른", "저렴한");
    }

    @Test
    void heapPollutionTest() {
        // String 서브 타입의 ArrayList 선언
        List<String> list1 = new ArrayList<>();
        list1.add("홍길동");
        list1.add("임꺽정");

        // 최상위 타입으로써 다루기 위해 Object 타입으로 업 캐스팅
        Object obj = list1;

//        doSomething(obj);

        // ArrayList로 되돌릴 때, String이 아닌 Double로 다운 캐스팅
        // 소수값 리스트에 추가
        List<Double> list2 = (List<Double>) obj;
        list2.add(1.0);
        list2.add(2.0);

        System.out.println(list2); // [홍길동, 임꺾정, 1.0, 2.0]

        for(double n : list2) {
            System.out.println(n);
        }
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
