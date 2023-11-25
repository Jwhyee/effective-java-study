# 변경 가능성을 최소화하라.

## 불변 클래스

> 인스턴스 내부 값을 수정할 수 없는 클래스

불변 인스턴스에 간직된 정보는 고정되어 객체가 파괴되는 순간까지 절대 달라지지 않는다.

자바 플랫폼 라이브러리에도 `String`, 기본 타입의 박싱된 클래스들, `BigInteger`, `BigDecimal`이 여기에 속한다.
불변 클래스는 가변 클래스보다 설계 및 구현, 사용이 쉬우며, 오류가 생길 여지도 적고 훨씬 안전하다.

### 불변 클래스 생성 규칙

#### 객체의 상태를 변경하는 메소드(변경자)를 제공하지 않는다.

불변이라는 단어와 같이 변하지 않아야하기 때문에 객체 상태를 변경할 수 없도록 변경자를 제공하지 않아야 한다.

#### 클래스를 확장할 수 없도록 한다.

하위 클래스로 접근하면 상위 클래스의 상태를 변하게 만들 수 있다.
그렇기 때문에 클래스를 `final`로 선언해 상속을 막도록 만들어야 한다.

#### 모든 필드를 final로 선언한다.

각 필드에 `final`(시스템이 강제하는 수단) 키워드를 사용하면 설계자의 의도를 명확히 드러낼 수 있다.
새로 생성된 인스턴스를 동기화 없이 다른 스레드로 건네도 문제없이 동작하게끔 보장하는 데도 필요하다.

