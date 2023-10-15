# 상속을 고려해 설계하고, 문서화하라. 그러지 않았다면 상속을 금지하라.

`item18`에서 언급한 것과 같이 문서화해놓지 않은 '외부' 클래스를 상속할 때의 위험을 경고했다.
여기서 '외부'란, 프로그래머의 토엦권 밖에 있어서 언제 어떻게 변경되맂 모른다는 뜻이다.

## 상속을 고려한 설계와 문서화

메소드를 재정의하면 어떤 일이 일어나는지를 정확히 정리하여 문서로 남겨야 한다.

> 상속용 클래스는 재정의할 수 있는 메소드들을 내부적으로 어떻게 이용하는지(자기 사용) 문서로 남겨야 한다.

`item18`에서 작성한 클래스를 다시 살펴보자.

```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

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
}
```

위 코드로 구현된 코드는 `addAll()`을 사용하면 카운트가 2배씩 증가하는데, 다른 개발자들은 한 개씩 올라가는 줄 알고 사용할 것이다.
이렇게 문서화 없이 구현을 해놓을 경우 나 자신을 포함한 다른 개발자 모두 헷갈리게 된다.

또한, 호출되는 메소드가 재정의 가능 메소드일 경우 그 사실을 호출하는 메소드의 API 설명에 적시해야 한다.

위 코드를 예시로, 구현체에서 `super.addAll()`이 호출될 경우, 내가 다시 구현한 `add()` 함수가 실행되는 것도 명시해줘야 한다.
예를 들어, '`addAll()`이 호출될 경우 재정의 된 `add()` 함수가 호출되며, `addCount`는 본 메소드에서 한 번, `add` 메소드에서 한 번으로 총 두 번 증가한다.'라는 문구와 같이 어떤 순서로 호출하는지, 각각의 호출 결과가 이어지는 처리에 어떤 영향을 주는지도 담아야 한다.

즉, 재정의 가능 메소드를 호출할 수 있는 모든 상황을 문서로 남겨야 하는 것이다.

> 재정의 가능이란, public과 protected 메소드 중 final이 아닌 모든 메소드를 의미한다.

### @implSpec을 이용해 내부 동작 방식 설명

API 문서의 메소드 설명 끝에서 종종 'Implementation Requirements'로 시작하는 절을 볼 수 있는데,
그 메소드의 내부 동작 방식을 설명하는 곳이다.
이 절은 메소드 주석에 `@implSpec` 태그를 붙여주면 자바독 도구가 생성해준다.

```java
/**
 * @author  Josh Bloch
 * ...
*/
public abstract class AbstractCollection<E> implements Collection<E> {
    /**
     * @implSpec
     * ...
     * Implementation Requirements:
     * This implementation iterates over the collection looking for the specified element. 
     * If it finds the element, it removes the element from the collection using the iterator's remove method.
     * Note that this implementation throws an UnsupportedOperationException if the iterator returned by
     * this collection's iterator method does not implement the remove method and this collection contains the specified object.
     */
    public boolean remove(Object o) {
        ...
    }
}
```

이 메소드에서는 `iterator` 메소드를 재정의하면 `remove` 메소드의 동작에 영향을 줌을 확실히 알 수 있다.
앞서 우리가 구현했던 `HashSet`을 재정의한 `InstrumentedHashSet`과 대조대는 상황이다.

이러한 `@implSpec`은 실수를 유발하지 않게 만드는 중요한 사항이지만, 아직까지 선택사항으로 남겨져있다.
이 태그를 활성화하려면, 명령줄 매개변수로 아래 내용을 추가해주면 된다.
아직까지 커스텀 태그로 등록되어 있어 개발자 입맛대로 `@구현`과 같이 수정해서 적용해도 된다.
하지만, 추후에 표준 태그로 정의될 수 있으니 가능한 자바 개발팀과 같은 방식으로 사용하는 편이 좋다.

```bash
-tag "implSpec:a:Implementation Requirements:"
-tag "구현:a:구현 요구사항:"
```

## 상속을 허용하는 클래스가 지켜야할 제약

### protected 메소드 형태로 공개할 수 있어야 한다.

