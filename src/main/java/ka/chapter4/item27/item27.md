# 비검사 경고를 제거하라.

제네릭을 사용하기 시작하면 수많은 컴파일러 경고를 보게 된다.
비검사 형변환 경고, 비검사 메소드 호출 경고, 비검사 배개변수화 가변인수 타입 경고, 비검사 변환 경고 등 제네릭에 익숙해질수록 마주치는 경고 수는 줄겠지만, 새로 작성한 코드가 한 번에 꺠끗하게 컴파일 되리라 기대하지는 말자.

대부분의 비검사 경고는 쉽게 제거할 수 있다.

```java
Set<Node> nodeSet = new HashSet();
```

위 코드에서는 컴파일러가 다이아몬드 연산자를 추가하지 않았다고 경고를 해준다.
대부분의 에러는 IDE에서 알려주는대로 수정하면 쉽게 해결할 수 있다.

하지만 이렇게 단순한 에러가 아닌 제거하기 훨씬 어려운 경고도 있다.
어렵다고 포기하지 않고, 모두 제거핟나면 그 코드는 타입 안전성이 보장되는 코드가 된다.

경고를 제거할 수는 없지만 타입 안전하다고 확신할 수 있다면 아래 어노테이션을 사용해도 좋다.

```java
public static void main(String[] args) {
    @SuppressWarnings("unchecked")
    Set<Node> nodeSet = new HashSet();
}
```

이 어노테이션에는 주의해야할 점이 있다.

1. 타입 안전함을 검증하지 않은 채 경고를 숨기면 스스로에게 잘못된 보안 인식을 심어주는 꼴이다.
2. 안전하다고 검증된 비검사 경고를 숨기지 않고 두면 진짜 문제를 알리는 새로운 경고가 나와도 눈치채기 어렵다.

이 어노테이션은 개별 지역변수 선언부터 클래스 전체까지 어떤 선언에도 달 수 있다.
하지만 `@SuppressWarnings` 어노테이션은 항상 가능한 좁은 범위에 적용하자.

1. 변수 선언
2. 아주 짧은 메소드
3. 생성자

넓은 범위에 해당 어노테이션을 달아 놓으면 정말 심각한 문제를 눈치채지 못할 수 있기 때문이다.
그러니 절대 클래스 전체에 적용해서는 안 된다.

한 줄이 넘는 메소드나 생성자가 달린 어노테이션을 발견하면 지역변수 선언 쪽으로 옮기자.

```java
@SuppressWarnings("unchecked")
public <T> T[] toArray(T[] a) {
    if (a.length < size)
        // Make a new array of a's runtime type, but my contents:
        return (T[]) Arrays.copyOf(elementData, size, a.getClass());
    System.arraycopy(elementData, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

`ArrayList`에서 가져온 위 코드를 보면 다음과 같은 경고가 발생한다.

```
unchecked cast
required: T[]
found:    Object[]
```

애너테이션은 선언에만 달 수 있기 때문에 `return`문에는 `@SuppressWarnings`를 다는게 불가능하다.

그렇기 때문에 바로 반환하는 것이 아닌 변수로 뺀 다음 어노테이션으로 비검사를 선언하고, 반환하는 방식으로 변경해주면 된다.

```java
public <T> T[] toArray(T[] a) {
    if (a.length < size) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Arrays.copyOf(elementData, size, a.getClass());
        return result;
    }
    System.arraycopy(elementData, 0, a, 0, size);
    if (a.length > size)
        a[size] = null;
    return a;
}
```

**이 어노테이션을 사용할 땐, 그 경고를 무시해도 안전한 이유를 항상 주석으로 남겨야 한다.**

다른 사람이 그 코드를 이해하는 데 도움이 되며, 더 중요하게는 다른 사람이 그 코드를 잘못 수정하여 타입 안전성을 잃는 상황까지 줄여준다.

## 정리

- 비검사 경고는 중요하니 무시하지 말자.
- 모든 비검사 경고는 런타임에 `ClassCastException`을 일으킬 수 있다.
  - 잠재적 가능성을 뜻하니 최선을 다해 제거하라.
- 경고를 없애기 어렵다면 가능한 한 범위를 좁혀 `@SuppressWarnings("unchecked")`를 사용하라.
  - 경고를 숨기기로 한 근거를 꼭 주석으로 남겨라.
