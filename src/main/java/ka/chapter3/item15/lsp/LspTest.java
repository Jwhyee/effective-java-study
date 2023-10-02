package ka.chapter3.item15.lsp;

import org.junit.jupiter.api.Test;

public class LspTest {
    @Test
    void castingTest() {
        ParentClass p = new ChildClass();
        p.print();
    }
}
