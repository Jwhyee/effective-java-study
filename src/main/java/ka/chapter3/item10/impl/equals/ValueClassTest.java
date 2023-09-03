package ka.chapter3.item10.impl.equals;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValueClassTest {
    @Test
    void test1() {
        Integer a = 10;
        Integer b = 20;

        assertTrue(a == (b - 10));
    }

    @Test
    void decimalFloatTest() {
        float a = 0.1f - 0.0f;
        float b = 0.3f;

        assertTrue(a + 0.2f == b);
    }
}