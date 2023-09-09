# equals를 재정의하려거든 hashCode도 재정의하라.

**item10**의 주의사항 중 아래와 같은 내용이 있었다.

> `equals`와 `hashCode`는 함께 재정의해라.

만약 이를 어길 경우, `hashCode` 일반 규약을 어기게 되며,
`HashMap`, `HashSet`과 같이 `key`의 `HashCode`를 기준으로 사용하는 컬렉션의 원소로 사용할 때, 문제를 일으킬 것이다.

## HashCode와 주소값

C언어 계열에서는 `pointer`를 통해서 특정 값의 실제 메모리 주소를 반환한다.
하지만 자바에서 객체의 값을 출력하면 아래와 같이 나온다.

```java
// 출력 : ka.chapter3.item11.phone.Contact@51114e
System.out.println(contact);
```

가장 뒤에 나오는 `51114e`라는 `hash` 값은 실제 주소값과 관련이 있을까?

### C Lang

C언어 계열은 `*p`, `malloc`, `free` 등과 같은 메소드를 활용한다.

```cpp
#include <stdio.h>

int main() {
    int x = 42;

    printf("x 주소값: %p\n", (void *)&x);

    return 0;
}
```

즉, 메모리를 직접 관리하기 때문에, 객체의 실제 메모리 주소를 직접 얻을 수 있다.

### Java

`Java`에서 객체의 값을 출력하면 다음과 같이 나온다.

```java
import org.openjdk.jol.vm.VM;

public class HashTest {
    
  @Test
  void postHashTest() {
    Post p = new Post(1);

    System.out.println("hashCode : " + p.hashCode());
    System.out.println("identity : " + System.identityHashCode(p));

    System.out.println("VM hash : " + VM.current().addressOf(p));
  }

}
```

```bash
hashCode : 1434041222
identity : 1434041222
```

하지만 `OpenJDK`의 `Java Object Layout (JOL)` 라이브러리를 사용하면 아래와 같은 값이 나온다.

```bash
VM hash : 30320045384
```