널리 쓰일 클래스를 상속용으로 설계한다면, 여러분이 문서화한 내부 사용 패턴과, `protected` 메소드와 필드를 구현하면서 선택한 결정에 영원히 책임져야 함을 잘 인식해야 한다.
이 결정들이 그 클래스의 성능과 기능에 영원한 족쇄가 될 수 있다.
그러니 **상속용으로 설계한 클래스는 배포 전에 반드시 하위 클래스를 만들어 검증해야 한다.**

효율적인 하위 클래스를 큰 어려움 없이 만들 수 있게 하려면,
클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅(hook)을 잘 선별하여 `protected` 메소드 형태로 공개해야할 수도 있어야 한다.

```java
/**
 * @implSpec
 * This implementation gets a list iterator positioned before fromIndex, 
 * and repeatedly calls ListIterator.next followed by ListIterator.
 * remove until the entire range has been removed. 
 * Note: if ListIterator.remove requires linear time, this implementation requires quadratic time.
 * */
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    protected void removeRange(int fromIndex, int toIndex) {
        ListIterator<E> it = listIterator(fromIndex);
        for (int i=0, n=toIndex-fromIndex; i<n; i++) {
            it.next();
            it.remove();
        }
    }
}
```

이 메소드는 우리가 실제로 사용할 수는 없지만, 
그럼에도 이러한 메소드를 제공한 이유는 단지 하위 클래스에서 부분 리스트의 `clear` 메소드를 고성능으로 만들기 쉽게 하기 위해서이다.
만약 이 메소드가 없다면, 하위 클래스에서 `clear` 메소드를 호출하면, 제거할 원소 수의 제곱에 비례해 성능이 느려지거나,
부분리스트의 메커니즘을 밑바닥부터 새로 구현해야 했을 것이다.

> 상속이 캡슐화를 망치는 이유 : 하위 클래스가 상위 클래스의 내부 구현 세부 사항에 접근하거나 의존할 수 있기 때문

이를 테스트하는 방법은 알아서 잘 예측하고, 실제 하위 클래스를 만드러 시험해보는 방법 뿐이다.
`protected` 메소드 하나하나가 내부 구현에 해당하므로 그 수는 가능한 한 적어야 한다.

한편으로는 너무 적게 노출해서 상속으로 얻는 이점마저 없애지 않도록 주의해야 한다.
꼭 필요한 `protected` 멤버를 놓쳤다면, 하위 클래스를 작성할 때, 그 빈자리가 확연히 드러난다.
거꾸로, 하위 클래스를 여러 개 만들 때까지 전혀 쓰이지 않는 `protected` 멤버는 사실 `private`이었어야 할 가능성이 크다.

### 상속용 클래스의 생성자는 재정의 가능 메소드를 호출해서는 안 된다.

이 규칙을 어길 경우 프로그램이 오동작할 것이다.
상위 클래스의 생성자가 하위 클래스의 생성자보다 먼저 실행되므로, 하위 클래스에서 재정의한 메소드가 하위 클래스의 생성자보다 먼저 호출된다.

```java
public class Parent {
    final String name;

    public Parent(String name) {
        this.name = name;
    }
}
```

```java
public class Child extends Parent {

    final String name;

    public Child(String pName, String cName) {
        this.name = cName;
        super(pName);
    }
}
```

위 코드와 같이 `super(pName)`을 두 번째 줄로 내릴 경우, 컴파일 에러가 발생한다.
때문에, 항상 상위 클래스의 생성자가 먼저 호출되고, 그 다음 하위 클래스의 생성자가 실행되는 것이다.

재정의한 메소드가 하위 클래스의 생성자에서 초기화하는 값에 의존한다면 의도대로 동작하지 않을 것이다.

```java
public class Super {
    public Super() {
        overrideMe();
    }

    public void overrideMe() {

    }
}
```

```java
public final class Sub extends Super {
    private final Instant instant;

    public Sub() {
        instant = Instant.now();
    }

    @Override
    public void overrideMe() {
        System.out.println(instant);
    }
}
```

위와 같이 재정의한 메소드 `overrideMe()`가 하위 클래스의 생성자에서 초기화하는 값에 의존하도록 구성하였다.

```java
public class SuperTest {
    @Test
    void superConstructorCallTest() {
        Sub sub = new Sub();
        sub.overrideMe();
    }
}
```

```
# 출력 결과
null
2023-10-14T03:18:16.282365Z
```

