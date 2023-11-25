# 상속보다는 컴포지션을 사용하라.

> 컴포지션이란, 다른객체의 인스턴스를 자신의 인스턴스 변수로 포함해서 메서드를 호출하는 기법

> 아래에서 설명하는 '상속'은 클래스가 다른 클래스를 확장하는 구현 상속을 의미한다.
> 이번 아이템에서 논하는 문제는 클래스가 인터페이스를 구현하거나, 인터페이스가 다른 인터페이스를 확장하는 상속과는 무관하다.

상속은 코드를 재사용하는 강력한 수단이지만, 항상 최선은 아니다. 잘못 사용하면 오류를 내기 쉬운 소프트웨어를 만들게 된다.

상위 클래스와 하위 클래스를 모두 같은 프로그래머가 통제하는 패키지 안에서라면 상속도 안전한 방법이다.
확장할 목적으로 설계되었고, 문서화도 잘 된 클래스도 마찬가지로 안전하다.

하지만 일반적인 구체 클래스를 패키지 경계를 넘어, 즉 다른 패키지의 구체 클래스를 상속하는 일은 위험하다.

## 상속 시 주의할 점

### 메소드 호출과 달리 상속은 캡슐화를 깨트린다.

상위 클래스는 릴리즈마다 내부 구현이 달라질 수 있다. 즉, 그 여파로 인해 코드 한 줄 건드리지 않은 하위 클래스가 오동작할 수 있다는 것이다.
이를 대비해서 문서화라도 제대로 해두지 않으면, 하위 클래스는 상위 클래스의 변화에 맞게 계속 수정돼야만 한다.

```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;
    
    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```

위 코드를 토대로 아래와 같은 테스트를 작성하면 실패하게 된다.

```java
public class InstrumentedSetTest {
    @Test
    void addAllTest() {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(List.of("가", "나", "다"));
        
        // 테스트 실패!
        assertThat(s.getAddCount()).isEqualTo(3);
    }
}
```

분명 3개의 값을 추가했는데 `Actual : 6`이라는 값이 나오게 된다.
구현을 조금 더 깊이 살펴보면 `addAll()`에 문제가 있다.

```java
@Override
public boolean add(E e) {
    addCount++;
    return super.add(e);
}

@Override
public boolean addAll(Collection<? extends E> c) {
    addCount += c.size();
    return super.addAll(c);
}
```

여기서 사용되는 `addAll` 메소드는 분명 `HashSet.addAll()`을 호출하고 있다.
하지만, `addAll` 구현을 살펴보면 아래와 같이 되어있다.

```java
public boolean addAll(Collection<? extends E> c) {
    boolean modified = false;
    for (E e : c)
        if (add(e))
            modified = true;
    return modified;
}
```

여기서 `add()`를 호출하고 있는데, 이는 `HashSet.add()`이 아닌 하위 클래스에서 구현한 `InstrumentedHashSet.add()`를 호출하는 것이다.
이미 하위 클래스에서 해당 메소드를 재정의했기 때문에 이와 같은 일이 발생하는 것이다.

즉, `add`가 호출될 때마다, `addCount++`가 계속 호출되어 3번이 더 추가된 6이라는 값이 나오게 되는 것이다.

이러한 경우 하위 클래스에서 `addAll()` 메소드를 재정의하지 않으면 해결할 수 있긴하다.
하지만 지금 당장은 동작할지 몰라도, `HashSet.addAll()`이 `add()` 메소드를 이용해 구현했음을 가정한 해법이라는 한계를 지닌다.

> 상위 클래스는 릴리즈마다 내부 구현이 달라질 수 있다. 즉, 그 여파로 인해 코드 한 줄 건드리지 않은 하위 클래스가 오동작할 수 있다.

`addAll()` 메소드를 다른 식으로도 재정의할 수도 있다. 주어진 컬렉션을 순회하며 원소 하나당 `add()`를 한 번만 호출하는 것이다.
이 방식은 `HashSet.addAll()`을 더 이상 호출하지 않으니 `addAll()`이 `add()`를 사용하는지와 상관없이 결과가 옳다는 점에서 조금은 나은 해법이다.

하지만 여전히 상위 클래스의 메소드 동작을 다시 구현하는 방식은 어렵기도 하며, 시간도 더 들고, 자칫 오류를 내거나 성능을 떨어뜨릴 수도 있다.
또한, 하위 클래스에서는 접근할 수 없는 `private` 필드를 써야하는 상황이라면 이 방식으로는 구현 자체가 불가능하다.

### 상위 클래스에 새로운 메소드가 추가될 경우 하위 클래스는 깨질 수 있다.

다음 릴리즈에서 상위 클래스에 새로운 메소드가 추가될 경우를 생각해보자.

보안적인 이슈로 인해 컬렉션에 추가된 모든 원소가 특정 조건을 만족해야만 하는 프로그램일 경우,
그 컬렉션을 상속하여 원소를 추가하는 모든 메소드를 재정의해 필요한 조건을 먼저 검사하게끔 할 수 있다.

