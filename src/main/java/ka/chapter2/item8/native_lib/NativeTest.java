package ka.chapter2.item8.native_lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NativeTest {
    @Test
    void test() {
        NativeClass nativeClass = new NativeClass();
        int result = nativeClass.calculateSum(10, 20);
        assertTrue(result == 30);
    }
}
