package ka.chapter3.item10.reflexivity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReflexivityTest {
    @Test
    void reflexivityTest() {
        List<BreakReflexivity> list = new ArrayList<>();
        BreakReflexivity br = new BreakReflexivity(1);
        list.add(br);

        // 테스트 실패!
        assertTrue(list.contains(br));
    }
}