보안적인 이슈로 상위 클래스인 `HashSet`에 저장될 데이터가 문자열인 경우 `"sec"`이라는 단어가 포함되었는지 확인해야하는 조건이 추가되었다고 가정해보자. 

```java
public class CustomHashSet<E> extends HashSet<E> {
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (containsOnlyStrings(c)) {
            for (E e : c) {
                if(!((String) e).contains("sec")) return false;
            }
        }
        addCount += c.size();
        return super.addAll(c);
    }

    private boolean containsOnlyStrings(Collection<?> collection) {
        for (Object item : collection) {
            if (!(item instanceof String)) {
                return false;
            }
        }
        return true;
    }
}
```

그럼 이와 같이 코드를 작성해서 필요한 조건을 먼저 검사하게끔 할 수 있다.
하지만 이 방식이 통하는 것은 상위 클래스에 또 다른 원소 추가 메소드가 만들어지기 전까지다.

다음 릴리즈에서 우려한 일이 생기면, 하위 클래스에서 재정의하지 못한 그 새로운 메소드를 사용해 '허용되지 않은' 원소를 추가할 수 있게 된다.
실제로도 컬렉션 프레임워크 이전부터 존재하던 `HashTable`과 `Vector`를 컬렉션 프레임워크에 포함시키자 이와 관련된 보안 구멍들을 수정해야하는 사태가 벌어졌다.

## 원인 분석과 해결

위에서 발견한 두 문제는 모두 메소드를 재정의했기 때문에 발생했다.
클래스를 확장하더라도, 메소드를 재정의하는 대신, 새로운 메소드를 추가하면 괜찮으리라 생각할 수도 있다.

이 방식이 안전하긴 하지만, 다음 릴리즈에서 상위 클래스에 새 메소드가 추가됐는데,
운이 없게도 우리가 재정의한 메소드와 시그니처가 같고 반환 타입이 다르다면 우리가 만든 클래스는 컴파일 조차 되지 않는다.

혹여나 반환 타입마저 같다면, 상위 클래스의 새 메소드를 재정의한 꼴이니, 앞서의 문제와 똑같은 상호아에 부닥친다.
문제는 여기서 그치지 않는다. 우리가 만든 메소드는 상위 클래스의 메소드가 요구하는 규약을 만족하지 못할 가능성이 크다.

### 컴포지션

이러한 문제를 모두 피해가는 묘안은 바로 컴포지션(composition; 구성)을 사용하는 것이다.

기존 클래스를 확장하는 대신, 새로운 클래스를 만들고 private 필드로 기존 클래스의 인터페이스를 참조하게 한다.
기존 클래스가 새로운 클래스의 구성 요소로 쓰인다는 뜻에서 이러한 설계를 컴포지션이라고 한다.

새 클래스의 private 필드로 참조하는 인스턴스 메소드들은 기존 클래스의 대응하는 메소드를 호출해 그 결과를 반환한다.
이러한 방식을 전달(forwarding)이라고 하며, 새 클래스의 메소드들은 전달 메소드(forwarding method)라고 부른다.

그 결과 새로운 클래스는 기존 클래스의 내부 구현 방식의 영향에서 벗어나며, 심지어 기존 클래스에 새로운 메소드가 추가되더라도 전혀 영향받지 않는다.

```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet() {}

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }
    ...
}
```

즉, 위 코드와 같이 기존에 구현했던 `InstrumentedHashSet`은 `HashSet`을 바로 상속 받아 사용했지만,
컴포지션의 경우에는 아래와 같은 구조로 되어있는 것이다.

```java
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }

    public void clear() {
        s.clear();
    }

    ...
}
```

```java
public class InstrumentedSet<E> extends ForwardingSet<E> {

    private int addCount = 0;
    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

}
```

실제 `Set`을 상속 받는 것이 아닌, `Set` 인터페이스를 직접 구현한 `ForwardingSet` 클래스를 생성해 이를 상속 받는 형태인 것이다.

`InstrumentedSet`은 `HashSet`의 모든 기능을 정의한 `Set` 인터페이스를 활용해 설계되어 견고하고 아주 유연하다.
구체적으로는 `Set` 인터페이스를 구현했고, `Set`의 인스턴스를 인수로 받는 생성자 하나를 제공한다.

> 임의의 `Set`에 계측 기능을 덧씌워 새로운 `Set`으로 만드는 것이 이 클래스의 핵심이다.

상속 방식은 구체 클래스 각각을 따로 확장해야 하며, 지원하고 싶은 상위 클래스의 생성자 각각에 대응하는 생성자를 별도로 정의해줘야 한다.
하지만 지금 선보인 컴포지션 방식은 한 번만 구현해두면 어떠한 `Set` 구현체라도 계측할 수 있으며, 기존 생성자들과도 함께 사용할 수 있다.