개발자가 의도한 바는, 하위 클래스에서 `Instant`가 주입되어, 상위 클래스에서도 동일한 값을 출력하는 것이었지만,
실제로는 상위 클래스의 생성자가 먼저 호출되어 `null`이 나오고, 하위 클래스의 생성자에 영향을 받아 주입된 값이 출력된다.

또한, `overrideMe()`에서 `instant.getNano()`와 같이 객체의 메소드를 호출하려 한다면,
상위 클래스의 생성자가 먼저 호출되므로, 초기화 되지 못해 `NPE`를 던지게 될 것이다.
여기서 NPE가 터지지 않은 이유는 `println`이 null 입력도 받아들이기 때문이다.

> `private`, `final`, `static` 메소드는 재정의가 불가능하니 생성자에서 안심하고 호출해도 된다.

#### Cloneable, Serializable

위와 이어지는 내용으로 `clone`과 `readObject` 모두 직접적으로든 간접적으로든 재정의 가능 메소드를 호출하면 안 된다.

위 두 인터페이스는 상속용 설계의 어려움을 한층 더해준다. 둘 중 하나라도 구현한 클래스를 상속할 수 있게 설계하는 것은 일반적으로 좋지 않은 생각이다.
그 클래스를 확장하려는 프로그래머에게 엄청난 부담을 짊어주기 때문이다. item13과 item86을 참고하자.

`clone`과 `readObject`는 생성자와 비슷하게 새로운 객체를 만드는 효과를 낸다.

따라서 상속용 클래스에서 `Cloneable`, `Serializable`을 구현할지 정해야 한다면,
이들을 구현할 때 따르는 제약도 생성자와 비슷하다는 점에 주의하자.
즉, **`clone`과 `readObject` 모두 직접적으로든 간접적으로든 재정의 가능 메소드를 호출하면 안 된다.** 

`readObject`의 경우 하위 클래스의 상태가 미처 다 역직렬화 되기 전에 재정의한 메소드부터 호출하게 된다.

`clone`의 경우 하위 클래스의 `clone` 메소드가 복제본의 상태를 올바른 상태로 수정하기 전에 재정의한 메소드를 호출한다.

어떤 메소드를 사용하던 프로그램이 오작동할 것이고, 특히 `clone`이 잘못될 경우, 복제본뿐 아니라, 원본 객체에도 피해를 줄 수 있다.

item13에서 구현 예시로 든 `Stack` 클래스와 같이 배열 내부 자료구조까지 복제본으로 완벽히 복사된 상태로 복제본을 수정했다고 가정하자.
그런데 사실은 `clone`이 완벽하지 못해 복제본이 원본 객체의 `Object` 배열의 참조를 그대로하고 있게 된다면, 원본 객체도 함께 피해를 입게 되는 것이다. 

`Serializable`을 구현한 상속용 클래스가 `readResolve`, `writeReplace` 메소드를 갖는다면, 이 메소드들은 `private`가 아닌 `protected`로 선언해야 한다.
`private`으로 선언한다면, 하위 클래스에서 무시되기 때문이다. 이 역시 상속을 허용하기 위해 내부 구현을 클래스 API로 공개하는 예 중 하나이다.

## 정리

클래스를 상속용으로 설계하려면 엄청난 노력이 들고 그 클래스에 안기는 제약도 상당하다.

위와 같은 문제들을 해결하는 가장 좋은 방법은 **상속용으로 설계하지 않은 클래스는 상속을 금지하는 것이다.**
가장 쉬운 방법은 클래스를 `final`로 선언하는 것이다.
상황이 여의치 않다면, 모든 생성자를 `private` 혹은 `package-private`로 선언하고, `public` 정적 팩터리를 만들어주는 방법이다.

- 상속용 클래스를 설계하기란 만만치 않다.
- 클래스 내부에서 스스로 어떻게 사용하는지(자기사용 패턴)를 모두 문서로 남겨야 한다.
  - 문서화한 것은 그 클래스가 쓰이는 한 반드시 지켜야 한다.
  - 그렇지 않을 경우, 내부 구현 방식을 믿고 활용하던 하위 클래스를 오동작하게 만든다.
- 효율 좋은 하위 클래스를 만들 수 있도록 `protected`로 제공할 수 있어야 한다.
- 클래스를 확장해야할 명확한 이유가 떠오르지 않으면, 상속을 금지하는 편이 낫다.
- 상속을 금지하려면 클래스를 `final`로 선언하거나 생성자 모두를 외부에서 접근할 수 없도록 만들자.
