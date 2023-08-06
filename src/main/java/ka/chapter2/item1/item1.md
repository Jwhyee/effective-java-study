# 생성자 대신 정적 팩터리 메서드를 고려하라.

## 정적 팩터리 메서드

### 정적(static)이란 뭘까?

Java를 처음 공부하면서 느낀 것은 `main()` 함수가 정말 길다는 것이다.

```java
public class TestClass {
    public static void main(String[] args) {

    }
}
```

Java를 공부하기 전에 C, C++을 공부했지만, `static`이라는 키워드를 처음봐서 생소했다.
간단하게 아래 코드를 통해 우리가 흔히 사용하는 기능 중에 `static`이 있는지 확인해보자.

```java
public class TestClass {
    @Test
    @DisplayName("1) 문자열을 정수형으로 변환")
    void parseIntTest1()  {
        String s = "1";
        int i = Integer.parseInt(s);
        assertTrue(i == 1);
    }

    @Test
    @DisplayName("2) 문자열을 정수형으로 변환")
    void parseIntTest2()  {
        String s = "1";
        Integer i = new Integer(s);
        assertTrue(i == 1);
    }
}
```

문자열을 정수형으로 변환할 때, 우리는 `Integer.parseInt()`라는 함수를 자주 사용한다.
이렇게 객체를 생성하지 않아도 내부 변수 및 메소드에 접근할 수 있는 이유는 뭘까?

그 이유는 바로 `static` 필드는 클래스가 로드될 때, 메모리에 올라가기 때문이다.
즉, 어플리케이션이 실행되는 중(런타임) 해당 클래스에 접근해 `static` 필드에 접근 할 때, 메모리에 할당되는 것이다.
`static` 필드는 메모리 전역에 공유되기 때문에 한 번 수정한 값은 어플리케이션이 종료될 때까지 계속 유지된다.
아래 테스트 코드를 통해 확인해보자!

```java
public class StaticClass {
    public static int number = 1;

    public static void printNumber() {
        System.out.println("number = " + number);
    }

}

public class TestClass {
    @Test
    @DisplayName("1) static 필드 초기화")
    void staticTest1() {
        StaticClass.printNumber();
        StaticClass.number = 10;

        StaticClass.printNumber();
        assertTrue(StaticClass.number == 10);
    }

    @Test
    @DisplayName("2) static 필드 초기화")
    void staticTest2() {
        StaticClass.printNumber();
        assertTrue(StaticClass.number == 10);
    }
}
```

위 두 테스트는 모두 최종적으로 `10`이라는 값을 출력한다.
만약 `StaticClass`를 객체로 생성하고, `number` 변수에 `static`을 붙이지 않았다면 `1`이 출력되었을 것이다.
즉, 한 번 수정한 `static` 필드는 그대로 계속 유지되고, 메모리를 공유한다는 사실을 알 수 있다. 

## 장점

### 1. 이름을 가질 수 있다.

> 생성자에 넘기는 매개변수와 생성자 자체만으로는 반환될 객체의 특성을 제대로 설명하지 못한다.
> 반면 정적 팩터리는 이름만 잘 지으면 반환될 객체의 특성을 쉽게 묘사할 수 있다.

```java
public class TestClass {
    @Test
    @DisplayName("1) probablePrime 테스트")
    void bigIntegerTest1() {
        int numBits = 10;
        int certainty = 10;
        Random random = new Random();

        BigInteger bi = new BigInteger(numBits, certainty, random);
        System.out.println(bi);
    }

    @Test
    @DisplayName("2) probablePrime 테스트")
    void bigIntegerTest2() {
        int numBits = 10;
        Random random = new Random();

        BigInteger bi = BigInteger.probablePrime(numBits, random);
        System.out.println(bi);
    }
}
```

위 코드에서 어떤 테스트가 **'값이 소수인 BigInteger를 반환한다.'**에 대한 기능일까?
솔직히 1번 테스트는 뭔 기능인지 알아도 '왜 저렇게 만들었지?'라는 생각이 먼저 든다.
정적 팩터리가 아니더라도 기능에 대한 네이밍만 잘 지어주면 누구나 쉽게 볼 수 있는 코드가 된다.
이런 장점은 `static` 필드를 사용할 때, 객체를 생성하지 않고 바로 사용할 수 있기 때문에 더욱 좋은 효과를 볼 수 있다.

### 2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.

> 불변 클래스(immutable class)는 인스턴스를 미리 만들어 놓거나,
> 새로 생성한 인스턴스를 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.

