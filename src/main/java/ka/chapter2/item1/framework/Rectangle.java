package ka.chapter2.item1.framework;

public class Rectangle implements Shape {

    private final int width;
    private final int heigt;

    public Rectangle(int width, int height) {
        this.width = width;
        this.heigt = height;
    }

    @Override
    public void draw() {
        System.out.printf("가로가 %dcm이고, 높이가 %dcm인 직사각형을 그립니다.\n", width, heigt);
    }
}
