package ka.chapter4.item19;

import ka.chapter4.item19.extend.Sub;
import org.junit.jupiter.api.Test;

public class TestClass {
    @Test
    void superCallTest() {
        Sub sub = new Sub();
        sub.overrideMe();
    }
}
