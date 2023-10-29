package ka.chapter3.item23.refactor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FigureTest {
    @Test
    void figureTest() {
        Figure rect = new Rectangle(10.0, 20.0);
        assertThat(rect.area()).isEqualTo(200.0);
    }

    @Test
    void figureInstanceTest() {
        Figure circle = new Circle(13.0);

        assertThat(circle).isInstanceOf(Circle.class);
    }
}
