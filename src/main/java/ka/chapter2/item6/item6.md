# 불필요한 객체 생성을 피하라

> 똑같은 기능의 객체를 매번 생성하기보다는 객체 하나를 재사용하는 편이 나을 때가 많다.

## String 객체

`String`은 보통 객체를 생성하지 않고, 리터럴을 이용해 값을 초기화한다.
왜 `String`은 참조형인데 `new`를 통한 객체 생성이 아닌 리터럴을 이용하는걸까?

```java
public class StringTest {
    @Test
    void equalsTest1() {
        String str1 = new String("abc");
        String str2 = "abc";

        // 테스트 실패!
        assertTrue(str1 == str2);
    }

    @Test
    void equalsTest2() {
        String str1 = new String("abc");
        String str2 = "abc";

        // 테스트 성공!
        assertTrue(str1.equals(str2));
    }
}
```

그 이유는 문자열풀인 `String pool`에 대해서 먼저 알아봐야한다.

![image](https://github.com/Jwhyee/effective-java-study/assets/82663161/eb17262d-d4a2-467c-b69c-83af4d95f338)

이와 같이 리터럴을 사용하면 `Heap` 영역 내부 `String pool`이라는 곳에서 문자열을 관리한다.
하지만, `new` 키워드를 통해 문자열을 생성하면 그냥 `Heap` 영역 내부에 저장된다.

이 이유는 `intern()`이라는 메소드 때문에 차이가 발생하는데,
리터럴을 이용해 값을 작성하면 `intern()` 메소드를 자동 호출하게 된다.
하지만, `new` 키워드를 사용한 문자열 객체는 해당 메소드 호출 없이 값만 생성한다.

```java
public class StringTest {
    @Test
    void equalsTest3() {
        String str1 = "abc";
        String str2 = "abc";

        // 테스트 성공!
        assertTrue(str1 == str2);
    }
}
```

위 코드와 같이 리터럴만 이용해 같은 값을 가진 문자열을 생성하면,
동일한 `String pool` 내부 값을 참조하기 때문에 두 객체의 주소값이 동일하다는 것을 알 수 있다.

![image](https://github.com/Jwhyee/effective-java-study/assets/82663161/6d115044-c7e2-4e71-9432-5dab081c46f3)

그럼 `new` 키워드를 사용하면 `String pool`에 넣지 못하는 것일까?
앞서 말한 것과 같이 `intern()` 메소드만 사용해주면 같은 효과를 볼 수 있다!

```java
public class StringTest {
    @Test
    void equalsTest4() {
        String str1 = new String("abc").intern();
        int str1HashCode = System.identityHashCode(str1);

        String str2 = "abc";
        int str2HashCode = System.identityHashCode(str2);

        System.out.println("str1HashCode = " + str1HashCode);
        System.out.println("str2HashCode = " + str2HashCode);

        assertTrue(str1 == str2);
    }
}
```
```bash
str1HashCode = 1909546776
str2HashCode = 1909546776
```

즉, 문자열은 객체를 생성해서 사용하는 것보다 리터럴을 사용하는 것이 훨씬 효율적이며,
새로운 인스턴스를 만들긴 하지만, 동일한 값을 가진 여러 문자열이 생기면 단 하나의 값을 참조하기 때문에
같은 객체를 재사용함이 보장된다.

## 정적 팩터리 메소드

> 정적 팩터리 메소드만 제공하는 불변 클래스는 대부분 유틸 클래스로 사용하기 때문에,
> 불필요한 객체 생성을 하지 않아도 된다.

```java
public class BooleanTest {
    @Test
    void booleanObjectTest1() {
        Boolean b1 = new Boolean("true");
        
        // 테스트 성공!
        assertTrue(b1 == true);
    }
}
```

위 코드와 같이 생성자를 통해 문자열을 전달하면 그에 맞는 값을 얻을 수 있다.

![스크린샷](https://github.com/Jwhyee/effective-java-study/assets/82663161/36d83f6a-cc45-43a2-87bf-338e35cd54f9)

사진을 보면 알 수 있듯이 생성자 부분에 오류가 나는데, 신기한건 실행이 가능하다는 것이다.
그 이유는 해당 코드가 `@Deprecated` 사용 자제 어노테이션이 붙었기 때문이다.

```java
public final class Boolean ... {
    ...
    @Deprecated(since="9", forRemoval = true)
    public Boolean(String s) {
        this(parseBoolean(s));
    }
}
```

이를 정상적으로 사용하기 위해서는 정적 팩터리 메소드인 `valueOf()`를 통해 사용하는 것이 좋다.

```java
public class BooleanTest {
    @Test
    void booleanObjectTest2() {
        // 테스트 성공!
        assertTrue(Boolean.valueOf("true") == true);
    }
}
```

이처럼 불필요한 객체를 생성하지 않고도, 원하는 값을 얻어와 사용할 수 있다.

## 생성 비용이 비싼 객체

기본적으로, 생성 비용이 비싼 객체라하면, 생성까지의 시간이 많이 걸리거나 메모리 리소스를 많이 소비하는 것을 의미한다.

대표적으로 이를 확인할 수 있는 것은 시간을 측정하는 것이지만, 내가 만드는 객체가 비싼 객체인지는 매번 명확히 알기는 어렵다.

우선, 생성 비용이 비싼 `s.matches()`를 예시로 확인해보자!

```java
public class RomanNumeral {
    public static boolean isRomanNumeral(String s) {
        return s.matches("^(?=.)M*(C[MD]|D?C{0,3})" +
                "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    }
}
```

위 코드는 `String`에서 제공하는 `matches(regex)`를 통해서,
전달 받은 값이 해당 정규식에 해당되는지 확인하는 코드이다.
이 메소드를 통해 총 몇 밀리초가 걸리는지 확인을 해보자!

```java
public class RomanTest {

    long startTime, endTime;

    @BeforeEach
    void startTimeCheck() {
        startTime = System.nanoTime();
    }

    @AfterEach
    void endTimeCheck() {
        endTime = System.nanoTime();

        long totalNanoTime = endTime - startTime;
        double timeMillis = (double) totalNanoTime / 1000000.0;
        System.out.println("timeMillis = " + timeMillis + "ms");
    }

    @Test
    void romanTest1() {
        System.out.println("s.matches");
        boolean result = RomanNumeral.isRomanNumeral("IX");
        assertTrue(result);
    }
}
```

```bash
timeMillis = 6.246625ms
```

단순히 `String`에서 제공하는 정규표현식 메소드를 사용했을 뿐인데, `6ms`라는 큰 시간이 나왔다.
해당 메소드를 살펴보면 `Pattern.matches`에 의존되어 실행되는 것을 알 수 있다.

또한, `Pattern` 클래스는 정규표현식을 검사하기 위해 유한 상태 머신을 만드는데,
이를 만드는 과정이 복잡하여 인스턴스 생성 비용이 매우 크다.

> 유한 상태 머신이란, 문자열의 형태를 인식하는데 사용되며, 문자열이 패턴과 일치하는지 확인하는 역할을 한다.

이렇게 비싸게 주고 만든 정규표현식용 `Pattern` 인스턴스는 한 번 쓰고 버려지기 때문에 가비지 컬렉션 대상이 된다.

이런 코드는 효율적이지 않고, 호출을 할 때마다 많은 비용이 요구되기 때문에,
불변인 `Pattern` 인스턴스를 클래스 초기화(정적 초기화) 과정에서 직접 생성해 캐싱해두고,
나중에 메소드가 호출될 때마다 인스턴스를 재사용하는 것이 낫다.

```java
public class RomanNumeral {
    private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})" +
            "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    public static boolean isRomanNumeralByPattern(String s) {
        return ROMAN.matcher(s).matches();
    }
    
    ...
}
```

위와 같이 클래스 로딩 시점에 초기화 되도록 정의해놓으면, `ROMAN` 인스턴스를 계속해서 재사용할 수 있게 된다.

```java
public class RomanTest {

    long startTime, endTime;

    @BeforeEach
    void startTimeCheck() {
        startTime = System.nanoTime();
    }

    @AfterEach
    void endTimeCheck() {
        endTime = System.nanoTime();

        long totalNanoTime = endTime - startTime;
        double timeMillis = (double) totalNanoTime / 1000000.0;
        System.out.println("timeMillis = " + timeMillis + "ms");
    }

    @Test
    @Order(2)
    void romanTest1() {
        System.out.println("s.matches");
        boolean result = RomanNumeral.isRomanNumeral("IX");
        assertTrue(result);
    }

    @Test
    @Order(1)
    void romanTest2() {
        System.out.println("Pattern");
        boolean result = RomanNumeral.isRomanNumeralByPattern("IX");
        assertTrue(result);
    }
}
```

```bash
s.matches
timeMillis = 6.246625ms

Pattern
timeMillis = 0.224333ms
```

`@Order` 어노테이션을 통해 테스트 순서를 지정해도
인스턴스를 재사용하는 2번 테스트 방식이 6배나 더 빠른 것을 눈으로 확인할 수 있다.

하지만, 이렇게 만든 기능이 거의 사용하지도 않는다면,
불필요하게 초기화되어 메모리만 먹고있는 꼴이나 다름이 없다.

```java
public class RomanNumeral {
    private static Pattern ROMAN;

    public static boolean isRomanNumeralByPattern(String s) {
        // 지연 초기화 : 메소드를 처음 사용할 때 초기화
        if (ROMAN == null) {
            ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})" +
                    "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
        }
        return ROMAN.matcher(s).matches();
    }
}
```

위 코드와 같이 지연 초기화를 이용해 코드가 처음 호출될 때, `ROMAN`을 초기화 할 수 있다.
하지만, 이런 방식은 코드를 복잡하게 만들고, 성능도 크게 개선되지 않을 때가 많기 때문에 사용하지 않는 것을 권한다.

## 불변 객체의 재사용

> 객체가 불변이라면 재사용해도 안전함이 명백하다.

어댑터(혹은 뷰)는 실제 작업은 뒷단 객체에 위임하고, 자신은 제 2의 인터페이스 역할을 해주는 객체다.
즉, 어댑터는 뒷단 객체만 관리하면 되며, 그 외에는 관리할 상태가 없으므로 뒷단 객체 하나당 어댑터 하나씩만 만들어지면 충분하다.

`Map` 인터페이스의 `keySet()` 메소드는 키 전부를 담은 `Set` 뷰를 반환한다.

```java
public class MapTest {

    Map<Integer, String> fruitRepository;

    @BeforeEach
    void mapInit() {
        fruitRepository = new LinkedHashMap<>();
        fruitRepository.put(1, "사과");
        fruitRepository.put(2, "샤인머스켓");
        fruitRepository.put(3, "물복");
    }
    
    @Test 
    void mapTest1() {
        Set<Integer> repoKeySetView1 = fruitRepository.keySet();
        Set<Integer> repoKeySetView2 = fruitRepository.keySet();
        
        // 테스트 성공!
        assertTrue(repoKeySetView1 == repoKeySetView2);
    }
}
```

위 코드와 같이 `keySet()`을 호출할 때마다 새로운 인스턴스를 생성하는 것이 아닌,
매번 같은 `Set` 인스턴스를 반환하는 것을 알 수 있다.

```java
public class MapTest {
    @Test
    void mapTest1() {
        Set<Integer> repoKeySetView1 = fruitRepository.keySet();
        repoKeySetView1.remove(1);
        Set<Integer> repoKeySetView2 = fruitRepository.keySet();
        
        // 테스트 성공!
        assertTrue(repoKeySetView1 == repoKeySetView2);
    }
}
```

또, `repoKeySetView1`에서 값을 하나 지우고,
다시 `keySet()`을 통해 `Set` 뷰를 만들어도 두 인스턴스는 동일하다는 것을 알 수 있다.

즉, 모두가 똑같은 `Map` 인스턴스를 대변하기 때문에, 반환한 객체 중 하나를 수정하면 다른 모든 객체가 따라서 바뀌는 것이다.

이와 같이 `Map.keySet()`을 통해 뷰 객체를 여러 개 만들어도 상관은 없지만, 모두 같은 객체이므로 그럴 필요도 없고, 이득도 없다.

## 오토 박싱

> 오토 박싱(Auto boxing)이란 기본 타입과 박싱된 기본 타입을 섞어 쓸 때, 자동으로 상호 변환해주는 기술이다.

오토박싱은 기본 타입과 그에 대응하는 박싱된 기본 타입의 구분을 흐려주지만, 완전히 없애주는 것은 아니다.

아래 코드를 보면 `sum` 변수는 `Long` 타입을 사용하였지만, `for`문 내부에서 사용하는 변수는 기본형인 `long`을 사용하였다.

```java
public class BoxingTest {
    ...
    @Test
    void longBoxingTest1() {
        Long sum = 0L;
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            // 오토 박싱
            sum += i;
        }

        assertTrue(sum == 2305843005992468481L);
    }
    ...
}
```

```bash
// 3.410초 ㄷ ㄷ
timeMillis = 3400.193125ms
```

우리가 육안상으로 봤을 때는, 각 타입이 오토박싱 되어서 타입의 구분이 흐려지는 것처럼 보이지만,
실제로는 기본형 `long` 타입이 박싱된 `Long`으로 변환되는 과정에서 객체가 계속 생성되는 것이므로
타입의 구분이 완전히 없어지는 것이 아님을 증명할 수 있다.

```java
public class BoxingTest {
    ...
    @Test
    void longBoxingTest2() {
        long sum = 0L;
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }

        assertTrue(sum == 2305843005992468481L);
    }
    ...
}
```

```java
// 0.69초
timeMillis = 689.634083ms
```

이렇게 박싱된 기본 타입보다는 기본(primitive) 타입을 사용하고,
의도치 않은 오토박싱이 숨어들지 않도록 주의하자!

## 정리

- **객체 생성은 비싸니까 피하는 것이 주제가 아니다.**
  - 요즘의 JVM에서는 작은 객체를 생성하고, 회수하는게 큰 부담이 되지 않는다.
  - 명확성, 간결성, 기능을 위해 객체를 추가로 생성하는 것은 좋은 일이다.
- **(아주 무거운 객체가 아닌) 단순히 객체 생성을 피하고자 객체 풀을 만들지 말자.**
  - DB와 같은 경우에는 생성 비용이 비싸니 재사용하는 것이 낫다.
  - 하지만 일반적으로 객체 풀은 코드를 헷갈리게 만들고 메모리 사용량을 늘리고 성능을 떨어뜨린다.