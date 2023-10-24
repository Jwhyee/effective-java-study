package ka.chapter3.item23.refactor;

public class Rectangle extends Figure {

    final double height, width;

    public Rectangle(double height, double width) {
        this.height = height;
        this.width = width;
    }

    @Override
    double area() {
        return width * height;
    }
}
