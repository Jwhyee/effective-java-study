package ka.chapter5.item28;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {
    @Test
    void objArrTest() {
        Object[] objectArr = new Long[1];
        // 에러 발생 : java.lang.ArrayStoreException: java.lang.String
        objectArr[0] = "타입이 달라 넣을 수 없다.";
    }

    /*@Test
    void objListTest() {
        // String 타입 리스트의 배열 생성
        List<String>[] strList = new List<String>[1];
        // Integer 타입 Immutable 리스트 생성
        List<Integer> intList = List.of(42);

        // Object 타입 배열 생성 후 문자열 리스트 배열 주입
        Object[] objects = strList;
        // 배열 내부 0번 인덱스에 리스트 추가
        objects[0] = intList;

        String s = strList[0].get(0);
        System.out.println("s = " + s);
    }*/
}
