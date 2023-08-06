package ka.chapter2.item1;

import ka.chapter2.item1.framework.Rectangle;
import ka.chapter2.item1.framework.Shape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Item1Main {

    @Test
    @DisplayName("1) 문자열을 정수형으로 변환")
    void parseIntTest1()  {
        String s = "1";
        int i = Integer.parseInt(s);
        assertTrue(i == 1);
    }

    @Test
    @DisplayName("2) 문자열을 정수형으로 변환")
    void parseIntTest2()  {
        String s = "1";
        Integer i = new Integer(s);
        assertTrue(i == 1);
    }

    @Test
    @DisplayName("1) static 필드 초기화")
    void staticTest1() {
        StaticClass.printNumber();
        StaticClass.number = 10;

        StaticClass.printNumber();
        assertTrue(StaticClass.number == 10);
    }

    @Test
    @DisplayName("2) static 필드 초기화")
    void staticTest2() {
        StaticClass.printNumber();
        assertTrue(StaticClass.number == 10);
    }

    @Test
    @DisplayName("1) probablePrime 테스트")
    void bigIntegerTest1() {
        int numBits = 10;
        int certainty = 10;
        Random random = new Random();

        BigInteger bi = new BigInteger(numBits, certainty, random);
        System.out.println(bi);
    }

    @Test
    @DisplayName("2) probablePrime 테스트")
    void bigIntegerTest2() {
        int numBits = 10;
        Random random = new Random();

        BigInteger bi = BigInteger.probablePrime(numBits, random);
        System.out.println(bi);
    }

    @Test
    @DisplayName("1) boolean 테스트")
    void booleanTest1() {
        Boolean bool = Boolean.valueOf("true");
        System.out.println(bool);
    }

    @Test
    @DisplayName("1) Singleton 테스트")
    void singletonTest1() {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();

        System.out.println("s1.toString() = " + s1.toString());
        System.out.println("s2.toString() = " + s2.toString());
        assertTrue(s1 == s2);
    }

    @Test
    @DisplayName("1) List 테스트")
    void listTest1() {
        List<Integer> list = new ArrayList<>();
//        List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        System.out.println(list);
        assertTrue(list.size() == 3);
    }

    @Test
    @DisplayName("2) List 테스트")
    void listTest2() {
        List<Integer> list = List.of(1, 2, 3);

        System.out.println(list);
        assertTrue(list.size() == 3);
    }

    @Test
    @DisplayName("1) Shape 테스트 - 직사각형")
    void shapeTest1() {
        Shape rectangle = Shape.createRectangle(20, 5);
//        Rectangle rectangle = (Rectangle) Shape.createRectangle(20, 5);
        rectangle.draw();
    }

    @Test
    @DisplayName("2) Shape 테스트 - 원")
    void shapeTest2() {
        Shape circle = Shape.createCircle(10);
        circle.draw();
    }

}
