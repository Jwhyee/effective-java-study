package ka.chapter2.item1.framework;

public class Circle implements Shape {

    private final int radius;

    protected Circle(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.printf("반지름이 %dcm인 원을 그립니다.\n", radius);
    }
}
