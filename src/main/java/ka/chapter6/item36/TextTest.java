package ka.chapter6.item36;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static ka.chapter6.item36.Text.Style.BOLD;
import static ka.chapter6.item36.Text.Style.ITALIC;

public class TextTest {
    @Test
    void test() {
        Text text = new Text();
        text.applyStyles(EnumSet.of(BOLD, ITALIC));
    }
}
