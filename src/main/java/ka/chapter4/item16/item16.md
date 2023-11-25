# public 클래스에서는 public 필드가 아닌 접근자 메소드를 사용하라.

아래와 같은 클래스는 인스턴스 필드를 모아놓는 일 외에는 아무런 목적도 없는 퇴보한 클래스이다.

```java
class Point {
    public double x;
    public double y;
}
```

이러한 클래스는 데이터 필드에 직접 접근할 수 있지만, 아래와 같은 불이익이 따른다.
- 캡슐화의 이점을 제공하지 못한다.
- API를 수정하지 않고서는 내부 표현을 바꿀 수 없다.
- 불변식을 보장할 수 없다.
- 외부에서 필드에 접근할 때, 부수 작업을 수행할 수도 없다.

우리가 `Spring`에서 DTO와 같이 단순히 데이터만 전송하는 클래스를 보면 대부분 아래와 같이 구현한다.

```java
class PostDto {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
```

실제 필드는 `private`로 두고, 자바 빈즈 패턴을 이용해 값을 지정하고, 접근자(getter)를 통해 값을 가져온다.
`public` 클래스일 경우 **패키지 바깥에서 접근할 수 있는 클래스라면 접근자를 제공**함으로써 클래스 내부 표현 방식을 언제든 바꿀 수 있는 유연성을 얻을 수 있다.

요구사항 중 `title`의 반환값 앞에 `origin :`을 추가해야하는 사항이 있다고 가정하자.

```java
public String getTitle(){
    return "origin : " + title;
}
```

접근자를 사용할 경우 위와 같이 반환값을 제어할 수 있지만, 만약 `title` 필드의 제어자를 `public`으로 공개해두었다면, 해당 필드에 접근할 수 있는 권한이 생기는 것이므로 내부 표현 방식을 마음대로 바꿀 수 없게 된다.

`package-private` 클래스의 혹은 `private` 중첩 클래스라면 데이터 필드를 노출한다 해도 하등의 문제가 없다.
이러한 방식은 클래스 선언 면에서나 이를 사용하는 클라이언트 코드 면에서나 접근자 방식보다 훨씬 깔끔하다.

> item15에서 정리한 package-private 타입의 Node 클래스 참고

하지만 이럴 경우 클라이언트 코드가 해당 클래스 내부 표현에 묶이긴 하나, 클라이언트 코드도 결국 같은 패키지 안에서만 동작하는 코드일 뿐이다.

따라서 패키지 바깥 코드는 전혀 손대지 않고도 데이터 표현 방식을 바꿀 수 있다. `private` 중첩 클래스의 경우라면 수정 범위가 더 좁아져서 이 클래스를 포함하는 외부 클래스까지로 제한된다.

### 안 좋은 예시

`java.awt` 패키지의 `Point`와 `Dimension` 클래스이다. `Point` 클래스의 내부를 살펴보면 아래와 같이 되어있다.

```java
public class Point extends Point2D implements java.io.Serializable {
    public int x;
    public int y;
}
```

자신있게 대놓고 기본 필드를 `public`으로 선언해 놓은 것을 볼 수 있다.

```java
import java.awt.Point;

public class TestClass {
    @Test
    void pointTest() {
        Point point = new Point();
        point.x = 10;
        point.y = 20;

        point.translate(1, 2);

        System.out.println(point);
    }
}
```

이러한 클래스를 흉내내지 않고, 타산지석으로 삼으면 된다.

> 타산지석(他山之石) : 하찮은 남의 언행일지라도 자신을 수양하는 데에 도움이 된다

### 불변 필드 노출

public 클래스의 필드가 불변이라면, 직접 노출할 때의 단점이 조금은 줄어든다.
불변식은 보장할 수 있지만, 여전히 결코 좋은 방법은 아니다. API를 변경하지 않고는 표현 방식을 바꿀 수 없고, 필드를 읽을 때 부수 작업을 수행할 수 없다는 단점은 여전하다.

```java
public final class Time {
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;
    
    public final int hour;
    public final int minute;
    
    public Time(int hour, int minute) {
        if (hour < 0 || hour >= HOURS_PER_DAY) {
            throw new IllegalArgumentException("시간 : " + hour);
        }
        if (minute < 0 || minute >= MINUTES_PER_HOUR) {
            throw new IllegalArgumentException("분 : " + minute);
        }
        this.hour = hour;
        this.minute = minute;
    }
}
```

## 정리

- public 클래스는 절대 가변 필드를 직접 노출해서는 안 된다.
- 불변 필드라면 노출해도 덜 위험하지만, 완전히 안심할 수는 없다.
  - 하지만 package-private 클래스 혹은 private 중첩 클래스에서는 불변이든 가변이든 필드를 노출하는 편이 나을 수 있다.