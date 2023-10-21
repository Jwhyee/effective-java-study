# 인터페이스는 구현하는 쪽을 생각해 설계하라.

`Java8`에서 부터 기존 인터페이스에 `default` 메소드를 추가할 수 있게 되었다.
이 메소드를 선언하면, 그 인터페이스를 구현한 후 디폴트 메소드를 재정의하지 않은 모든 클래스에서 디폴트 구현이 쓰이게 된다.

## 디폴트 메소드

```java
public interface Calculator {
    int sum(int num1, int num2);
    int minus(int num1, int num2);

    default int multiple(int num1, int num2) {
        return num1 * num2;
    }
}
```

```java
public class EngineeringCalc implements Calculator {
    @Override
    public int sum(int num1, int num2) {
        return num1 + num2;
    }

    @Override
    public int minus(int num1, int num2) {
        return num1 - num2;
    }
}
```

위와 같이 `Calculator`를 `implements`하여 `sum`과 `muinus`만 구현해주고, `multiple`은 인터페이스의 기능대로 따라가도록 두었다.

```java
public class CalcTest {

    @Test
    void calcTest() {
        EngineeringCalc ec = new EngineeringCalc();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(ec.minus(10, 20)).isEqualTo(-10);
            softAssertions.assertThat(ec.sum(10, 20)).isEqualTo(30);
            softAssertions.assertThat(ec.multiple(10, 20)).isEqualTo(200);
        });
    }
}
```

그 결과 직접 구현한 `minus`, `sum`도 정상적으로 동작하며, `multiple` 또한 정상적으로 작동하는 것을 알 수 있다.

이렇게 기존 인터페이스에 새로운 메소드를 안정적으로 추가할 수 있지만, 모든 구현체들과 매끄럽게 동작한다는 보장은 없다.
**현재의 인터페이스에 새로운 메소드가 추가될 일은 영원히 없다.** 고 가정하고 작성됐으니 말이다.

> 생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메소드를 작성하기란 어려운 법이다.

`Java8`에서 추가된 `Collection.removeIf()` 메소드를 봐보자.

```java
public interface Collection {
    default boolena removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean result = false;
        for(Iterator<E> it = iterator(); it.hasNext();) {
            if(filter.test(it.next())) {
                it.remove();
                result = true;
            }
        }
        return result;
    }
}
```

`Iterator`를 돌면서 주어진 `Predicate`가 `true`를 반환하면 반복자의 `remove` 메소드를 호출해 그 원소를 제거한다.

아파치의 `SynchronizedColletion` 클래스는 모든 메소드에서 주어진 락 객체로 동기화한 후 내부 컬렉션 객체에 기능을 위임하는 래퍼 클래스이다.
지금도 활발히 관리되고 있지만, `removeIf` 메소드를 재정의하고 있지 않다.
즉, 모든 메소드 호출을 알아서 동기화해주지 못한다.
`removeIf`의 구현은 동기화에 관해 아무것도 모르므로 락 객체를 사용할 수 없다.

`SynchronizedColletion` 인스턴스를 여러 스레드가 공유하는 환경에서 한 스레드가 `removeIf`를 호출하면, 
`ConcurrentModificationException`이 발생하거나 다른 예기치 못한 결과로 이어질 수 있다.

## 주의사항

> **기존 인터페이스에 디폴트 메소드로 새 메소드를 추가하는 일은 꼭 필요한 경우가 아니면 피해야한다.**

추가하려는 디폴트 메소드가 기존 구현체들과 충돌하지는 않을지 심사숙고해야 한다.
반면, 새로운 인터페이스를 마드는 경우라면 표준적인 메소드 구현을 제공하는데 아주 유용한 수단이며,
그 인터페이스를 더 쉽게 구현해 활용할 수 있게끔 해준다.

> **인터페이스로부터 메소드를 제거하거나 기존 메소드의 시그니처를 수정하는 용도가 아니다.**

이런 형태로 인터페이스를 변경하면 반드시 기존 클라이언트를 망가뜨리게 된다.

> **인터페이스를 설계할 때는 여전히 세심한 주의를 기울여야 한다.**

디폴트 메소드로 기존 인터페이스에 새로운 메소드를 추가하면 커다란 위험도 딸려온다.

> **새로운 인터페이스라면 릴리즈 전에 반드시 테스트를 거쳐야 한다.**

새 인터페이스가 의도한 용도에 잘 부합하는지 확인하는 길은 험난하지만, 바로잡을 기회가 아직 남았을 때 결함을 찾아내는 것이 좋다.
**인터페이스를 릴리즈한 후라도 결함을 수정하는 게 가능한 경우도 있겠지만, 절대 그 가능성에 기대서는 안 된다.**