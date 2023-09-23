package ka.chapter3.item14.field;

import org.junit.jupiter.api.Test;

public class FieldCompareToTest {
    @Test
    void floatFieldTest1() {
        int compare = Float.compare(0.1f, 0.2f);
        System.out.println(compare);
    }

    @Test
    void floatFieldTest2() {
        float f1 = 0.1f;
        float f2 = 0.2f;
        int compare = new Float(f1).compareTo(new Float(f2));
        System.out.println(compare);
    }
}
