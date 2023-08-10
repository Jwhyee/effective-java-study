package ka.chapter2.item4.singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilityTest {
    @Test
    void test1() {
        ConcreteDateUtil util = new ConcreteDateUtil();
        assertTrue(util instanceof  DateUtil);
    }
}
