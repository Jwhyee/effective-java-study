package ka.chapter4.item16;

import org.junit.jupiter.api.Test;

import java.awt.Point;

public class TestClass {
    @Test
    void pointTest() {
        Point point = new Point();
        point.x = 10;
        point.y = 20;

        point.translate(1, 2);

        System.out.println(point);
    }
}
