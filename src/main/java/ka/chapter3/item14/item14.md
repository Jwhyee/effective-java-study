# Comparable을 구현할지 고려하라.

## Comparable

`Comparable` 인터페이스를 살펴보면 `compareTo`라는 추상 메소드 하나만 존재한다.

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```

이름과 같이 매개변수로 들어온 것과 자신을 비교하는 것이다. 아래 차이점을 제외하면 `Object.equals()`와 동일한 기능을 한다.

- 단순 동치성 비교
- 순서 비교 + 제네릭

즉, `Comparable`을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서(natural order)가 있음을 뜻한다.

```java
public class ComparableTest {

    @Test
    void wordList() {
        String[] words = {"world", "banana", "apple"};
        Arrays.sort(words);
        // [apple, banana, world] 출력
        System.out.println(Arrays.toString(words));
    }

}
```

위와 같이 숫자가 아닌 문자열을 정렬할 수 있는 이유는 `String`이 `Comparable`을 구현했기 때문이다.

```java
public final class String implements Comparable<String>, ... {
    ...
}
```

이렇게 알파벳, 숫자, 연대와 같이 순서가 명확한 클래스를 작성할 경우 `Comparable` 인터페이스를 구현하도록 하자.

## Comparable 규약

> compareTo 메소드의 일반 규약은 equals 규약과 비슷하다.

```java
public interface Comparable<T> {
    /**
     *  이 객체와 주어진 객체의 순서를 비교한다.
     *  이 객체가 주어진 객체보다 작으면 음의 정수를, 같으면 0을, 크면 양의 정수를 반환한다.
     *  이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.
     * 
     *  아래에서 사용할 sgn(표기식) 표기는 수학에서 말하는 부호 함수(signum function)을 뜻한다.
     *  - 모든 x, y에 대해 sgn(x.compareTo(y)) == -sgn(y.compareTo(x))여야 한다.
     *  - 추이성을 보장해야 한다.
     *      - x.compareTo(y) > 0 && y.compareTo(z) && x.compareTo(z);
     *  - 모든 z에 대해 x.compareTo(y) == 0이면, sgn(x.compareTo(z)) == sgn(y.compareTo(z))이다.
     *  - 아래 사항은 필수는 아니지만 지키는 것이 좋다.
     *      - (x.compareTo(y) == 0) == (x.equals(y))여야 한다.
     *      - 만약 이 사항을 지키지 않을 경우 아래 내용을 명시해주어야 한다.
     *      - "주의 : 이 클래스의 순서는 equals 메소드와 일관되지 않다."
     * */
    public int compareTo(T o);
}
```

`compareTo`는 `equals`와 달리, 타입이 다른 객체를 신경쓰지 않아도 된다. 타입이 다른 객체가 주어지면 간단히 `ClassCastException`을 던지면 된다.

### 작성 요령

`compareTo` 메소드 작성 요령은 `equals`와 비슷하지만 몇가지 차이점이 있다.

#### Comparable은 제네릭 인터페이스이다.

`compareTo` 메소드의 인수 타입은 컴파일 타임에 정해진다.
즉, `equals`의 규약처럼 입력 인수의 타입을 확인 혹은 형변환할 필요가 없다.
인수의 타입이 잘못됐다면 컴파일 자체가 되지 않으며, `null` 값이 들어올 경우 `NPE`를 던져야한다.

#### compareTo는 순서를 비교한다.

`equals`의 경우 각 필드가 동치인지를 비교하는 것이었다면 `compareTo`는 순서를 비교한다.
객체 참조 필드를 비교하려면, `compareTo` 메소드를 재귀적으로 호출한다.

`Comparable`을 구현하지 않은 필드나, 표준이 아닌 순서로 비교해야한다면, `Comparator`를 대신 사용한다.
비교자는 직접 만들거나, 자바가 제공하는 것 중에 골라서 사용하면 된다.

#### 관계 연산자 사용 자제

Effective Java 2/E 에서는 실수 기본타입을 `Double.compare`, `Float.compare`를 사용하라고 권했었다.

```java
public class FieldCompareToTest {
    @Test
    void floatFieldTest() {
        int compare = Float.compare(0.1f, 0.2f);
        System.out.println(compare);
    }
}
```

```java
public final class Float implements Comparable<Float> {
    /**
     * 지정된 두 부동소수점 값을 비교합니다.
     * 반환되는 정수 값의 부호는 호출에 의해 반환되는 정수의 부호와 동일합니다
     * new Float(f1).compareTo(new Float(f2))
     * */
    public static int compare(float f1, float f2) {
        if (f1 < f2)
            return -1;
        if (f1 > f2)
            return 1;

        int thisBits    = Float.floatToIntBits(f1);
        int anotherBits = Float.floatToIntBits(f2);

        return (thisBits == anotherBits ?  0 :
                (thisBits < anotherBits ? -1 :
                        1));
    }
}
```

하지만 Java7 부터는 `javadoc`에 있는 설명처럼 박싱된 기본 타입 클래스들에 새로 추가된 정적 메소드인 `compare`를 이용하면 되는 것이다.

```java
public class FieldCompareToTest {
    @Test
    void floatFieldTest() {
        float f1 = 0.1f;
        float f2 = 0.2f;
        int compare = new Float(f1).compareTo(new Float(f2));
        System.out.println(compare);
    }
}
```

#### 핵심 필드 비교 순서

클래스 내부에 핵심 필드가 여러 개가 존재할 경우 무엇을 먼저 비교하느냐가 중요해진다.
이는 아래 4번 규약에서 설명하도록 하겠다.

### 규약

1, 2, 3번 규약은 `compareTo` 메소드로 수행하는 동치성 검사도 `equals` 규약과 똑같이 반사성, 대칭성, 추이성을 충족해야 한다.
`hashCode` 규약을 지키지 못하면 해시를 사용하는 `HashMap`과 같은 클래스와 어울리지 못하듯, `compareTo` 규약을 지키지 못하면, 비교를 활용하는 클래스와 어울리지 못한다.

```java
public class Order implements Comparable<Order> {

