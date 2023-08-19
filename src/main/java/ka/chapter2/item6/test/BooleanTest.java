package ka.chapter2.item6.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanTest {

    @Test
    void booleanObjectTest1() {
        Boolean b1 = new Boolean("true");
        assertTrue(b1 == true);
    }

    @Test
    void booleanObjectTest2() {
        assertTrue(Boolean.valueOf("true") == true);
    }

}