> final fields also allow programmers to implement thread-safe immutable objects without synchronization<br>
> final 필드는 synchronization 없이도 스레드 안전 불변 객체를 구현할 수 있도록 해준다.<br>
> - [JLS, 17.5](https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html#jls-17.5)

#### 모든 필드를 private로 선언한다.

`private`로 선언하지 않을 경우 가변 객체를 클라이언트에서 접근해 수정될 수 있다.
접근은 가능하되 수정하지는 못하도록 `public final`로 선언해 릴리즈하면, 다음 릴리즈에서 `private`로 바꾸지 못하게 된다.
그렇기 때문에 가능한 `private`로 시작하는 것이 좋다.

> Java 17에서 `public`으로 공개된 api가 Java 21에 가서 갑자기 `private`로 바뀐다면 큰 피해가 생길 수 있다.

#### 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.

클래스에 가변 객체를 참조하는 필드가 하나라도 있다면, 클라이언트에서 그 객체의 참조를 얻을 수 없도록 해야한다.
이런 필드는 절대 클라이언트가 제공한 객체 참조를 가리키게 해서는 안 되며, 접근자 메소드가 그 필드를 그대로 반환해서도 안 된다.
생성자, 접근자, readObject 메소드 모두에서 방어적 복사를 수행하라.

### 불변 클래스 예시

[코드](complex/Complex.java)에 있는 사칙연산 메소드(plus, minus, times, dividedBy)를 보면 연산한 결과를 `Complex`에 대한 새로운 객체로 만들어 반환하는 것을 볼 수 있다.

> 이처럼 피연산자에 함수를 적용해 그 결과를 반환하지만, 피연산자 자체는 그대로인 프로그래밍 패턴을 함수형 프로그래밍이라 한다.
> 이와 달리, 절차적 혹은 명령형 프로그래밍에서는 메서드에서 피연산자인 자신을 수정해 자신의 상태가 변하게 된다.
> > 주어진 코드에서 함수형에 대한 코드는 보이지 않는 것 같다.

또한, 메서드 이름으로 `add` 같은 동사 대신 `plus`라는 전치사를 사용한 점에도 주목하자.
이는 해당 메소드가 객체의 값을 변경하지 않는다는 사실을 강조하려는 의도이다.

## 함수형 프로그래밍

Item5에서 `Supplier`라는 인터페이스가 나왔었다. 이를 예시로 한 번 살펴보자!

```java
public class SupplierTest {
    @Test
    void supplierTest() {
        Supplier<Integer> randomSupplier = () -> {
            int num = 100;
            return new Random().nextInt(num + 1);
        };

        for (int i = 0; i < 3; i++) {
            int random = randomSupplier.get();
            System.out.println("random = " + random);
        }
    }
}
```

위와 같은 코드를 통해 랜덤한 값을 뽑아내는 코드를 작성했었다. 당시에는 함수 자체를 변수에 담는다는 개념으로 이해했지만 조금 다르다.

우리가 가장 많이 사용하는 함수형 프로그램이라면 `Stream` 인터페이스의 `map`을 예시로 들 수 있다.

```java
public class StreamFunctionalTest {

    @Test
    void integerToStringMap() {
        int[][] arr = {{1, 0, 0, 0},
                        {1, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 1, 1}
        };
        
        Arrays.stream(arr)
                .map(Arrays::toString)
                .forEach(System.out::println);
    }
    
}
```

```
[1, 0, 0, 0]
[1, 1, 0, 0]
[0, 1, 0, 0]
[0, 1, 1, 1]
```

위 코드를 자세히 보면 `Arrays::toString`이라는 코드를 볼 수 있다. 이 말은 `Stream` 인터페이스의 `map()`이라는 함수에 `Arrays` 클래스에 있는 `toString` 메소드를 전달한 것이다.

```java
public interface Stream ... {
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);
}
```

위와 같이 `map` 함수에는 `Function` 이라는 인터페이스를 매개변수로 받고있다.

```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
```

해당 클래스를 살펴보면 `@FunctionalInterface` 어노테이션이 붙어있는데, 이런 어노테이션이 붙은 인터페이스들은 메소드 혹은 조건식과 같은 형태로 전달할 수 있게 된다.

자바에서 함수형 프로그래밍을 가능하게 하는 종류는 대표적으로 다음과 같다.

|     인터페이스명     |  매개변수 여부   | 리턴타입 여부  |  설명   |
|:--------------:|:----------:|:--------:|:-----:|
|  Supplier\<T>  |     N      |    T     |  공급자  |
|  Consumer\<T>  |     T      |    N     |  소비자  |
| Predicate\<T>  | T(boolean) |    T     |  단정   |
| Function<T, R> |     T      |    R     |  함수   |

아래 사진은 `@FunctionalInterface`이 붙은 인터페이스 들이다. 대부분 위 네이밍과 동일하게 되어있다.

![스크린샷 2023-10-05 오후 12 51 21](https://github.com/Jwhyee/problem-solving/assets/82663161/e62c69b8-a0a0-486c-aafa-679d0213240d)

### Supplier<T>

> **공급자**의 개념으로 특정 값을 만들어 반환해줌

`Set`의 경우 저장 순서를 보장하지 않는다. `Set` 내부에 데이터가 어디에 저장되어 있는지 찾는 일이 많지 않겠지만, `Supplier`를 통해 특정 데이터가 저장된 순서를 찾는 코드를 만들어보자!

```java
public class FunctionalTest {
    @Test
    void supplierTest() {
        // 문자열 Set 생성
        Set<String> stringSet = new HashSet<>();
        
        // Set에 string 1 ~ string 100까지 저장
        for (int i = 1; i <= 100; i++) {
            stringSet.add("string " + i);
        }
        
        // 함수 팩토리에 구현한 findIdx를 통해 "string 20"이 저장된 위치를 찾음
        // findIdx(set, data)
        final int idx = CustomFunctionalFactory.findIdx(stringSet, () -> "string 20");

        // 실제 저장된 위치가 맞는지 확인
        int i = 0;
        for (String s : stringSet) {
            if (i == idx) {
                assertTrue("string 20".equals(s));
            }
            i++;
        }
    }
}
```

```java
public class CustomFunctionalFactory {
    /** Set에 포함된 특정 값의 저장 순서를 반환하는 함수
     * @param set       특정 데이터가 포함되어 있는지 탐색할 Set
     * @param supplier  전달받은 "string 20"이 들어있음
     * @return          특정 데이터가 저장된 순서 반환, 데이터가 없을 경우 -1 반환
     * @param <T>       매개변수로 제네릭 타입을 받기 위함
     */
    public static <T> int findIdx(Set<T> set, Supplier<T> supplier) {
        int i = 0;
        // 넘겨 받은 set에 저장된 데이터를 순회하면서 "string 20"의 저장 순서 탐색
        for (T t : set) {
            if (t.equals(supplier.get())) {
                // 순서 반환
                return i;
            }
            i++;
        }
        return -1;
    }
}
```

**Supplier(공급자)**는 이름과 같이 값을 만들어서 반환해주는 역할을 한다.

### Consumer<T>

> **소비자**의 개념으로 주어진 값을 소비함

```java
public class FunctionalTest {
    @Test
    void consumerFunctionalTest() {
        // 문자열 리스트 생성
        List<String> stringList = new ArrayList<>();
        
        // string 1 ~ 100까지 리스트에 저장
        for (int i = 1; i <= 100; i++) {
            stringList.add("string " + i);
        }
        
        // 함수 팩토리에 리스트와 주어진 리스트로 처리할 기능을 넘겨줌 -> 1 ~ 100까지 출력
        CustomFunctionalFactory.forEachRemaining(stringList, (item) -> System.out.println(item));
    }
}
```

```java
public class CustomFunctionalFactory {
    /** List에 남아있는 값에 대해서 무언가를 처리하는 메소드
     * @param list      forEach를 돌 리스트
     * @param consumer  forEach를 돌면서 수행할 일
     * @param <T>       매개변수로 제네릭 타입을 받기 위함
    */ 
    public static <T> void forEachRemaining(List<T> list, Consumer<T> consumer) {
        for (T t : list) {
            consumer.accept(t);
        }
    }
}
```

**Consumer(소비자)**는 이름과 같이 특정 데이터를 가지고 무언가를 처리하는 역할을 한다.

### Predicate<T>

> **단정짓다**라는 개념으로 주어진 값을 토대로 옳고, 그름을 판단함

```java
public class FunctionalTest {
    @Test
    void predicateFunctionalTest() {
        // 문자열 리스트 생성
        List<String> stringList = new ArrayList<>();

        // string 1 ~ 100까지 리스트에 저장
        for (int i = 1; i <= 100; i++) {
            stringList.add("string " + i);
        }

        // 함수 팩토리에 리스트와 해당 리스트에서 걸러줄 조건식을 넘겨줌 -> 1이 포함된 데이터만 돌려 받음
        final List<String> filteredList = CustomFunctionalFactory
                .filter(stringList, (item) -> item.contains("1"));

        // 1이 포함된 데이터만 출력
        for (String s : filteredList) {
            System.out.println(s);
        }

    }
}
```

```java
public class CustomFunctionalFactory {
    /** 주어진 리스트에서 조건식에 맞는 데이터만 새로운 리스트에 담아서 반환하는 메소드
     * @param list          기존 데이터 리스트
     * @param predicate     리스트에서 검사할 조건식
     * @return              조건식을 통해 걸러진 데이터 리스트
     * @param <T>           매개변수로 제네릭 타입을 받기 위함
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        // 조건식에 해당되는 데이터를 담을 새로운 리스트
        List<T> result = new ArrayList<>();
        
        // 기존 데이터 리스트 순회
        for (T item : list) {
            // 주어진 조건식에 맞을 경우 결과 리스트에 추가
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        
        // 결과 리스트 반환
        return result;
    }
}
```

**Predicate(단정짓다)**라는 이름과 같이 기존 데이터 중 조건식에 맞는 데이터만 추출하는 역할을 한다.
상황에 따라 어떤 조건식으로든 교체할 수 있어 굉장히 유연하다.

### Functional<T>

```java
public class FunctionalTest {
    @Test
    void functionFunctionalTest() {
        // 문자열 리스트 생성
        List<String> stringList = new ArrayList<>();

        // string 1 ~ 100까지 리스트에 저장
        for (int i = 1; i <= 100; i++) {
            stringList.add("string " + i);
        }

        // 함수 팩토리에 리스트와 mapping할 값을 넘겨줌 -> 모든 데이터 뒤에 "^_^"가 붙은 값을 반환함
        final List<String> filteredList = CustomFunctionalFactory
                .map(stringList, (item) -> item.concat("^_^"));

        // 기존 데이터에 "^_^"가 붙은 값들 출력
        for (String s : filteredList) {
            System.out.println(s);
        }
    }
}
```

```java
public class CustomFunctionalFactory {
    /** 주어진 리스트에서 특정 함수를 전달 받고, 해당 함수를 적용하는 함수
     * @param list          기존 데이터 리스트
     * @param function      리스트를 순회하면서 실행할 함수
     * @return              함수대로 진행한 결과 리스트 반환
     * @param <T>           기존 함수의 데이터 타입
     * @param <R>           반환할 리스트의 데이터 타입(주어진 함수의 반환타입 ex) item.concat() -> String 타입)
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        // 조건식에 해당되는 데이터를 담을 새로운 리스트
        List<T> result = new ArrayList<>();

        // 기존 데이터 리스트 순회
        for (T item : list) {
            // 전달 받은 함수에 기존 데이터 요소를 넣고, 결과 배열에 추가
            result.add(function.apply(item));
        }
        
        // 결과 리스트 반환
        return result;
    }
}
```

**Function(함수)**라는 이름과 같이 기존 데이터를 순회하면서 각 요소를 전달 받은 함수를 적용하는 역할을 한다.

보통은 `Repository`에서 바로 `Dto`에 매핑하지만, 가끔 아래 코드처럼 전체 데이터를 뽑아서 `Dto`로 변환해 `list`로 적용하는 방식을 사용한다.
`Function<T, R>`에 대입해서 보면, `<T>`는 `Post.class`를 의미하고, `<R>`은 `Post::of`의 반환 타입인 `PostDto.class`를 의미한다.

```java
public class Post {
    ...

    public PostDto of() {
        return new PostDto(...);
    }
}

public class PostService {
    public List<PostDto> findAllPostDto() {
        List<PostDto> postDtoList = postRepository.findAll().stream()
                .map(Post::of)
                .toList();
    }
}
```

## 불변 클래스의 특징

### 불변 객체는 단순하다.

불변 객체는 생성된 시점의 상태를 파괴될 때까지 그대로 간직한다.

모든 생성자가 클래스 불변식(class invariant)을 보장한다면, 그 클래스를 사용하는 프로그래머가 다른 노력을 들이지 않더라도 영원히 불변으로 남는다.

반면 가변 객체의 경우 임의의 복잡한 상태에 놓일 수 있다. 변경자 메서드가 일으키는 상태 전이를 정밀하게 문서로 남겨놓지 않은 가변 클래스는 믿고 사용하기 어려울 수 있다.

### 불변 객체는 근본적으로 스레드 안전하여 따로 동기화할 필요 없다.

여러 스레드에서 불변이 아닌 객체에 접근할 경우, 값이 변경되어 예기치 못한 상황이 발생할 수 있다. 하지만, 불변 객체는 여러 스레드가 동시에 접근 및 사용하더라도 절대 훼손되지 않는다.

불변 객체에 대해서는 그 어떤 스레드도 다른 스레드에 영향을 줄 수 없어, **불변 객체는 안심하고 공유할 수 있다.**
따라서 불변 클래스라면 한 번 만든 인스턴스를 최대한 재활용하는 것이 좋다. 가장 쉬운 재활용 방법은 자주 쓰이는 값들을 상수로 제공하는 것이다.

```java
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE = new Complex(1, 0);
public static final Complex I = new Complex(0, 1);
```

`Integer` 클래스도 `@Native`로 구현되어 있긴하지만 아래와 같이 불변 값을 `static final`로 제공하고 있다.

```java
@Native public static final int   MIN_VALUE = 0x80000000;
@Native public static final int   MAX_VALUE = 0x7fffffff;
```

불변 클래스는 자주 사용되는 인스턴스를 캐싱하여 같은 인스턴스를 중복 생성하지 않게 해주는 정적 팩터리를 제공할 수 있다.

박싱된 기본 타입 클래스 전부와 `BigInteger`가 여기에 속한다. 이런 정적 팩터리를 사용하면 여러 클라이언트가 인스턴스를 공유하여 메모리 사용량과 가비지 컬렉션 비용이 줄어든다.

> `Integer.MIN_VALUE`와 같은 값을 빈번하게 사용하는데, 정적 팩터리가 아니라면 `Integer`에 대한 인스턴스를 계속 생성해서 사용해야한다.
> 즉, 다중 스레드 환경에서 계속해서 인스턴스를 만들어서 접근하기에 메모리를 엄청나게 낭비하게 되는 것이다. 이런 상황에서 불변이고, 정적 팩터리를 사용하면 메모리를 훨씬 절약할 수 있게 된다.<br>
> 이와 같이 새로운 클래스를 설계할 때, `public` 생성자 대신 정적 팩터리를 만들어 두면, 클라이언트를 수정하지 않고도 필요에 따라 캐시 기능을 나중에 덧붙일 수 있다.

불변 객체를 자유롭게 공유할 수 있다는 점은 방어적 복사도 필요 없다는 결론으로 자연스럽게 이어진다.

> 아무리 복사해봐야 원본과 똑같으니 복사 자체가 의미가 없다. 그러니 불변 클래스는 `clone` 메소드나 복사 생성자를 제공하지 않는 것이 좋다.
> `String` 클래스의 복사 생성자는 이 사실을 잘 이해하지 못한 자바 초창기 때 만들어진 것으로, 되도록 사용하지 말아야 한다.

### 불변 객체는 자유롭게 공유할 수 있음은 물론, 불변 객체끼리는 내부 데이터를 공유할 수 있다.

`BigInteger` 클래스는 내부에서 값의 부호(sign)와 크기(magnitude)를 따로 표현한다.
부호에는 int 변수를, 크기(절댓값)에는 int 배열을 사용하는 것이다.
한면 negate 메소드는 크기가 같고 부호만 반대인 새로운 `BigInteger`를 생성하는데, 이때 배열은 비록 가변이지만 복사하지 않고 원본 인스턴스와 공유해도 된다.
그 결과 새로 만든 `BigInteger` 인스턴스도 원본 인스턴스가 가리키는 내부 배열을 그대로 가리킨다.

```java
@Test
void bigIntegerTest() {
    try {
        BigInteger bi = new BigInteger("-1000000");
        int signum = bi.signum();
        Field field = bi.getClass().getDeclaredField("mag");
        field.setAccessible(true);

        System.out.println(field);
        System.out.println(signum);

    } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
    }
}
```

### 객체를 만들 때 다른 불변 객체들을 구성요소로 사용하면 이점이 많다.

값이 바뀌지 않는 구성요소들로 이뤄진 객체라면 그 구조가 아무리 복잡하더라도 불변식을 유지하기 훨씬 수월하기 때문이다.
좋은 예로, 불변 객체는 맵의 키와 집합(Set)의 원소로 쓰기에 안성맞춤이다.
맵이나 집합은 안에 담긴 값이 바뀌면 불변식이 허물어지는데, 불변 객체를 사용하면 그런 걱정은 하지 않아도 된다.

### 불변 객체는 그 자체로 실패 원자성을 제공한다.

상태가 절대 변하지 않으니, 잠깐이라도 불일치 상태에 빠질 가능성이 없다.

## 불변 클래스의 단점

### 값이 다르면 반드시 독립된 객체로 만들어야 한다.

값의 가짓수가 많다면 이들을 모두 만드는 데 큰 비용을 치러야 한다.
백만 비트짜리 `BigInteger`에서 비트 하나를 바꿔야할 경우를 예로 들어보자.

```java
@Test
void bigIntegerTest() {
    BigInteger moby = new BigInteger("1000000");

    moby = moby.flipBit(0);

    // 1000001 출력
    System.out.println(moby);
}
```

`flipBit()`의 수가 커질수록 1, 2, 4, 8, 16과 같이 2^n으로 커지게 된다. 즉, 객체의 가장 오른쪽 비트(가장 낮은 자리수)를 뒤집으면서 비트 값을 반전시키는 것이다.
이렇게 `flipBit(0)` 메소드는 원본과 단지 한 비트만 다른 백만 비트짜리 새로운 인스턴스를 생성하게 된다. 이 연산은 BigInteger의 크기에 비례해 시간과 공간을 잡아먹는다.

클라이언트들이 원하는 복잡한 연산들을 정확히 예측할 수 있다면 `package-private`의 가변 동반 클래스만으로 충분하다.
그렇지 않다면 이 클래스를 `public`으로 제공하는 것이 최선이다.

String의 경우를 보면 다음과 같다.

```java
String str = "abc";
str = "bcd";
```

`String`의 경우 새로운 값을 지정할 경우 새로운 객체를 만들어서 다시 주입하게 된다.
즉, `String` 클래스가 불변이기 때문에 기존 값을 수정하는 것이 아닌 새로운 객체로 변환하고, 기존 값은 GC에게 맡기게 된다.

```java
String str = "abc";
str += "bcd";
```

위 코드도 동일하다. 기존 객체의 값을 가져와 새로 들어온 값을 추가한 새로운 객체를 만들어 주입하게 된다.
이 경우 계속해서 새로운 객체를 생성하고, 소멸하기 때문에 GC는 일을 계속하게 된다.

때문에 문자열에 다른 문자열 값을 추가하는 행위는 불필요한 리소스를 계속 생성하는 것이기 때문에 `StringBuilder`를 사용하는 것이 좋다.
실제로 `String` 클래스로 초기화된 값에 `+` 연산을 할 경우 바이트 코드로 변환하는 과정에서 최적화를 위해 `StringBuilder`로 바뀌게 된다.

`StringBuilder`와 같은 경우 불변인 `String`을 가변으로 사용할 수 있게 해준다.

![image](https://github.com/Back-Mo/java-spring-api-study/assets/82663161/bf724719-9d7d-443d-82db-4cbbc2380fd7)

## 불변 클래스 설계 방법

### 상속 방어

클래스가 불변임을 보장하려면 자신을 상속하지 못하게 해야한다는 규칙이 있었다.

상속을 방어하기 위한 가장 쉬운 방법은 클래스에 `final`을 붙이는 것이지만, 더 유연한 방법이 있다.
모든 생성자를 `private` 혹은 `package-private`으로 만들고, `public` 정적 팩터리를 제공하는 방법이다.

```java
public class Complex {
    private final double re;
    private final double im;
    
    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }
}
```

위와 같이 생성자를 `private` 혹은 `package-private`로 막으면 상속을 받더라도 부모 클래스에 접근할 수 없게 된다.
바깥에서 볼 수 없는 `package-private` 구현 클래스를 원하는 만큼 만들어 활용할 수 있으니 훨씬 유연하다.
패키지 바깥의 클라이언트에서 바라본 이 불변 객체는 사실상 `final`과 같은 역할을 하게 되는 것이다.

`public`, `protected` 생성자가 없으니 다른 패키지에서는 이 클래스를 확장하기 불가능하다.
정적 팩터리 방식은 다수의 구현 클래스를 활용한 유연성을 제공하고, 이에 더해 다음 릴리스에서 객체 캐싱 기능을 추가해 성능을 끌어올릴 수도 있다.

## 정리

- `Getter`가 있다고 해서 `Setter`를 만들지 말자.
- 클래스는 꼭 필요한 경우가 아니라면 불변이어야 한다.
    - 불변 클래스는 장점이 많으며, 단점이라곤 특정 상황에서의 잠재적 성능 저하일 뿐이다.
- 모든 클래스를 불변으로는 만들 수 없다.
    - 불변으로 만들 수 없는 클래스라도 변경할 수 있는 부분을 최소한으로 줄이자.
    - 가변 필드에 대한 상태의 수를 줄이면, 그 객체를 예측하기 쉬워지고, 오류가 생길 가능성이 줄어든다.
    - 꼭 변경해야 할 필드를 뺀 나머지 모두를 `final`로 선언하자.
- **다른 합당한 이유가 없다면 모든 필드는 `private final` 이어야한다.**
- **생성자는 불변식 설정이 모두 완료된, 초기화가 완벽히 끝난 상태의 객체를 생성해야한다.**
    - 확실한 이유가 없다면 생성자와 정적 팩터리 외에는 그 어떤 초기화 메소드도 `public`으로 제공해서는 안 된다.
    - 재활용할 목적으로 상태를 다시 초기화하는 메소드도 복잡성만 커지고, 성능 이점은 거의 없으니 안 된다.
- 위에서 예시로 든 `Complex` 클래스는 불변을 설명하기 위한 예시일 뿐, 반올림을 제대로 처리하지 않고, 복수소 NaN과 무한대도 다루지 않았다.