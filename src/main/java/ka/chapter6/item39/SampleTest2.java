package ka.chapter6.item39;

import java.util.ArrayList;
import java.util.List;

public class SampleTest2 {
    @ExceptionTest(ArithmeticException.class)
    public static void test1() {
        int i = 0;
        i = i / i;
    }

    @ExceptionTest(ArithmeticException.class)
    public static void test2() {
        int[] a = new int[0];
        int i = a[1];
    }

    @ExceptionTest(ArithmeticException.class)
    public static void test3() {

    }

    @MultiExceptionTestByArray({
            IndexOutOfBoundsException.class,
            NullPointerException.class
    })
    public static void doublyBad() {
        List<String> list = new ArrayList<>();
        list.addAll(5, null);
    }

}
