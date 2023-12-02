package ka.chapter4.item15.lsp;

import org.junit.jupiter.api.Test;

public class LspTest {
    @Test
    void castingTest() {
        ParentClass p = new ChildClass();
        p.print();
    }
}
