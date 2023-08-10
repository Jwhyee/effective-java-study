# 인스턴스화를 막으려거든 private 생성자를 사용하라.

`item3`을 공부하면서 느낀 것은 '싱글톤이 아닌, 정적 메소드와 정적 필드만을 사용하면 더 편하지 않을까?'라는 것이다.

> 이따금 단순히 정적 메소드와 정적 필드만을 담은 클래스를 만들고 싶을 때가 있을 것이다.
> **객체 지향적으로 사고하지 않는 이들이 종종 남용하는 방식**이기에 그리 곱게 보이지는 않지만,
> 분명 나름의 쓰임새가 있다.

## 왜 객체지향적이지 않은가?

> 객체지향 프로그래밍이란, 객체 간 협력과 상호 작용을 중요시한다.

### 1. 캡슐화 원칙 위반

> 캡슐화란, 외부로부터 클래스에 정의된 속성과 기능들을 보호하고,
> 필요한 부분만 외부로 노출될 수 있도록 하여,
> 각 객체 고유의 독립성과 책임 영역을 안전하게 지키고자 하는 것이 목표이다.

아래와 같이 정적 필드와 정적 메소드만 이용한 클래스가 있다.
기본적으로 현재 시간을 기준으로 작동하지만, 사용자가 시간을 변경할 수 있다. 

```java
public class StaticDateTimeUtil {
    public static LocalDateTime localDateTime = LocalDateTime.now();

    public static String getPassedTime() {
        ...
    }
}
```

```java
public class UtilTest {
    @Test
    void staticUtilTest1() {
        StaticDateTimeUtil.localDateTime = LocalDateTime.now().minusHours(2);
        String passedTime = StaticDateTimeUtil.getPassedTime();
        System.out.println(passedTime);
        // 테스트 통과
        assertTrue(passedTime.equals("2시간 전"));
    }

    @Test
    void staticUtilTest2() {
        String passedTime = StaticDateTimeUtil.getPassedTime();
        System.out.println(passedTime);
        // 테스트 실패
        assertTrue(passedTime.equals("0초 전"));
    }
}
```

만약 위와 같은 테스트를 한다고 가정할 경우,
1번 테스트에서 시간을 바꿔놨기 때문에 2번 테스트 또한 그 영향을 받게된다.

즉, 여러 곳에서 객체를 공유하기 때문에 상태를 독립적으로 관리할 수 없어 캡슐화 원칙에 위배된다.

### 2. 개방-폐쇄 원칙(OCP) 위배

> 소프트웨어의 엔티티(클래스, 모듈 등)는 확장에는 열려 있어야하고, 수정에는 닫혀 있어야 한다.

아래 코드를 보면, 정수에 대한 더하기와 빼기에 대한 기능이 정적 메소드로 담겨있다.
만약, 곱하고, 나누는 기능을 추가하려면 `MathUtil` 클래스 자체를 수정해야한다.

```java
public class IntegerMathUtil {
    public static int add(int a, int b) {
        return a + b;
    }

    public static int subtract(int a, int b) {
        return a - b;
    }
}
```

OCP 원칙을 지키려면 아래와 같이 구분지어 사용해야한다.

```java
public interface Operation {
    int operate(int a, int b);
}
```

```java
public class AddOperation implements Operation {
    @Override
    public int operate(int a, int b) {
        return a + b;
    }
}
```

```java
public class SubtractOperation implements Operation {
    @Override
    public int operate(int a, int b) {
        return a - b;
    }
}
```

```java
public class MultipleOperation implements Operation {
    @Override
    public int operate(int a, int b) {
        return a * b;
    }
}
```

위와 같이 연산에 대한 `interface`를 생성하고,
어떤 연산(덧셈, 뺄셈, 곱셈 등)을 할지에 대한 구현체들을 만들어주면 기능을 쉽게 확장할 수 있다.

```java
public class IntegerMathUtil {
    public static int operate(int a, int b, Operation operation) {
        return operation.operate(a, b);
    }
}
```

