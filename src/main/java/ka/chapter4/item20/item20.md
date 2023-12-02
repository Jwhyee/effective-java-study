# 추상 클래스보다는 인터페이스를 우선하라.

자바에서는 아래와 같은 다중 구현 메커니즘을 제공한다.

- 인터페이스
- 추상 클래스

이 둘의 가장 큰 차이점은 다음과 같다.

- 추상 클래스가 정의한 타입을 구현한 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 한다
- `Java`에서는 단일 상속만 지원을 하므로 아래와 같은 장,단점이 생긴다.
  - 추상 클래스 방식은 새로운 타입을 정의하는 데 커다란 제약을 받는다.
  - 인터페이스는 선언한 메소드를 모두 정의하고, 일반 규약을 잘 지켰다면, 다른 어떤 클래스를 상속했든 같은 타입으로 취급한다.

## 특징

### 계층구조

#### 인터페이스

기존 클래스에도 손쉽게 새로운 인터페이스를 구현해 넣을 수 있다.

```java
public class Room implements AutoCloseable {
    ...
    @Override
    public void close() {
        cleanable.clean();
    }
}
```

위 코드처럼 인터페이스가 요구하는 메소드가 아직 없다면 추가하고, 클래스 선언에 `implements` 구문만 추가하면 끝이다.

자바 플랫폼에서도 `Comparable`, `Iterable`, `AutoCloseable` 인터페이스가 새로 추가됐을 때,
표준 라이브러리의 수많은 기존 클래스가 이 인터페이스들을 구현한 채 릴리즈 됐다.

#### 추상 클래스

인터페이스의 경우 어떤 클래스에 넣어도 기능만을 구현하기 때문에 크게 신경쓰지 않아도 된다.

```java
public class Reader implements AutoCloseable {
    ...
    @Override
    public void close() {
        cleanable.clean();
    }
}

public class Room implements AutoCloseable {
    ...
    @Override
    public void close() {
        cleanable.clean();
    }
}
```

추상 클래스도 클래스기 때문에 `extends`를 통해 추상 클래스를 상속 받아 구현하게 된다.
위 코드에 있는 `AutoCloseable`을 추상 클래스라 가정하고 적용해보면 굉장히 어색해진다.

```java
public class Reader extends AutoCloseable {
    ...
    @Override
    public void close() {
        cleanable.clean();
    }
}

public class Room extends AutoCloseable {
    ...
    @Override
    public void close() {
        cleanable.clean();
    }
}
```

즉, A와 B 클래스에 C라는 추상 클래스를 상속해 확장하길 원한다면, C라는 추상 클래스는 계층구조상 두 클래스의 공통 조상이어야 한다.

### Mixin

믹스인(Mixin)이란, 클래스가 구현할 수 있는 타입으로, 믹스인을 구현한 클래스에 원래 **주된 타입** 외에도 특정 선택적 행위를 제공한다고 선언하는 효과를 준다.
예컨대 `Comparable`은 자신을 구현한 클래스의 인스턴스들끼리는 순서를 정할 수 있다고 선언하는 믹스인 인터페이스이다.
이처럼 대상 타입의 주된 기능에 선택적 기능을 혼합(mixed in)한다고 하여, 믹스인이라 부른다.

#### 추상 클래스

추상 클래스는 기존 클래스에 덧씌울 수 없다는 이유로 믹스인을 정의할 수 없다.
클래스는 두 부모를 섬길 수 없고, 클래스 계층구조에는 믹스인을 삽입하기에 합리적인 위치가 없기 때문이다.

#### 인터페이스

인터페이스는 계층구조가 없는 타입 프레임워크를 만들 수 있다.

```java
public interface Singer {
    AudioClip sing(Song s);
}

public interface SongWriter { 
    Song compose(int chartPosition);
}
```

우리 주변에는 작곡도 하는 가수가 제법 있다.
위 코드처럼 타입을 인터페이스로 정의하면, 가수 클래스가 `Singer`, `SongWirter` 모두를 구현해도 전혀 문제되지 않는다.

심지어 `Singer`와 `SongWriter` 모두를 확장하고, 새로운 메소드까지 추가한 제 3의 인터페이스를 정의할수도 있다.

```java
public interface SingerSongWriter extends Singer, SongWriter {
    AudioClip strum();

    void actSensitive();
}
```

이러한 설계를 할 경우 가능한 조합 전부를 각각의 클래스로 정의한 고도비만 계층구조가 만들어질 것이다.
속성이 n개라면, 지원해야할 조합의 수는 **2의 n승**개나 된다. 흔히 조합 폭발(combinatorial explosion)이라 부르는 현상이다.

거대한 클래스 계층 구조에는 공통 기능을 정의해놓은 타입이 없으니, 자칫 매개변수 타입만 다른 메소드들을 수없이 많이 가진 거대한 클래스를 낳을 수 있다.

### 골격 구현

관례상 인터페이스 이름이 `Interface`라면, 네이밍 컨벤션은 `AbstractInterface`와 같이 앞에 `Abstract`를 붙이는 것이 좋다.

> 예) `AbstractCollection`, `AbstractSet`, `AbstractList`, `AbstractMap`

위 예시가 바로 핵심 컬렉션 인터페이스의 골격 구현이다.
제대로 설계했다면 골격 구현은 그 인터페이스로 나름의 구현을 만드려는 프로그래머의 일을 상당히 덜어준다.

```java
static List<Integer> intArrayAsList(int[] arr) {
    Objects.requireNonNull(a);
    
    return new AbstractList<>() {
        @Override public Integer get(int idx) {
            return a[idx]; // 오토박싱
        }
        
        @Override public Integer set(int idx, Integer val) {
            int oldVal = a[idx];
            a[idx] = val;   // 오토언박싱
            return oldVal;  // 오토박싱
        }
        
        @Override public int size() {
            return arr.length;
        }
    };
}
```

`ArrayList`와 같은 `List` 구현체가 제공하는 기능들을 생각해보면, 이 코드는 골격 구현의 힘을 잘 보여주는 인상적인 예라고 할 수 있다.
이 예는 `int` 배열을 받아 `Integer` 인스턴스의 리스트 형태로 보여주는 어댑터(Adapter)이기도 하다.

이러한 골격 구현 클래스는 `defalut` 메소드의 이점을 여전히 누릴 수 있으며,
`private` 내부 클래스를 정의하고, 각 메소드 호출을 내부 클래스의 인스턴스에 전달하는 방식을 사용해
시뮬레이트한 다중 상속(simulated multiple inheritance)을 할 수 있다.

## 정리

- 일반적으로 다중 구현용 타입으로는 인터페이스가 적합하다.
- 복잡한 인터페이스라면 구현하는 수고를 덜어주는 골격 구현을 함께 제공하는 방법을 꼭 고려하라.
- 골격 구현은 '가능한 한' 인터페이스의 디폴트 메소드로 제공하자.
  - 그 인터페이스를 구현한 모든 곳에서 활용할 수 있어진다.
  - '가능한 한'이라는 말은 인터페이스에 걸려있는 구현상의 제약 때문에 골격 구현을 추상 클래스로 제공하는 경우가 더 흔하기 때문이다.