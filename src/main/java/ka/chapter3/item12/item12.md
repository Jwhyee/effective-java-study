# toString을 항상 재정의하라.

아래와 같은 `Phone` 클래스가 있다.

```java
public class Phone {
    private int areaCode, prefix, lineNum;

    public Phone(String number) {
        String[] split = number.split("-");
        areaCode = Integer.parseInt(split[0]);
        prefix = Integer.parseInt(split[1]);
        lineNum = Integer.parseInt(split[2]);
    }

}
```

만약 이 `Phone`의 전체 내용을 출력하기 위해 `toString()`을 호출하면 어떻게 될까?

```java
public class PhoneTest {

    @Test
    void postToStringTest() {
        Phone p = new Phone("010-1234-1234");

        System.out.println(p.toString());
    }

}
```

```bash
ka.chapter3.item12.phone.Phone@24b1d79b
```

이와 같이 `Object`의 기본 `toString()`은 아래 형식에 맞게 출력된다.

```java
public final class Object {
    public String toString() {
        // 클래스_이름@16진수로_표시한_해시코드
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}
```

`toString`의 규약을 살펴보면 다음과 같다.

> In general, the toString method returns a string that "textually represents" this object.
> **The result should be a concise but informative representation that is easy for a person to read.**
> It is recommended that all subclasses override this method.
> The string output is not necessarily stable over time or across JVM invocations

> 일반적으로 toString 메서드는 이 개체를 "텍스트적으로 표현"하는 문자열을 반환합니다.
> **결과는 사용자가 읽기 쉬운 간결하지만 유익한 표현이어야 합니다.**
> 모든 하위 클래스가 이 메서드를 재정의하는 것이 좋습니다.
> 문자열 출력이 시간이 지남에 따라 또는 JVM 호출 간에 반드시 안정적인 것은 아닙니다

`toString`의 규약은 간결하고, 사용자가 읽기 쉽도록 만들어주면 된다.

`equals`나 `hashCode`의 규약처럼 대단히 중요하지 않지만,
`toString`을 잘 구현하면 디버깅을 할 때 정말 편하다.

<center>

<p> toString을 재정의하지 않은 경우 </p>    

<img src="https://github.com/Jwhyee/effective-java-study/assets/82663161/6ae4b59d-49ae-455d-8d1a-96c35c9bad63">

<p> toString을 재정의한 경우 </p>

<img src="https://github.com/Jwhyee/effective-java-study/assets/82663161/c2464cef-608a-443a-b0ef-12457cefc3a2">

</center>

이와 같이 현재 객체가 어떤 정보를 담고 있는지 자세히 확인할 수 있으며,
알고리즘을 풀거나 객체의 데이터 흐름을 볼 때 편하다.

## toString 재정의

`toString`은 그 객체가 가진 주요 정보 모두를 반환하는게 좋다.

```java
public class PhoneTest {

    @Test
    void contactMapTest() {
        Map<String, Phone> map = new HashMap<>();
        map.put("준영", new Phone("010-1234-1234"));

        System.out.println(map);
    }

}
```

우리는 `Map`에 저장한 것을 출력할 때, 객체의 해시값이 아닌 실제 값이 나오길 원한다.

```bash
# 원하지 않는 데이터
{준영=ka.chapter3.item12.phone.Phone@281e3708}

# 원하는 데이터
{준영=010-1234-1234}
```

하지만, 객체가 거대하거나 문자열로 표현하기에 적합하지 않다면 무리가 있다.
그렇기 때문에 요약한 정보를 담을 수 있도록 만들어야한다.

### 포맷을 명시해라.

`toString`을 구현할 때면, 반환값의 포맷을 문서화할지 정해야한다.

```java
/**
 * 이 전화번호는 문자열 표현을 반환한다.
 * 이 문자열은 "XXX-YYYY-ZZZZ" 형태의 11글자로 구성된다.
 * XXX : 지역코드, YYY : 프리픽스, ZZZZ : 가입자 번호
 * 각각의 대문자는 10진수 숫자 하나를 나타낸다.
 * 
 * 전화번호의 각 부분의 값이 너무 작아서 자릿수를 채울 수 없다면,
 * 앞에서부터 0으로 채워나간다. 예컨대 가입자 번호가 123이라면
 * 전화번호의 마지막 네 문자는 "0123"이 된다.
*/
@Override
public String toString() {
    return "%03d-%04d-%04d".formatted(areaCode, prefix, lineNum);
}
```

