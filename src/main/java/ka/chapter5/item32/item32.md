# 제네릭과 가변인수를 함께 쓸 때는 신중하라.

## 가변인수와 제네릭

가변인수(varargs) 메소드와 제네릭은 JDK 5에서 함께 추가되었으니 잘 어우러지리라 기대하겠지만, 그렇지 않다.
가변인수는 메소드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해준다.

```java
public class VarargsTest {
    @Test
    void argsTest() {
        printArgs(1, 2, 3, 4, 5);
    }

    public void printArgs(int... args) {
        for (int arg : args) {
            System.out.printf("%d ", arg);
        }
    }
}
```

가변인수 메소드를 호출하면 해당 인수들을 담기 위한 배열이 자동으로 하나 만들어진다.

### 힙 오염 문제

배열이 하나 만들어지는 과정에서 내부로 감춰야 했을 이 배열을 클라이언트에 노출하는 문제가 생겼고,
그 결과 `varargs` 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.

```java
// 컴파일 경고 발생 : Possible heap pollution from parameterized vararg type
public void printListArgs(List<Integer>... args) {
    for (List<Integer> list : args) {
        for (Integer integer : list) {
            System.out.printf("%d ", integer);
        }
        System.out.println();
    }
}
```

우선 이전 아이템들에서 공부한 내용에 따르면 다음과 같다.

- 실체화 불가 타입은 런타임에는 컴파일 타임보다 타입 관련 정보를 적게 담고 있다.
- 거의 모든 제네릭과 매개변수화 타입은 실체화 되지 않는다.

그렇기 때문에 메소드를 선언할 때, 실체화 불가 타입으로 `varargs` 매개변수를 선언하면 위와 같은 컴파일 경고가 발생하는 것이다.
가변인수 메소드를 호출할 때에도 `varargs` 매개변수가 실체화 불가 타입으로 추론되면, 그 호출에 대해서도 경고를 낸다.

```bash
warning: [unchecked] Possible heap pollution from
    parameterized vararg type List<String>
```

매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다.

```java
public void printListArgs(List<String>... args) {
    List<Integer> intList = List.of(42);
    Object[] objects = args;
    // 힙 오염 발생
    objects[0] = intList;
    // 런타임 에러 발생 : ClassCastException
    String s = args[0].get(0);
}
```

위 코드를 보면 매개변수화 타입의 변수인 `args`에 다른 객체인 `intList`를 참조해 힙 오염이 발생했다.
이 메소드에는 형변환하는 곳이 보이지 않는데도 인수를 건네 호출하면 `ClassCastException`이 발생한다.
마지막 줄에 보이지 않는 형변환이 숨어있기 때문이다.

> **제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.**

### 왜 허용했을까?

제네릭 배열을 직접 생성하는걸 허용하지 않으면서, 제네릭 `varargs` 매개변수를 받는 메소드를 선언할 수 있게 한 이유는 무엇일까?

```java
// Code 28-3
List<String>[] stringLists = new List<String>[l]; List<Integer> intList = List.of(42);
Object[] objects = stringLists;
objects[0] = intList;
String s = stringLists[0].get(0);
```

```java
// Code 32-1
public void printListArgs(List<String>... args) {
    List<Integer> intList = List.of(42);
    Object[] objects = args;
    objects[0] = intList;
    String s = args[0].get(0);
}
```

코드 28-3은 컴파일 단계에서 에러를 내지만, 코드 32-1은 런타임에 되서야 에러가 나는 것을 알 수 있다.
이는, 제네릭이나 매개변수화 타입의 `varargs` 매개변수를 받는 메소드가 실무에서 매우 유용하기 때문이다.

```java
// Arrays
@SafeVarargs
@SuppressWarnings("varargs")
public static <T> List<T> asList(T... a)

// Collections
@SafeVarargs
public static <T> boolean addAll(Collection<? super T> c, T... elements)

// EnumSet
@SafeVarargs
public static <E extends Enum<E>> EnumSet<E> of(E first, E... rest)
```

그래도 위 코드들은 앞서 보여준 메소드와 달리 타입 안전하다.

### @SafeVarargs

