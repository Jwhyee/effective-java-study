package ka.chapter3.item10.transitivity.lsp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnimalTest {
    @Test
    void test() {
        Animal cat = new Cat();
//        String move = cat.move(10, false);
        int move = cat.move(10);
        assertTrue(move == 1000);
    }
}
