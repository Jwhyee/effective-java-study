package ka.chapter5.item31;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {
    @Test
    void listTest() {
        List<Object> objList = new ArrayList<>();
        List<String> strList = new ArrayList<>();

        objList.add(strList);
    }


    public static void swap(List<?> list, int i, int j) {
        swapHelper(list, i, j);
    }

    private static <E> void swapHelper(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }
}
