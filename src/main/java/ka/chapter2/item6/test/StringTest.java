package ka.chapter2.item6.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StringTest {
    @Test
    void equalsTest1() {
        String str1 = new String("abc");
        String str2 = "abc";

        assertTrue(str1 == str2);
    }

    @Test
    void equalsTest2() {
        String str1 = new String("abc");
        String str2 = "abc";

        assertTrue(str1.equals(str2));
    }

    @Test
    void equalsTest3() {
        String str1 = "abc";
        String str2 = "abc";

        assertTrue(str1 == str2);
    }

    @Test
    void equalsTest4() {
        String str1 = new String("abc").intern();
        int str1HashCode = System.identityHashCode(str1);

        String str2 = "abc";
        int str2HashCode = System.identityHashCode(str2);

        System.out.println("str1HashCode = " + str1HashCode);
        System.out.println("str2HashCode = " + str2HashCode);

        assertTrue(str1 == str2);
    }
}
