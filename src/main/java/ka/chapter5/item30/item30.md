# 이왕이면 제네릭 메소드로 만들라.

## 제네릭 메소드

클래스와 마찬가지로, 메소드도 제네릭으로 만들 수 있다.

```java
public static Set union(Set s1, Set s2) {
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result;
}
```

위 코드를 작성하면 컴파일은 가능하지만 경고가 발생한다.

```bash
Raw use of parameterized class 'HashSet' 
Unchecked call to 'HashSet(Collection<? extends E>)' as a member of raw type 'java.util.HashSet' 

Unchecked call to 'addAll(Collection<? extends E>)' as a member of raw type 'java.util.Set'
```

왜 이러한 비검사 경고가 발생하는지 분석해보면 다음과 같다.

```java
@Test
void unionTest() {
    Set<Node> nodeSet = Set.of(new Node(1, 2), new Node(3, 4));
    Set<Integer> integerSet = Set.of(1, 2, 3, 4);
    Set union = union(nodeSet, integerSet);
    System.out.println(union);
}
```

위와 같이 아예 타입이 다른 `Set` 2개를 생성한 뒤, `union` 함수에 넣어줬다.
이후 출력되는 결과를 보면 다음과 같다.

```bash
[1, 2, chapter5.item30.UnionTest$Node@6d3af739, 3, chapter5.item30.UnionTest$Node@543788f3, 4]
```

여기서 `union` 함수에 넣기 전에는 제네릭 타입이었지만, 나온 결과는 로타입을 반환한다.
이러한 경고를 없애고, 타입 안전하게 만들기 위해서는 다음과 같이 수정하면 된다.

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```

이와 같이 반환값도 `<E>` 타입으로 반환되도록 메소드에 지정을 해주고, 모든 타입을 동일한 `E`로 맞춰주면 100% 타입 안전한 코드가 된다.

```java
@Test
void unionTest() {
    Set<Integer> integerSet1 = Set.of(1, 2, 3, 4);
    Set<Integer> integerSet2 = Set.of(5, 6, 7, 8);
    Set<Integer> union = union(integerSet1, integerSet2);
    System.out.println(union);
}
```

## 항등함수(identity function)

> 항등함수란, 주어진 입력 값에 대해 동일한 값을 반환하는 함수를 의미

항등함수 객체는 상태가 없기 때문에 요청할 때마다 새로 만드는 것은 낭비이다.
제네릭이 실체화된다면 항등함수를 타입별로 하나씩 만들어야 했겠지만, 소거 방식을 사용한 덕에 제네릭 싱글턴 하나면 충분하다.

코드로 예시를 확인해보자.

```java
public static UnaryOperator<String> IDENTITY_FN = (t) -> t + t;

@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction() {
    return (UnaryOperator<T>) IDENTITY_FN;
}
```

```java
@Test
void functionTest() {
    String[] strs = {"삼베", "대마", "나일론"};
    UnaryOperator<String> sameString = identityFunction();
    for (String s : strs) {
        System.out.println(sameString.apply(s));
    }
}
```

```
삼베삼베
대마대마
나일론나일론
```

위와 같은 결과가 나오는 이유는 `identityFunction`으로 구현된 `UnaryOperator`가 함수형 `apply`를 `IDENTITY_FN`로 지정했기 때문이다.
즉, `apply(s)`에서 사용한 `s`는 `IDENTITY_FN = (t)`의 `t`와 동일해지며, `t + t` 연산을 통해 동일한 단어가 두 번 반복해 나오게 되는 것이다.

이와 같이 제네릭이 실체화 될 경우 타입별로 하나씩 만들어야하지만, 항등함수의 경우에는 돌어온 값이 그대로 나와야하기 때문에 다음과 같이 구현이 가능하다.

```java
public static UnaryOperator<Object> IDENTITY_FN = (t) -> t;
```

`Object` 타입은 더하기 연산을 사용할 수 없기에 단순히 들어온 값을 그대로 내보낼 수 밖에 없다.

### 재귀적 타입 한정(recursive type bound)

> 재귀적 타입 한정이란, 주로 타입의 자연적 순서를 정하는 `Comparable` 인터페이스와 함께 사용된다.

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```

`Comparable` 인터페이스는 `compareTo`라는 하나의 메소드만 보유하고 있다.
이 메소드는 말 그대로 현재 객체와 비교할 객체(동일한 타입)로 들어온 `o`와 비교할 수 있게 하는 것이다.

이 인터페이스는 주로 객체의 정렬 혹은 최소, 최댓값을 구할 때 많이 사용된다.

```java
public static <E extends Comparable<E>> E max(Collection<E> c);
```

타입 한정인 `<E extends Comparable<E>>`는 굉장히 복잡해 보이지만, 생각보다 쉽게 해석할 수 있다.

> 모든 타입 `E`는 자신과 비교할 수 있다.

```java
public static <E extends Comparable<E>> E max(Collection<E> c) {
    if(c.isEmpty()) throw new IllegalArgumentException("빈 컬렉션");

    E result = null;
    for (E e : c) {
        if (result == null || e.compareTo(result) > 0) {
            result = Objects.requireNonNull(e);
        }
    }

    return result;
}
```

재귀적 타입 한정은 훨씬 복잡해질 가능성이 있긴 하지만, 다행히 그런 일은 잘 일어나지 않는다.

## 정리

- 명시적으로 형변환해야하는 메소드보다 제네릭 메소드가 더 안전하며, 사용하기 쉽다.
- 메소드도 형변환 없이 사용할 수 있는 편이 좋다.
- 형변환을 해주야하는 기존 메소드는 제네릭하게 만들자.