```java
Set<Instant> times = new InstrumentedSet<>(new TreeSet<>(cmp));
Set<E> s = new InstrumentedSet<>(new HashSet<>(INIT_CAPACITY));
```

다른 `Set` 인스턴스를 감싸고(wrap) 있다는 뜻에서 `InstrumentedSet` 같은 클래스를 래퍼 클래스라 하며,
다른 `Set`에 계측 기능을 덧씌운다는 뜻에서 데코레이터 패턴(Decorator pattern)이라고 한다.

컴포지션과 전달의 조합은 넓은 의미로 위임(delegation)이라고 부른다.
단, 엄밀히 따지면 래퍼 객체가 내부 객체에 자기 자신의 참조를 넘기는 경우만 위임에 해당한다.

래퍼 클래스는 단점이 거의 없지만, 단 한 가지만 주의하면 된다. 래퍼 클래스가 콜백(callback) 프레임워크와는 어울리지 않는다는 점이다.
콜백 프레임워크에서는 자기 자신의 참조를 다른 객체에 넘겨서 다음 호출(콜백) 때 사용하도록 한다.

```java
// 콜백을 처리하는 인터페이스
interface CallbackHandler {
    void handleCallback();
}

// 콜백을 처리하는 클라이언트 클래스
class CallbackClient implements CallbackHandler {
    @Override
    public void handleCallback() {
        System.out.println("콜백이 호출되었습니다.");
    }

    public void doSomething() {
        // 어떤 작업을 수행한 후 콜백 호출
        // 이 부분에서 자신(this)을 넘기는 문제 발생 가능
        // CallbackClient가 CallbackHandler의 메서드를 직접 구현하므로 SELF 문제 발생
        handleCallback();
    }
}

```

내부 객체는 자신을 감싸고 있는 래퍼의 존재를 모르니 대신 자신(this)의 참조를 넘기고, 콜백 때는 래퍼가 아닌 내부 객체를 호출하게 된다. 이를 SELF 문제라고 한다.
전달 메소드가 성능에 주는 영향이나 래퍼 객체가 메모리 사용량에 주는 영향을 걱정하는 사람도 있지만, 실전에서는 둘 다 별다른 영향이 없다고 밝혀졌다.

```java
// 콜백을 처리하는 인터페이스
interface CallbackHandler {
    void handleCallback();
}

// 콜백을 처리하는 래퍼 클래스
class CallbackHandlerWrapper implements CallbackHandler {
    private final CallbackHandler delegate;

    public CallbackHandlerWrapper(CallbackHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void handleCallback() {
        // 래퍼 클래스가 콜백을 위임
        delegate.handleCallback();
    }
}

// 콜백을 처리하는 클라이언트 클래스
class CallbackClient {
    private final CallbackHandler callbackHandler;

    public CallbackClient(CallbackHandler callbackHandler) {
        this.callbackHandler = new CallbackHandlerWrapper(callbackHandler);
    }

    public void doSomething() {
        // 어떤 작업을 수행한 후 콜백 호출
        callbackHandler.handleCallback();
    }
}
```

전달 메소드를 작성하는 게 지루하겠지만, 재사용할 수 있는 전달 클래스를 인터페이스당 하나씩만 만들어두면 원하는 기능을 덧씌우는 전달 클래스들을 아주 손쉽게 구현할 수 있다.
좋은 예로, 구아바는 모든 컬렉션 인터페이스용 전달 메소드를 전부 구현해뒀다.

상속은 반드시 하위 클래스가 상위 클래스의 '진짜' 하위 타입인 상황에서만 쓰여야 한다. 다르게 말하면, 클래스 B가 클래스 A와 is-a 관계일 때만 클래스 A를 상속해야 한다.
클래스 A를 상속하는 클래스 B를 작성하려 한다면 "B가 정말 A인가?"라고 자문해보자.
대답이 "그렇다"라고 확신할 수 없다면, B는 A를 상속해서는 안 된다.
대답이 "아니다"라면 A를 `private` 인스턴스로 두고, A와는 다른 API를 제공해야 하는 상황이 대다수다.
즉, A는 B의 필수 구성요소가 아니라 구현하는 방법 중 하나일 뿐이다.

## 정리

- 상속은 강력하지만 캡슐화를 해친다는 문제가 있다.
- 상속은 상위 클래스와 하위 클래스가 순수한 is-a 관계일 때만 써야 한다.
- is-a 관계일 때도 안심할 수만은 없다.
  - 하쉬 클래스의 패키지가 상위 클래스와 다를 수 있고,
  - 상위 클래스가 확장을 고려해 설계되지 않았다면 여전히 문제가 될 수 있다.
- 상속의 취약점을 피하려면 상속 대신 컴포지션과 전달을 사용하자.
- 래퍼 클래스로 구현할 적당한 인터페이스가 있다면, 더욱 그렇다.
- 래퍼 클래스는 하위 클래스보다 견고하고 강력하다.
