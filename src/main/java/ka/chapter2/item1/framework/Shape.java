package ka.chapter2.item1.framework;

import java.util.InputMismatchException;

public interface Shape {
    void draw();

    static Shape createRectangle(int width, int height) {
        return new Rectangle(width, height);
    }

    static Shape createCircle(int radius) {
        return new Circle(radius);
    }

    static Shape createShape(String sort, int w, int h, int r) {
        if (sort.equals("rectangle")) {
            return new Rectangle(w, h);
        } else if (sort.equals("circle")) {
            return new Circle(r);
        } else {
            throw new InputMismatchException("...");
        }
    }
}