```java
public class OperationTest {
    @Test
    void addTest() {
        int result = IntegerMathUtil.operate(10, 20, new AddOperation());
        assertTrue(result == 30);
    }

    @Test
    void subtractTest() {
        int result = IntegerMathUtil.operate(10, 20, new SubtractOperation());
        assertTrue(result == -10);
    }
}
```

마지막으로 `MathUtil`을 통해 클라이언트 코드에서
연산할 숫자와 어떤 연산을 할 것인지에 대한 구현체를 넣어주면 완성이다.

이를 통해 새로운 연산을 추가할 때,
클래스를 수정하지 않고도, 새로운 `Operation` 구현체를 생성하여 확장할 수 있다.

## 언제 사용할까?

### 기본 타입 값을 정의할 때

```java
// java.lang.Math
public final class Math {
    public static final double E = 2.7182818284590452354;
    public static final double PI = 3.14159265358979323846;
    private static final double DEGREES_TO_RADIANS = 0.017453292519943295;
    private static final double RADIANS_TO_DEGREES = 57.29577951308232;
}
```

해당 클래스에서는 수학적 연산을 하기 위해 상수로 두었지만,
`PI`와 같은 경우는 수학적 연산을 할 때 사용할 수 있기 때문에 사용자가 필드를 가져와 사용할 수 있도록 하였다.

이와 같이 기본 타입 값을 정의하는 용도로 사용한다.

### 인터페이스 구현체를 생성할 때

```java
// java.util.Collections
public class Collections {
    // 비어 있는 리스트 생성
    public static final <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }
    // 불변 싱글톤 리스트 생성
    public static <T> List<T> singletonList(T o) {
        return new SingletonList<>(o);
    }
}
```

해당 클래스에는 다양한 정적 메소드(팩터리)가 존재한다.
그 중 `List` 인터페이스를 구현하는 객체를 생성해주는 기능이 있다.

이와 같이 인터페이스에 대한 구현체 객체를 생성해줄 때 사용하기도 한다.

### final 클래스와 관련 메소드를 모아놓을 때

```java
public final class MathUtils {
    private MathUtils() {}

    public static int add(int a, int b) {
        return a + b;
    }

    public static int sub(int a, int b) {
        return a - b;
    }

    public static int mul(int a, int b) {
        return a * b;
    }
}
```

위 클래스는 `final` 클래스기 때문에 다른 클래스에서 상속 받을 수 없다.
때문에 정적 메소드만을 활용해 유틸리티 클래스처럼 활용이 가능하다.

이런 유틸리티 클래스는 인스턴스로 만들어 쓰려고 설계한게 아니다.
때문에 `private` 생성자를 명시적으로 작성해줘야한다.

## 인스턴스화를 어떻게 막을까?

추상 클래스로 만드는 것으로는 인스턴스화를 막을 수 없다.

```java
public abstract class DateUtil {

    protected DateUtil() { }

    public static String currentTime() {
        return LocalDateTime.now().toString();
    }

}
```

생성자를 `protected`를 통해 인스턴스화를 막을 수 있도록 하였지만,
아래와 같이 상속받는 하위 클래스를 통해 인스턴스화를 뚫을 수 있다.

```java
public class ConcreteDateUtil extends DateUtil {

}
```

```java
public class UtilityTest {
    @Test
    void test1() {
        ConcreteDateUtil util = new ConcreteDateUtil();
        // 테스트 통과
        assertTrue(util instanceof  DateUtil);
    }
}
```

이를 해결하기 위해 간단하게 생성자만 `private`로 바꿔주면 인스턴스화를 막을 수 있다.

```java
public abstract class DateUtil {
    private DateUtil() {
        throw new AssertionError();
    }
}
```

명시적 생성자가 `private`다 보니 클래스 바깥에서 접근할 수 없게 된다.
즉, 상속도 불가능하며, 인스턴스화 자체를 할 수 없게 된다.
`item3`에서 봤던 `Reflection`을 통해 생성자에 접근하더라도 `Exception`을 통해 접근할 수 없게 만든다.