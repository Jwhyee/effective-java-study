package ka.chapter6.item39;

import java.util.ArrayList;
import java.util.List;

public class SampleTest4 {
//    @MultiExceptionTestByContainer(IndexOutOfBoundsException.class)
//    @MultiExceptionTestByContainer(NullPointerException.class)
    @MultiExceptionTestByContainer(ClassCastException.class)
    @MultiExceptionTestByContainer(IllegalStateException.class)
    public static void test1() {
        List<String> list = new ArrayList<>();
        list.addAll(5, null);
    }

}