```java
public class TestClass {
    @Test
    @DisplayName("1) boolean 테스트")
    void booleanTest1() {
        Boolean bool = Boolean.valueOf("true");
        System.out.println(bool);
    }
}
```

위 코드는 문자열을 인자로 받아 그에 맞는 `Boolean` 값을 반환하는 코드이다.
`valueOf()`를 통해서 `Boolean` 타입으로 반환 받았지만, 이 기능은 `Boolean`에 대한 객체를 아예 생성하지 않는다.
코드를 조금 더 자세히 살펴보면 다음과 같다.

```java
public final class Boolean {
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    public static Boolean valueOf(String s) {
        return parseBoolean(s) ? TRUE : FALSE;
    }
}
```

사실 `Boolean`이라는 클래스가 로드 될 때, `TRUE`, `FALSE`에 대한 객체를 미리 생성해 놨다.
때문에 이미 메모리에 존재하는 상수를 가져와서 그대로 다시 사용하는 것이다.

반복되는 요청에 같은 객체를 반환하는 식으로 정적 팩터리 방식의 클래스는 언제 어느 인스턴스를 살아 있게 할지를 철저히 통제할 수 있다.
이런 클래스를 인스턴즈 통제(instance-controlled) 클래스라고 부른다.
이렇게 인스턴스를 통제하는 이유는 다음과 같다.

- 싱글턴으로 만들 수 있다. == 인스턴스화를 못하게 만들 수 있다.
- 불변 값 클래스에서 동치인 인스턴스가 단 하나뿐임을 보장할 수 있다.

> 동치란 두 객체가 동등하거나 동일한 값을 가지는 것을 의미함.

### 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

> 반환할 객체의 클래스를 자유롭게 선택할 수 있게 하는 '엄청난 유연성'을 선물한다.
> API를 만들 때, 이 유연성을 응용하면 구현 클래스를 공개하지 않고도 그 객체를 반환할 수 있어 API를 작게 유지할 수 있다.
> 이는 인터페이스를 정적 팩터리 메서드의 반환 타입으로 사용하는 인터페이스 기반 프레임워크를 만드는 핵심 기술이기도 하다.

```java
public class TestClass {
    @Test
    @DisplayName("1) List 테스트")
    void listTest1() {
        List<Integer> list = new ArrayList<>();
        // List<Integer> list = new LinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        System.out.println(list);
        assertTrue(list.size() == 3);
    }

    @Test
    @DisplayName("2) List 테스트")
    void listTest2() {
        List<Integer> list = List.of(1, 2, 3);

        System.out.println(list);
        assertTrue(list.size() == 3);
    }
}
```

위 코드를 보면 우리가 직접적으로 `new`를 이용해서 `List`에 대한 객체를 생성하지 않았다.
하지만 `List`라는 인터페이스에 존재하는 `of()`라는 정적 팩터리 메서드를 통해 반환할 객체의 클래스를 자유롭게 선택할 수 있다. 

```java
public interface Shape {
    void draw();

    static Shape createRectangle(int width, int height) {
        return new Rectangle(width, height);
    }

    static Shape createCircle(int radius) {
        return new Circle(radius);
    }
}
```

위와 같이 도형을 만들기 위한 `interface`가 있고,
도형의 종류에는 직사각형(Rectangle)과 원(Circle)이 있다.

```java
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

public class Circle implements Shape {

    private final int radius;

    public Circle(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw() {
        System.out.printf("반지름이 %dcm인 원을 그립니다.\n", radius);
    }
}
```

각 도형에 대한 클래스는 모두 `Shape`에 대한 구현체이다.
그렇기 때문에 아래 코드와 같이 `Shape`로도 받을 수 있게 된다.

```java
public class TestClass {
    @Test
    @DisplayName("1) Shape 테스트 - 직사각형")
    void shapeTest1() {
        Shape rectangle = Shape.createRectangle(20, 5);
        rectangle.draw();
    }

    @Test
    @DisplayName("2) Shape 테스트 - 원")
    void shapeTest2() {
        Shape circle = Shape.createCircle(10);
        circle.draw();
    }
}
```

앞서 우리가 봤던 `List.of()`와 같은 형태이다.
이렇게 정적 팩터리 메서드를 사용하면 **구현체에 대한 객체를 유연하게 생성**할 수 있게 된다.

### 4. 입력 매게변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

> 반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관없다.
> 심지어 다음 릴리스에서는 또 다른 클래스의 객체를 반환해도 된다.

이 내용은 3번과 비슷한 내용으로 생각하면 된다.
만약 하나의 메소드를 이용해서 직사각형이나 원을 만들고 싶다면 아래와 같이 수정하면 끝이다.

