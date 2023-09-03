package ka.chapter3.item10.transitivity;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ColorTest {
    @Test
    void test() {
        Point p = new Point(1, 1);
        ColorPoint cp1 = new ColorPoint(1, 1, Color.BLACK);

        // 테스트 실패!
        assertTrue(cp1.equals(p));
    }

    @Test
    void transitivityTest() {
        ColorPoint cp1 = new ColorPoint(1, 1, Color.BLACK);
        Point p = new Point(1, 1);
        ColorPoint cp3 = new ColorPoint(1, 1, Color.BLUE);

        // 테스트 통과!
        assertTrue(cp1.equals(p));

        // 테스트 통과!
//        assertTrue(p.equals(cp3));

        // 테스트 실패!
//        assertTrue(cp1.equals(cp3));
    }

    private static final Set<Point> unitCircle = Set.of(
            new Point(1, 0), new Point(0, 1),
            new Point(-1, 0), new Point(0, -1)
    );

    private static boolean onUnitCircle(Point point) {
        return unitCircle.contains(point);
    }

    @Test
    void unitCircleTest() {
        assertTrue(onUnitCircle(new CounterPoint(1, 0)));
    }
}