이마저도 실제 메모리 주소를 [압축한 값](https://shipilev.net/jvm/anatomy-quarks/23-compressed-references/#_compressed_references)일 수 있기 때문에 실제로 사용하지 않는 것이 좋다.

이와 같이 `Java`와 같은 경우에는 메모리 관리를 사용자가 하는 것이 아닌 `GC`가 직접 처리한다.
때문에 `hashCode`를 이용해 실제 객체 메모리 값을 추상화하고, 압축한다.

C언어는 실제 메모리를 사용하기 때문에 안전성이 낮지만,
`Java`의 경우 직접적인 메모리를 노출시키지 않아, 안전성을 높일 수 있다.

## hashCode의 일반 규약

`Object` 클래스를 살펴보면 아래와 같은 주석을 확인할 수 있다.

```java
public class Object {
    /**
     * - Whenever it is invoked on the same object more than once during an execution of a Java application,
     * the hashCode method must consistently return the same integer, 
     * provided no information used in equals comparisons on the object is modified.
     * This integer need not remain consistent from one execution of an application to another execution of the same application.
     * - If two objects are equal according to the equals method,
     * then calling the hashCode method on each of the two objects must produce the same integer result.
     * - It is not required that if two objects are unequal according to the equals method,
     * then calling the hashCode method on each of the two objects must produce distinct integer results.
     * However, the programmer should be aware that producing distinct integer results for unequal objects may improve the performance of hash tables.
     */
    @IntrinsicCandidate
    public native int hashCode();
}
```

이를 그대로 번역하면 아래와 걑다.

- `equals` 비교에 사용되는 정보가 변경되지 않았다면,
애플리케이션이 실행되는 동안 그 객체의 `hashCode` 메소드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야 한다.
단, 애플리케이션을 다시 실행한다면, 이 값이 달라져도 상관없다.

- `equals(Object)`가 두 객체를 같다고 판단했다면, 두 객체의 `hashCode`는 똑같은 값을 반환해야 한다.

- `equals(Object)`가 두 객체를 다르다고 판단했더라도, 두 객체의 `hashCode`가 서로 다른 값을 반환할 필요는 없다.
단, 다른 객체에 대해서는 다른 값을 반환해야 해시 테이블의 성능이 좋아진다.

여기서 가장 주의해야할 것은 두 번째 조항인, 논리적으로 같은 객체는 같은 해시코드를 반환해야한다는 것이다.

코드를 통해 확인해보자!

```java
public class Contact {
    private final int countryCode;
    private final int prefix, middle, suffix;
    private final Member member;

    public Contact(int countryCode, String phoneNumber, String name) {
        this.countryCode = countryCode;
        this.member = new Member(name);

        String[] split = phoneNumber.split("-");
        prefix = Integer.parseInt(split[0]);
        middle = Integer.parseInt(split[1]);
        suffix = Integer.parseInt(split[2]);
    }
}
```

`HashMap`을 만들어 연락처와 보낼 문자를 저장해보자!

```java
public class ContectTest {
    @Test
    void equalsTest() {
        Map<Contact, String> sendMessageMap = new HashMap<>();

        String msg = "안녕 나 에스파 윈터야 100만원만 보내줘!";

        sendMessageMap.put(new Contact(82, "010-1234-1234", "준영"), msg);

        // 테스트 실패!
        assertTrue(sendMessageMap.get(new Contact(82, "010-1234-1234", "준영"))
                .equals(msg)
        );
    }
}
```

위 코드는 테스트를 실패하면서 아래와 같은 문구를 볼 수 있게 된다.

```bash
java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because the return value of "java.util.Map.get(Object)" is null
```

`sendMessageMap.put`와 `sendMessageMap.get`에서 각각 사용한 `Contact` 객체는 서로 다른 인스턴스이다.

아래와 같이 `get`을 하는 과정에서 보면 `equals`를 사용하는 것을 볼 수 있다.

```java
public class HashMap { 
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(key)) == null ? null : e.value;
    }
    
    final Node<K,V> getNode(Object key) {
        if (first.hash == hash && ((k = first.key) == key || (key != null && key.equals(k))))
            ...
        if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
            ...
    }
}
```

때문에 각자의 `HashCode` 값이 다르기 때문에 `map`에서 검색할 때, `NPE`가 발생한 것이다.

즉, `hashCode`를 재정의하지 않았기 때문에,
논리적 동치인 두 객체가 서로 다른 해시코드를 반환하여, 두 번째 규약을 지키지 못한 것이다.

## 논리적으로 같은 객체는 같은 해시코드를 반환해야 한다.

`hashCode` 메소드는 어떻게 작성해야할까?

```java
public class Contact {
    @Override
    public int hashCode() {
        return 42;
    }
}
```

위 코드는 논리적으로 같은 객체는 같은 해시코드를 반환하게 했지만, 논리적으로 같지 않은 객체도 반환하게 된다.

`Contact`의 모든 인스턴스가 동일한 값만 내어주기 때문에 해시 테이블의 버킷 하나에 담겨 마치 연결 리스트처럼 동작하게 된다.
그 결과 평균 수행 시간이 `O(1)`인 해시 테이블이 `O(n)`으로 느려져서, 객체가 많아지면 도저히 쓸 수 없게 된다.

## hashCode 작성 요령

위와 같은 문제를 해결하기 위해서는, 아래 요령에 따라 `hashCode`를 재정의해주면 된다.

`equals` 비교에 사용되지 않는 필드는 **반드시** 제외해야한다!
그렇지 않을 경우 `hashCode` 규약 두 번째를 위반할 위험이 있다.

### 1. 변수 선언

- 정수형 변수 `result`를 선언한 뒤, 값을 `c`로 초기화 한다.
- `c`는 뒤에서 나올 방식으로 계산한 해시코드이다.

```java
public class Contact {
    @Override
    public int hashCode() {
        int result = c;
        ...
    }
}
```

### 2. 핵심 필드를 토대로 값 구하기

핵심 필드란, `equals` 비교에 사용될 필드를 말한다.

아래에 따라 값을 구한 뒤, `c`와 치환해준다.

#### 기본 타입 필드일 경우

해당 타입에 대한 박싱 클래스의 `hashCode`를 호출해 값을 구한다.

```java
public class Contact {
    private final int middle;
    ...
    @Override
    public int hashCode() {
        int result = Integer.hashCode(middle);
        ...
    }
}
```

#### 참조 타입 필드 + equals 재귀 호출 경우

이 필드의 `equals`를 통해 계속해 비교할 경우 `hashCode`도 재귀적으로 호출해준다.

계산이 복잡해질 경우, 이 필드의 표준형을 만든 뒤 그 표준형의 `hashCode`를 호출한다.
필드의 값이 `null`이면, 0을 사용한다.

```java
public class Contact {
    private final Member member;
    ...

    @Override
    public int hashCode() {
        int result = Integer.hashCode(middle);
        result = 31 * result + (member != null ? member.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact c)) return false;
        return countryCode == c.countryCode
                && prefix == c.prefix
                && middle == c.middle
                && suffix == c.suffix
                && Objects.equals(member, c.member);
    }
}
```

#### 필드가 배열일 경우

원소 각각을 별도 필드처럼 다루며, 각 필드에 대한 해시코드를 구해 계산하고, `result` 변수를 갱신한다.

모든 원소가 핵심 원소라면, `Arrays.hashCode`를 사용한다.

```java
public class Contact {
    private String[] nickname;
    ...

    @Override
    public int hashCode() {
        int c = Arrays.hashCode(nickname);
        int result = Objects.hashCode(middle);
        result = 31 * result + c;

        return result;
    }
}
```

### 3. 자문

위 과정을 토대로 `hashCode`를 다 구현했다면, 동치인 인스턴스에 대해 똑같은 해시 코드를 반환할지 자문해봐야한다.

각 핵심필드인 `countryCode`, `middle`, `suffix`, `member`에 대해서 작업을 수행하였고,
`equals` 비교에 사용하지 않은 `prefix`도 제외했다.

```java
public class Contact {
    private final int countryCode;
    private final int prefix, middle, suffix;
    private final Member member;

    @Override
    public int hashCode() {
        int result = Integer.hashCode(countryCode);
        result = 31 * result + Integer.hashCode(middle);
        result = 31 * result + Integer.hashCode(suffix);
        result = 31 * result + (member != null ? member.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        ...
        return countryCode == c.countryCode
                && middle == c.middle
                && suffix == c.suffix
                && Objects.equals(member, c.member);
    }
}
```

이렇게 곱셈을 구현하는 이유는 비슷한 필드가 여러 개일 때, 해시 효과를 크게 높여주기 때문이다.
만약, `String`의 `hashCode`를 곱셈 없이 구현한다면, 모든 아나그램의 해시코드가 같아질 수 있다.

## 해시코드 팁

### Guava 라이브러리

위에서 작성한 방법으로도 충분히 해싱 기능을 잘 수행하지만,
해시 충돌을 더욱 적은 방법을 사용하고 싶다면 `com.google.common.hash.Hashing`을 사용하면 된다.

```java
@Override
public int hashCode() {
    HashFunction hashFunction = Hashing.sha256();
    HashCode hashCode = hashFunction.newHasher()
            .putInt(countryCode)
            .putInt(middle)
            .putInt(middle)
            .putInt(suffix)
            .putObject(member)
            .hash();
    return hashCode.asInt();
}
```

코드는 기존에 비해 더 길어졌지만,
비밀번호를 암호화할 때 사용하는 해싱 알고리즘 중 하나인, `SHA256` 방식으로 해시값을 만들어낸다.

### hashCode 생성 기능 사용 자제

`IntelliJ`에서 `hashCode`를 생성하면 아래와 같이 나온다.

```java
@Override
public int hashCode() {
    return Objects.hash(countryCode, prefix, middle, suffix, member);
}
```

이러한 방식은 우리가 작성한 `hashCode`에 비해 성능이 좋지 않다.

```java
public final class Objects {
    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }
}

public class Arrays {
    public static int hashCode(Object a[]) {
        if (a == null)
            return 0;

        int result = 1;

        for (Object element : a)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;
    }
}
```

위와 같이 `Object` 타입으로 인자를 받기 때문에 박싱과 언박싱이 계속되며,
배열로 변환되어 우리가 작성한 `hashCode`와 같은 형태로 계산된다.

앞서 `Item6`에서 봤던 박싱 및 언박싱의 시간 차이를 보면 겁이 날 수 밖에 없다.

### 캐싱 방식 고려

클래스가 불변이고, 해시 코드를 계산하는 비용이 크다면,
매번 새로 계산하는 것이 아닌 필드에 캐싱하는 방식을 고려해야 한다.

만약, 해당 타입의 객체가 주로 해시의 키로 사용될 것 같다면 인스턴스가 만들어질 때 해시코드를 계산해둬야 한다.

```java
public class Contact {
    private int hashCode;
    private final int countryCode;
    private final int prefix, middle, suffix;
    private final Member member;

    public Contact(int countryCode, String phoneNumber, String name) {
        this.countryCode = countryCode;
        this.member = new Member(name);

        String[] split = phoneNumber.split("-");
        prefix = Integer.parseInt(split[0]);
        middle = Integer.parseInt(split[1]);
        suffix = Integer.parseInt(split[2]);

        int result = Integer.hashCode(countryCode);
        result = 31 * result + Integer.hashCode(middle);
        result = 31 * result + Integer.hashCode(suffix);
        result = 31 * result + (member != null ? member.hashCode() : 0);
        hashCode = result;
    }
    
    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Integer.hashCode(countryCode);
            result = 31 * result + Integer.hashCode(middle);
            result = 31 * result + Integer.hashCode(suffix);
            result = 31 * result + (member != null ? member.hashCode() : 0);
        }
        return result;
    }
}
```

위와 같은 경우가 아니라면 지연 초기화(Lazy Initialization) 전략은 어떨까?
필드를 지연 초기화하려면 그 클래스를 `Thread-Safe`하게 만들도록 신경 써야 한다.

즉, 스레드 안전성까지 고려해야하는 것이다.

```java
private int hashCode; // 기본값인 0으로 초기화

@Override
public int hashCode() {
    int result = hashCode;
    if (result == 0) {
        result = Integer.hashCode(countryCode);
        result = 31 * result + Integer.hashCode(middle);
        result = 31 * result + Integer.hashCode(suffix);
        result = 31 * result + (member != null ? member.hashCode() : 0);
    }
    return result;
}
```

여러 스레드에서 동시에 접근할 경우, 동시에 계산하여 처음 의도와 다르게 여러번 계산하는 상황이 발생할 수 있다.
간단하게 `synchronized`를 붙여 한 쓰레드씩 접근할 수 있도록 하는 것이 좋을 것 같다.

## 정리

- **성능을 위해 해시코드를 계산할 때 핵심 필드를 생략하면 안 된다.**
  - 해시 품질이 나빠져 해시 테이블의 성능을 심각하게 떨어뜨릴 수 있다.
- **`hashCode`가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말자.**
  - 그래야 클라이언트가 이 값에 의지하지 않게 되고, 추후에 계산 방식을 바꿀 수 있다.
- **`equals`를 재정의할 때는 `hashCode`도 반드시 재정의하자.**
  - 그렇지 않을 경우 프로그램의 동작이 이상해질 수 있다.
- **`hashCode`는 API 문서에 기술된 일반 규약을 따라야한다.**
  - 서로 다른 인스턴스라면 되도록 해시코드도 서로 다르게 구현해야 한다.