```java
public interface Shape {
    void draw();

    static Shape createShape(String sort, int w, int h, int r) {
        if (sort.equals("rectangle")) {
            return new Rectangle(w, h);
        } else if (sort.equals("circle")) {
            return new Circle(r);
        } else {
            throw new InputMismatchException("...");
        }
        return null;
    }
}
```

### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

> 이런 유연함은 서비스 제공자 프레임워크(service provider framework)를 만드는 근간이 된다.
> 대표적인 서비스 제공자 프레임워크로는 JDBC(Java Database Connectivity)가 있다.
> 서비스 제공자 프레임워크에서의 제공자(provider)는 서비스의 구현체다.
> 그리고 이 구현체들은 클라이언트에 제공하는 역할을 프레임워크가 통제하여, 클라이언트를 구현체로부터 분리해준다.

## 단점

### 1. 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.

> 상속을 하려면 public 혹은 protected 생성자가 필요하다.
> 위에서 설명한 컬렉션 프레임워크의 유틸리티 구현 클래스들은 상속할 수 없다는 말이다.

```java
public final class Address {
    private final String city;
    private final String code;

    private Address(String city, String code) {
        this.city = city;
        this.code = code;
    }
    
    public static Address newInstance(String city, String code) {
        return new Address(city, code);
    }

    public String getCity() {
        return city;
    }

    public String getCode() {
        return code;
    }
}
```

위와 같이 `Address` 클래스가 존재한다.
이 `Address` 클래스를 다른 클래스에 상속할 경우 에러가 생기게 된다.
에러가 발생하는 이유는 2가지가 있다.

1. 불변 클래스
2. private 생성자

불변 클래스는 내부 상태를 변경할 수 없도록 설계가 되어있다.
때문에 객체가 한 번 생성되면 그 값이 끝까지 유지되어야 하고, 안전하게 공유될 수 있다.
만약 이런 불변 클래스를 상속할 경우 상속 받는 자식 클래스도 불변이어야 한다.
그 말은 즉, 생성자의 접근 제어자를 `private`가 아닌 다른 것으로 바꿔야 한다.

```java
public final class Person {
    private final String name;
    private final int age;
    // 컴포지션
    private final Address address;

    public Person(String name, int age, Address address) {
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
```

그렇기 때문에 위 코드와 같이 상속 대신 컴포지션(클래스에 다른 클래스 객체를 멤버로 가짐)을 사용하도록 유도하고,
불변 타입으로 만들려면 이 제약을 지켜야한다는 점에서 오히려 장점으로 받아들일 수도 있다.
불변 객체는 여러 곳에서 해당 객체를 참조하더라도 안전하게 공유된다.

### 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.

> 생성자처럼 API 설명에 명확히 드러나지 않으니
> 사용자는 정적 팩터리 메서드 방식 클래스를 인스턴스화할 방법을 알아내야 한다.

아래 사진은 `Scanner`에 대한 API 설명이다.

<center>
    <img src="https://github.com/Jwhyee/effective-java/assets/82663161/4a29bb6d-9e25-4da6-87dd-91f01e4e76de" alt="">
</center>

해당 클래스에는 객체를 생성하는 방법이 잘 나와 있다.
컬렉션 중 정적 팩터리를 이용해 인스턴스화 시키는 `List`는 잘 나와있지만,
이 외의 클래스에서는 찾아보기 어렵다.

때문에 아래와 같이 네이밍을 잘 지어놓는 것이 중요하다.

```java
// from
// 매개변수를 하나 받아, 해당 타입의 인스턴스를 반환하는 형변환 메소드
Date d = Date.from(instant)

// of
// 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
Set<Rank> faceCard = EnumSet.of(JACK, QUEEN, KING);

// valueOf
// from과 of의 더 자세한 버전
String s = String.valueOf(123);

// instance || getInstance
// 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스임을 보장하지 않는다.
StackWalker luke = StackWalker.getInstance(options);

// create || newInstance
// 위 내용과 비슷하지만, 매번 새로운 인스턴스를 생성해 반환함을 보장한다.
Object newArray = Array.newInstance(classObject, aryLength);
```

## 정리

정적 팩터리 메서드와 public 생성자는 각자의 쓰임새가 있으니
상대적인 장단점을 이해하고 사용하는 것이 좋다.
그렇다고 하더라도 정적 팩터리를 사용하는게 유리한 경우가 더 많으므로,
무작정 public 생성자를 제공하던 습관이 있다면 고치자!