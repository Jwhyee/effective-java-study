package ka.chapter3.item10.impl.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookTest {
    @Test
    @DisplayName("반사성 테스트")
    void reflexiveTest() {
        Book b = new Book("Effective Java 3/E", 500);
        assertTrue(b.equals(b));
    }

    @Test
    @DisplayName("타입 검사 테스트1")
    void checkTypeTest1() {
        Book b1 = new Book("Effective Java 3/E", 500);
        Book b2 = new Book("Effective Java 3/E", 500);
        assertTrue(b1.equals(b2));
    }

}
