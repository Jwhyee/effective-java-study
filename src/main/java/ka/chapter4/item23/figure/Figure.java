package ka.chapter4.item23.figure;

public class Figure {
    enum Shape {RECTANGLE, CIRCLE};

    final Shape shape;

    // 사각형(RECTANGLE)일 경우 사용
    double length, width;

    // 원(CIRCLE)일 경우 사용
    double radius;

    public Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    public Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        return switch (shape) {
            case CIRCLE -> Math.PI * (radius * radius);
            case RECTANGLE -> length * width;
            default -> throw new AssertionError(shape);
        };
    }
}