    int num, fee;

    @Override
    public int compareTo(Order o) {
        int result = Integer.compare(num, o.num);
        if (result == 0) {
            result = Integer.compare(fee, o.fee);
        }
        return result;
    }
}
```

아래에서 조금 더 자세하게 규약을 살펴보도록 하자!

> 이 객체가 주어진 객체보다 작으면 음의 정수를, 같으면 0을, 크면 양의 정수를 반환한다.

#### 1번 규약

두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야한다.

- 첫 번째가 두 번째보다 작으면, 두 번째가 첫 번째 보다 커야한다.
- 첫 번째가 두 번째보다 크면, 두 번째는 첫 번째보다 작아야한다.
- 첫 번째가 두 번째와 같다면, 두 번째는 첫 번째와 같아야한다.

```java
@Test
void firstConditionTest() {
    // 첫 번째 객체가 두 번째 객체보다 작으면, 두 번째가 첫 번째 보다 커야한다.
    Order o1 = new Order(1, 15000);
    Order o2 = new Order(2, 30000);
    assertTrue(o1.compareTo(o2) == -1);

    // 첫 번째가 두 번째보다 크면, 두 번째는 첫 번째보다 작아야한다.
    o1 = new Order(2, 30000);
    o2 = new Order(1, 15000);
    assertTrue(o1.compareTo(o2) == 1);

    // 첫 번째가 두 번째와 같다면, 두 번째는 첫 번째와 같아야한다.
    o1 = new Order(1, 15000);
    o2 = new Order(1, 15000);
    assertTrue(o1.compareTo(o2) == 0);
}
```

#### 2번 규약

첫 번째가 두 번째보다 크고, 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다.

```java
@Test
void secondConditionTest() {
    // 첫 번째가 두 번째보다 크고, 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다.
    Order o1 = new Order(3, 45000);
    Order o2 = new Order(2, 30000);
    Order o3 = new Order(1, 15000);

    assertTrue(o1.compareTo(o2) == 1);
    assertTrue(o2.compareTo(o3) == 1);
    assertTrue(o1.compareTo(o3) == 1);
}
```

#### 3번 규약

크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야한다.

```java
@Test
void thirdConditionTest() {
    // 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야한다.
    Order o1 = new Order(1, 15000);
    Order o2 = new Order(1, 15000);
    Order o3 = new Order(1, 15000);

    assertTrue(o1.compareTo(o2) == 0);
    assertTrue(o2.compareTo(o3) == 0);
    assertTrue(o1.compareTo(o3) == 0);
}
```

#### 4번 규약

`compareTo` 메소드로 수행한 동치성 테스트 결과가 `equals`와 같아야 한다.

이를 잘 지키면 `compareTo`로 줄지은 순서와 `equals`의 결과가 일관되게 된다.

```java
@Override
public int compareTo(Order o) {
    int result = Integer.compare(num, o.num);
    if (result == 0) {
        result = Integer.compare(fee, o.fee);
    }
    return result;
}