Java 1.7 이전에는 `@SuppressWarnings("unchecked")`를 사용해 경고를 숨겼지만,
이후에는 `@SafeVarargs` 어노테이션을 사용해 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었다.
`@SafeVarargs`은 메소드 작성자가 그 메소드 타입 안점함을 보장하는 장치이다.
만약 메소드가 안전한 게 확실하지 않다면 절대 사용하면 안 된다.

메소드가 안전한지 확인하는 방법은 다음과 같다.

1. 가변인수 메소드를 호출할 때, `varargs` 매개변수를 담는 제네릭 배열이 만들어진다.
2. 메소드가 이 배열에 아무것도 저장하지 않고, 참조가 밖으로 노출되지 않는타면 타입 안전하다.

```java
static <T> T[] toArray(T... args) {
    return args;
}
```

위 코드는 참조가 외부로 노출되기 때문에 절대 안전하지 않다.
반환하는 배열의 타입은 메소드에 인수를 넘기는 컴파일 타임에 결정되는데,
그 시점에는 컴파일러에게 충분한 정보가 주어지지 않아 타입을 잘못 판단할 수 있다.

```java
static <T> T[] pickTwo(T a, T b, T c) {
    return switch (ThreadLocalRandom.current().nextInt(3)) {
        case 0 -> toArray(a, b);
        case 1 -> toArray(a, c);
        case 2 -> toArray(b, b);
        default -> throw new AssertionError();
    };
}
```

컴파일러는 위 메소드를 보고 `toArray`에 넘길 T 인스턴스 2개를 담을 `varargs` 매개변수 배열을 만드는 코드를 생성한다.
이 코드가 만드는 배열의 타입은 `Object`인데, `pickTwo`에 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입이기 때문이다.

위 코드도 동일하게 실행하면 `ClassCastException`이 발생한다.
이유를 분석해보면, `pickTwo`의 반환값을 `attributes`에 저장하기 위해 `String[]`으로 형변환하는 코드를 컴파일러가 자동 생성하기 때문이다.
즉, `Object[]`는 `String[]`의 하위 타입이 아니므로 형변환이 실패하는 것이다.

### 안전한 코드

제네릭 `varargs` 매개변수 배열에 다른 메소드가 접근하도록 허용하면 안전하지 않다.
단, 두 가지 예외가 존재한다.

1. @SafeVarargs가 선언된 메소드를 또다른 `varargs` 메소드에 넘기는 것은 안전하다.
2. 이 배열 내용의 일부 함수를 호출만 하는 일반 메소드(varargs를 받지 않는)에 넘기는 것도 안전하다.

아래 코드를 확인해보자.

```java
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists) {
        result.addAll(list);
    }
    return result;
}
```

임의 개수의 리스트를 인수로 받아 받은 순서대로 그 안의 모든 원소를 하나의 리스트로 옮겨 담아 반환한다.
이렇게 `@SafeVarargs` 애노테이션을 사용할 때를 정하는 규칙은 간단하다.

> 제네릭이나 매개변수화 타입의 `varargs` 매개변수를 받는 모든 메소드에 붙리면 된다.

안전하지 않은 `varargs` 메소드는 절대 작성하지 말고,
통제할 수 있는 메소드 중 제네릭 `varargs`를 사용하며, 힙 오염 경고가 뜨는 메소드가 있다면,
진짜 안전한지 검사한 후 어노테이션을 붙이자.

- varargs 매개변수 배여렝 아무것도 저장하지 않는다.
- 그 배열(혹은 복제본)을 신뢰할 수 없는 코드에 노출하지 않는다.

위 두 조건 중 하나라도 어겼다면 무조건 수정하자.

## 정리

- 가변인수와 제네릭은 궁합이 좋지 않다.
- 가변인수 기능은 배열을 노출하여 추상화가 완벽하지 못하다.
  - 배열과 제네릭의 타입 규칙이 서로 다르기 때문이다.
- 제네릭 varargs 매개변수는 타입 안전하지는 않지만, 허용된다.
- 메소드에 제네릭(혹은 매개변수화된) varargs를 선언하고자 한다고 가정하자.
  - 먼저 그 메소드가 타입 안전한지 확인하자.
  - 그리고 @SafeVarargs 어노테이션을 달아 사용하는데 불편함이 없게끔 하자.
