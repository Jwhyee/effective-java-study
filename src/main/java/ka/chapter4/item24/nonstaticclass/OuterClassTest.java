package ka.chapter4.item24.nonstaticclass;

import org.junit.jupiter.api.Test;

public class OuterClassTest {
    @Test
    void test() {
        OuterClass o = new OuterClass();
        o.outerMethod(100);
    }

    @Test
    void instanceTest() {
        OuterClass o = new OuterClass();
        OuterClass.NonStaticInnerClass nsic = o.new NonStaticInnerClass();

        nsic.innerMethod();
    }

}
