package ka.chapter2.item4.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OperationTest {
    @Test
    void addTest() {
        int result = IntegerMathUtil.operate(10, 20, new AddOperation());
        assertTrue(result == 30);
    }

    @Test
    void subtractTest() {
        int result = IntegerMathUtil.operate(10, 20, new SubtractOperation());
        assertTrue(result == -10);
    }
}