이와 같이 포맷을 명시하면 표준적이면서, 명확하고, 사람이 읽을 수 있게 된다.
명시한 포맷에 맞는 문자열과 객체를 상호 전환할 수 있는 정적 팩터리나 생성자를 함께 제공해주면 좋다.

`LocalDate`를 예시로 객체를 문자열로 변환할 때, 다음과 같은 과정을 거친다.

```java
public class LocalDateTest {
    @Test
    void parseTest() {
        // 날짜를 문자열로 변환
        LocalDate date = LocalDate.now();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println("Formatted Date: " + formattedDate);
    }
}

public final class LocalDate {
    @Override
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }
}

public final class DateTimeFormatter {
    public String format(TemporalAccessor temporal) {
        StringBuilder buf = new StringBuilder(32);
        formatTo(temporal, buf);
        return buf.toString();
    }
}
```

날짜를 문자열로 변환할 때, `StringBuilder` 객체를 생성해서,
해당 버퍼에 날짜에 대한 정보를 넣어준 뒤, `buf.toString()`을 통해서 반환을 한다.

이렇게 객체와 상호 전환할 수 있도록 만들면, 

### 의도는 명확히 밝혀라.

만약 포맷을 명시하지 않을 경우 그 의도를 설명해야한다.

```java
public class Potion {
    /**
     * 이 약물에 관한 대략적인 설명을 반환한다.
     * 다음은 이 설명의 일반적인 형태이나,
     * 상세 형식은 정해지지 않았으며, 향후 변경될 수 있다.
     *
     * "[약물 #9: 유형=사랑, 냄새=테레빈유, 겉모습=먹물]"
     */
    @Override public String toString(){
        ...
    }
}
```

이와 같이 변경될 수 있다고, 대략적인 포맷을 작성하면,
이 포맷에 맞춰 코딩하거나, 특정 값을 빼내어 영구 저장한 프로그래머는 나중에 포맷이 바뀌어
피해를 입어도, 자기 자신을 탓할 수 밖에 없다.

### 반환 값에 포함된 정보를 얻을 수 있는 API를 제공하자.

앞서 작성한 `Phone` 클래스는 지역 코드, 프리픽스, 가입자 번호용 접근자를 제공해야 한다.

```java
public class PhoneTest {
    @Test
    void phoneGetTest() {
        Phone p = new Phone("010-1234-1234");
        String[] split = p.toString().split("-");
        int areaCode = Integer.parseInt(split[0]);
        int prefix = Integer.parseInt(split[1]);
        int lineNum = Integer.parseInt(split[2]);
    }
}
```

이런 방식으로 반환값을 파싱하는 것은 번거롭고, 성능도 나빠질 수 있다.
때문에 이를 반환하는 `getter` 메소드 같은 것을 제공하는 것이 좋다.

### 정적 유틸리티는 toString을 제공할 필요가 없다.

정적 유틸리티뿐이 아니라, 열거타입도 자바가 이미 완벽한 `toString`을 제공하기 때문에 따로 정의하지 않아도 된다.

```java
public enum DayOfWeek {
    MON("월요일"),
    TUE("화요일"),
    WED("수요일"),
    THU("목요일"),
    FRI("금요일"),
    SAT("토요일"),
    SUN("일요일");
    
    private final String korName;

    DayOfWeek(String kor) {
        this.korName = kor;
    }
}
```

```java
public class DayOfWeekTest {
    @Test
    void enumToStringTest() {
        for (DayOfWeek value : DayOfWeek.values()) {
            System.out.println(value.toString());
        }
    }
}
```

```bash
MON
TUE
WED
THU
FRI
SAT
SUN
```

위와 따로 재정의하지 않았음에도 필드값이 정확히 출력된다.

하지만 하위 클래스들이 공유해야할 문자열 표현이 있는 추상 클래스의 경우 `toString`을 재정의 해줘야한다.

```java
public abstract class AbstractCollection implements Collection<E> {
    public String toString() {
        Iterator<E> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }
}
```

## 정리

- 모든 구체 클래스에서 Object의 toString을 재정의하자.
  - 상위 클래스에서 이미 알맞게 재정의한 경우는 예외다.
- toString을 재정의한 클래스는 사용하기 편하고, 디버깅을 쉽게 해준다.
- **해당 객체에 관한 명확하고, 유용한 정보를 읽기 좋은 형태로 반환해야한다.**