@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Order order)) return false;
    return num == order.num && fee == order.fee;
}
```

`compareTo`의 순서와 `equals`의 결과가 일관되지 않아도 클래스는 잘 동작한다.
단, 이 클래스의 객체를 정렬된 컬렉션에 넣으면 해당 컬렉션이 구현한 인터페이스(Collections, Set, Map)에 정의된 동작과 엇박자를 낼 것이다.
기본적으로 이 인터페이스들은 `equals` 규약을 따르지만, 정렬된 컬렉션들은 동치성을 비교할 때 `equals`가 아닌 `compareTo`를 사용하기 때문이다.

아래 코드와 같이 `compareTo`는 `fee`를 먼저 비교하고, `equqls`에는 `num`을 먼저 비교하는 방식으로 테스트를 해보자!

```java
public class Order {
    @Override
    public int compareTo(Order o) {
        int result = Integer.compare(fee, o.fee);
        if (result == 0) {
            result = Integer.compare(num, o.num);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return num == order.num && fee == order.fee;
    }
}
```

```java
@Test
void collectionSortTest() {
    Set<Order> orderList = new TreeSet<>();

    for (int i = 1; i <= 5; i++) {
        orderList.add(new Order(i, 15000));
    }

    orderList.add(new Order(6, 1500));

    orderList.stream()
            .forEach(System.out::println);
}
```

```bash
Order{num=6, fee=1500}
Order{num=1, fee=15000}
Order{num=2, fee=15000}
Order{num=3, fee=15000}
Order{num=4, fee=15000}
Order{num=5, fee=15000}
```

`TreeSet`과 같이 정렬된 컬렉션을 넣을 때는 `compareTo`에 영향을 받는다는 것을 알 수 있다.
`compareTo`를 하는 과정에서 `fee`를 먼저 비교하도록 했기 때문에 이와 같은 일이 발생한다.
이렇게 `equals`와 순서를 다르게 구현할 경우 엇박이 날 수 있다.

```java
@Override
public int compareTo(Order o) {
    int result = Integer.compare(num, o.num);
    if (result == 0) {
        result = Integer.compare(fee, o.fee);
    }
    return result;
}
```

```bash
Order{num=1, fee=15000}
Order{num=2, fee=15000}
Order{num=3, fee=15000}
Order{num=4, fee=15000}
Order{num=5, fee=15000}
Order{num=6, fee=1500}
```

순서를 `equals`와 맞출 경우(num을 먼저 비교) 정상적으로 출력되는 것을 알 수 있다.

`BigDecimal`의 경우를 살펴보자.

```java
@Test
void bigDecimalTest() {
    Set<BigDecimal> bigDecimalSet = new HashSet<>();
    bigDecimalSet.add(new BigDecimal("1.0"));
    bigDecimalSet.add(new BigDecimal("1.00"));
    assertTrue(bigDecimalSet.size() == 2);

    bigDecimalSet = new TreeSet<>();
    bigDecimalSet.add(new BigDecimal("1.0"));
    bigDecimalSet.add(new BigDecimal("1.00"));
    assertTrue(bigDecimalSet.size() == 1);
}
```

`TreeSet`을 사용했을 때, 같은 값으로 인식하는 이유는 `compareTo` 구현을 볼 수 있다.

```java
private final int scale;

public int compareTo() {
    if (scale == val.scale) {
        long xs = intCompact;
        long ys = val.intCompact;
        if (xs != INFLATED && ys != INFLATED)
            return xs != ys ? ((xs > ys) ? 1 : -1) : 0;
    }
    ...
}
```

위와 같이 `int` 값을 이용해서 비교하기 때문이다. 즉, 1.0과 1.00을 같은 값으로 보는 것이다.

## 활용 방식

### 메소드 연쇄 방식

Java 8부터 Comparator 인터페이스가 일련의 비교자 생성 메소드(Comparator construction method)와 팀을 꾸려 메소드 연쇄 방식으로 비교자를 생성할 수 있게 되었다.

```java
// 변경 전 코드
public class Order implements Comparable<Order> {
    @Override
    public int compareTo(Order o) {
        int result = Integer.compare(num, o.num);
        if (result == 0) {
            result = Integer.compare(fee, o.fee);
        }
        return result;
    }
}
```

```java
// 변경 후 코드
import static java.util.Comparator.*;
public class Order implements Comparable<Order> {
    private static final Comparator<Order> COMPARATOR =
            comparing((Order o) -> o.num)
                    .thenComparingInt(o -> o.fee);
    @Override
    public int compareTo(Order o) {
        return COMPARATOR.compare(this, o);
    }
}
```

이와 같이 훨씬 간결하게 사용할 수 있지만 약간의 성능 저하가 뒤따른다.

```bash
Comparator 객체 생성 오버헤드
- Comparator를 생성하는 과정에서 람다 표현식을 사용하면 람다 표현식을 컴파일하여 런타임에서 익명 클래스로 변환해야 합니다.
- 이 과정에서 추가적인 클래스 및 객체 생성이 발생하므로, 매번 정렬을 수행할 때마다 Comparator 객체를 생성하는 것은 성능에 부담을 줄 수 있습니다.

비교 함수 호출 오버헤드
- Comparator를 사용한 정렬은 Comparator 객체의 compare 메서드를 호출하여 원소를 비교합니다.
- 이 때 메서드 호출 오버헤드와 비교 함수 호출 오버허드가 추가로 발생합니다. 이는 원래 compareTo 메서드를 사용하여 비교할 때보다 성능 저하를 일으킬 수 있습니다.
```

아래 코드와 같이 `Comparator`의 `compare`를 바로 구현하는 방법도 있다.

```java
private static final Comparator<Order> hashCodeOrder = new Comparator<Order>() {
    @Override
    public int compare(Order o1, Order o2) {
        return o1.hashCode() - o2.hashCode();
    }
};
```

하지만 위 코드는 정수 오버플로우를 일으키거나, 부동소수점 계산 방식에 따른 오류를 낼 수 있다.
때문에 아래와 같은 방식으로 변경해서 사용하는 것이 좋다.

```java
// 방식1
private static final Comparator<Order> hashCodeOrder = new Comparator<Order>() {
    @Override
    public int compare(Order o1, Order o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
};

// 방식2
private static final Comparator<Order> hashCodeOrder = Comparator.comparingInt(o -> o.hashCode());
```

## 정리

- 순서를 고려해야하는 클래스라면 Comparable 인터페이스를 구현하자.
- 인스턴스들을 쉽게 정렬하고, 검색하고, 비교 기능을 제공하는 컬렉션과 어우러지도록 해야한다.
- compareTo 메소드에서 필드 값을 비교하는 >, < 연산자는 사용하지 않는 것이 좋다.
- 박싱된 기본 타입 클래스가 제공하는 compare 메소드나 Comparator 인터페이스가 제공하는 비교자 생성 메소드를 사용하자.