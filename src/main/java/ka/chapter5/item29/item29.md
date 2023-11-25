# 이왕이면 제네릭 타입으로 만들라

JDK가 제공하는 제네릭 타입과 메소드를 사용하는 일반적으로 쉬운 편이지만, 제네릭 타입을 새로 만드는 일은 조금 더 어렵다.

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    private void ensureCapacity() {
        if(elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```

위 코드는 이전에 구현했던 단순한 스택 코드이다.
우리가 흔히 사용하는 스택과 같이 제네릭을 도입하면 아래와 같다.

```java
public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new E[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    private void ensureCapacity() {
        if(elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```

위와 같이 작성하면 생성자에서 에러가 발생한다.

```java
public Stack() {
    // 에러 발생 : Type parameter 'E' cannot be instantiated directly
    elements = new E[DEFAULT_INITIAL_CAPACITY];
}
```

이전 아이템에서 본 것과 같이 실체화 불가 타입으로는 배열을 만들 수 없기 때문이다.

이를 해결하기 위해서는 다음과 같이 조치할 수 있다.

### 방법 1

```java
elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
```

에러는 사라졌지만 경고가 발생한다. 동일하게 이전 아이템에서 설명한 것처럼 비검사 형변환이 프로그램의 타입 안전성을 해칠 수 있기 때문이다.

위 코드는 우리가 직접 설계하였고, `elements` 배열이 절대 클라이언트로 반환되거나, 다른 메소드에 전달되는 일이 전혀 없기 때문에 이 비검사 형변환은 확실히 안전하다.

이렇게 형변환이 안전할 경우 **아이템27**에서 나온 것처럼 범위를 최소로 좁혀 `@SuppressWarnings` 어노테이션으로 경고를 숨기자.

```java
@SuppressWarnings("unchecked")
public Stack() {
    elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
}
```

현재 코드에서 생성자가 비검사 배열 생성 말고는 하는 일이 없기 때문에 생성자 자체에 어노테이션을 붙여도 된다.

### 방법 2

```java
private Object[] elements;

public Stack() {
    elements = new Object[DEFAULT_INITIAL_CAPACITY];
}
```

배열의 타입을 `Object`로 바꿔보면, `pop` 메소드에서 `result`를 구하는 부분에서 에러가 발생하게 된다.

```java
public E pop() {
    if(size == 0) throw new EmptyStackException();
    E result = elements[--size];
    elements[size] = null; // 참조 해제
    return result;
}
```

이 또한 `elements[]`의 반환 값이 `Object`인데 `E result`와 맞지 않아 에러가 발생하는 것이다.

```java
public E pop() {
    if(size == 0) throw new EmptyStackException();
    
    @SuppressWarnings("unchecked")
    E result = (E) elements[--size];
    
    elements[size] = null; // 참조 해제
    return result;
}
```

위와 같이 변경하면 에러가 발생하지 않고, 정상적으로 사용할 수 있다.

## 정리

첫 번째 방식은 형변환을 배열 생성 시 단 한 번만 해주면 되지만,
두 번째 방식은 배열에서 원소를 읽을 때마다 해줘야 한다.

실제로 첫 번째 방식을 더 선호하며, 자주 사용하지만 `E`가 `Object`가 아닌 한 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염을 일으킨다.

위 내용은 이전 아이템에 나온 **배열보다는 리스트를 우선**하라는 내용과 모순돼 보인다.

`java`가 리스트를 기본 타입으로 제공하지 않으므로, `ArrayList`와 같은 제네릭 타입도 결국은 기본 타입인 배열을 사용해 구현해야 한다.

또한 `HashMap`과 같은 제네릭 타입은 성능을 높일 목적으로 배열을 사용하기도 한다.

- 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭이 더 안전하고 쓰기 편하다.
- 새로운 타입을 설계할 때는 형변환 없이도 사용할 수 있도록 제네릭을 사용하라.
- 기존 타입 중 제네릭이었어야 하는게 있다면 제네릭 타입으로 변경하자.
- 기존 클라이언트에는 아무 영향을 주지 않으면서, 새로운 사용자를 훨씬 편하게 해준다.