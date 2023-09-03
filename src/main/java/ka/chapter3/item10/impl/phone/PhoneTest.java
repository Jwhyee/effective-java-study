package ka.chapter3.item10.impl.phone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneTest {
    @Test
    void phoneTest() {
        int areaCode = 017;
        int prefix = 241;
        int lineNumber = 1234;
        PhoneNumber p = new PhoneNumber(areaCode, prefix, lineNumber);
        assertTrue(true);
    }

}
