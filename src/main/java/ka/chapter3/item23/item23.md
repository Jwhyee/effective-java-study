# 태그 달린 클래스보다는 클래스 계층구조를 활용하라.

## 태그 클래스

태그 달린 클래스란, 현재 상태에 대한 필드가 존재하는 클래스를 의미한다.

아래 클래스에 대한 코드를 확인해보면 `shape`라는 변수가 현재 `Figure`가 `RECTANGLE`인지 `CIRCLE`인지에 대한 상태를 반환하므로, 태그로 볼 수 있다.

```java
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
```

### 단점

이러한 태그 달린 코드의 가장 큰 문제점은 다음과 같다.

#### 쓸모 없는 코드가 많다.

열거 타입 선언, 태그 필드, switch문 등 만약 `CIRCLE`을 사용한다면,
`RECTANGLE`을 위한 `length`, `width`, `constructor`, `area()` 등등에 대한 메모리를 계속 지니게 된다.

#### 확장성이 좋지 않다.

`SQUARE`라는 정사각형이 추가될 경우 `switch`와 여러 코드를 수정해야한다.
즉, OCP(개방 폐쇄 원칙)을 지키지 못하며, 새로운 의미를 추가할 때마다 무언가를 빠뜨리면, 런타임에 문제가 생길 것이다.

#### 구체 타입을 판별하기 어렵다.

아래와 같이 `instanceof`를 통해 해당 클래스의 타입을 정확히 알기 어렵다.

```java
if(figure instanceof Shape)
```

---

> 태그 달린 클래스는 장황하고, 오류를 내기 쉽고, 비효율적이다.

### 보완 방법

태그 달린 클래스를 클래스 계층 구조로 변환하는 방법은 루트(root)가 될 추상 클래스를 정의하고,
태그 값에 따라 동작이 달라지는 메소드들을 루트 클래스의 추상 메소드로 선언하면 된다.

#### 1. 추상 클래스 도입

태그(상태)마다 동작이 달라지는 `area()`와 같은 메소드는 추상 메소드로 정의하는 것이 바람직하다.

```java
public abstract class Figure {
    abstract double area();
}
```

#### 2. 도형 클래스 정의

앞서 정의한 추상 클래스를 상속 받은 도형 클래스들을 정의해주고, 각 도형에 맞는 면적 계산식으로 구현해준다.

```java
public class Circle extends Figure {

    final double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    double area() {
        return Math.PI * (radius * radius);
    }
}
```

```java
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
```

#### 3. 도형 클래스 활용

아래 코드와 같이 추상 클래스를 활용해 도형의 면적을 구하거나, 타입을 확인할 수 있다.

```java
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
```

## 정리

계층구조로 설계한 클래스는 태그 달린 클래스의 모든 단점을 날려버린다.

- 계층구조로 설계하면 코드가 간결하고, 명확해진다.
- 각 의미를 독립된 클래스에 담아 관련 없는 데이터 필드를 제거할 수 있다.
- 전체 필드를 `final`로 정의할 수 있다.
- 타입 검사를 쉽게할 수 있으며, 변수를 명시하거나 제한할 수 있다.