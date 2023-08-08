# private 생성자나 열거 타입으로 싱글톤임을 보증하라.

## 싱글톤이란?

> 싱글톤(singleton)이란, 인스턴스를 오직 하나만 생성할 수 있는 클래스이다.
> 싱글톤의 전형적인 예로는 함수와 같은 **무상태 객체**나 설계상 유일해야하는 시스템 컴포넌트를 들 수 있다.

### 무상태 객체란?

우선 상태가 있는(stateful) 클래스가 무엇인지부터 알아보는 것이 좋을 것 같다!

```java
/**
 * 사용자의 주문을 저장하는 클래스
 * */
public class Order {
    private String nickname;

    private int price;

    public Order(String nickname, int price) {
        this.nickname = nickname;
        this.price = price;
    }
}
```

```java
/**
 * 사용자의 주문을 처리하는 클래스
 * */
public class OrderService {
    private Order nextOrder;
    private int orderCount;

    public void makeOrder(String nickname, int price) {
        nextOrder = new Order(nickname, price);
        orderCount++;
    }

    public Order getOrder() {
        return nextOrder;
    }

}
```

```java
// 사용자가 주문을 요청하는 테스트 클래스
public class OrderServiceTest {
    
    @Test
    void orderTest1() {
        OrderService service = new OrderService();
        service.makeOrder("user1", 10000);
        service.makeOrder("user2", 30000);

        System.out.println(service.getOrder());
        
    }
}
```

위와 같이 상태를 저장할 수 있는 것을 상태가 있는 클래스라고 부른다.
하지만 가장 큰 문제는 `Order nextOrder`라는 객체를 공유하기 때문에
여러 주문이 들어와도 가장 마지막 주문만 확인할 수 있게 된다.
즉, 주문량에 따라 앞서 들어온 주문은 무시될 수 있다는 것이다.

반대로 무상태는 **상태를 공유하는 필드 변수가 없는 것**을 의미한다.
즉, 특정 클라이언트가 의존할 수 있는 필드 변수가 존재할 수 없고, 값을 변경할 수 없어야한다.
위 코드를 싱글톤을 사용한 무상태 객체로 변환한다면 다음과 같다.

```java
public class OrderService {
    // private 생성자를 통해 현재 OrderService를 싱글톤으로 만듦
    public static final OrderService INSTANCE = new OrderService();
    private OrderService(){}
    
    // 외부에서 만들어진 OrderRepository 객체를 가져옴
    // 해당 필드는 클라이언트 코드에서 의존하지 않으며, 객체를 참조하는 용도로만 사용
    private final OrderRepository repository = OrderRepository.INSTANCE;

    // 아래 두 메소드는 클래스 내부 상태에 의존하지 않는다.
    // 주어진 매개 변수와 외부 OrderRepository 객체만으로 동작
    public void makeOrder(String nickname, int price) {
        repository.save(new Order(nickname, price));
    }

    public List<Order> getOrderList() {
        return repository.findAllOrder();
    }

}
```

## 싱글톤을 생성하는 방법

### 1. public static final 필드 방식

```java
public class DateTimeUtil {
    public static final DateTimeUtil INSTANCE = new DateTimeUtil();
    private DateTimeUtil() {}

    public String getPassedTime(LocalDateTime localDateTime) {
        ...
    }
}
```

위 코드와 같이 생성자를 `private`로 감추면, 다른 클래스에서 해당 객체를 생성할 수 없게 된다.
즉, `DateTimeUtil.INSTANCE`를 통해서만 객체를 생성할 수 있으며,
해당 객체는 `static final` 필드이기 때문에 딱 한 번만 생성이 된다.

```java
public class UtilTest {
    @Test
    void utilTest1() {
        // 테스트 실패!
        // 메소드가 static이 아니기 때문에 컴파일 에러 발생!
        String time = DateTimeUtil.getPassedTime(LocalDateTime.now().minusHours(2));
        System.out.println(time);
        assertTrue(time.equals("2시간 전"));
    }

    @Test
    void utilTest2() {
        // 테스트 성공!
        // 정상 접근 가능!
        DateTimeUtil util = DateTimeUtil.INSTANCE;
        String time = util.getPassedTime(LocalDateTime.now().minusHours(2));
        System.out.println(time);
        assertTrue(time.equals("2시간 전"));
    }
}
```

이러한 `Utility` 클래스는 여러 곳에서 사용하기 위해 만든 클래스이므로, 싱글톤으로 사용하기 유용하다.
하지만, 메소드 쓰임에 따라 `item1`에서 공부한 정적 팩터리 방식으로 만드는 것이 더 유리할 수도 있다!

#### 취약점

`Reflection`에서 제공하는 API를 통해 `private` 생성자를 가져올 수 있는 방법이 존재한다.

> `Reflection`은 Class 객체를 통해 클래스의 정보를 가져오고,
> 객체를 생성하거나 메소드를 호출하는 등의 작업을 할 수 있도록 지원하는 기능이다.

```java
public class OrderServiceTest {
    
    @Test
    void utilReflectionTest() {
        try {
            // DateTimeUtil 클래스의 생성자를 가져온다.
            // getDeclaredConstructor() -> 모든 접근 제어자를 무시하고, 클래스의 생성자를 가져온다.
            Constructor<DateTimeUtil> constructor = DateTimeUtil.class.getDeclaredConstructor();
            
            // 가져온 생성자를 접근 가능하도록 설정
            constructor.setAccessible(true);
            
            // 설정된 생성자를 사용해 DateTimeUtil 클래스의 새로운 인스턴스를 생성
            DateTimeUtil util = constructor.newInstance();
            
            // 인스턴스 내부 메소드 호출
            util.showCurrentTime();
            
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            // 다양한 Exception이 발생할 수 있음!
            throw new RuntimeException(e);
        }
    }
    
}
```

혹여나 이러한 방식의 공격을 방어하려면 아래와 같이 또 다른 객체가 생성될 때, 예외를 던져주면 된다.

```java
public class DateTimeUtil {
    public static final DateTimeUtil INSTANCE = new DateTimeUtil();
    private static boolean instanceCreated = false;

    private DateTimeUtil() {
        if (instanceCreated) {
            throw new IllegalStateException("이미 객체가 생성되어 있습니다.");
        }
    }

    static {
        instanceCreated = true;
    }
}
```

여기서 사용한 `static` 블록은 클래스가 로딩될 때(처음으로 해당 클래스가 사용될 때) 자동으로 실행되는 블록이다.

```java
// DateTimeUtil 클래스 로드
Constructor<DateTimeUtil> constructor = DateTimeUtil.class.getDeclaredConstructor();

// 이미 위에서 클래스가 로딩되어 instanceCreated가 true인 상태
// 때문에 IllegalStateException 발생!
DateTimeUtil util = constructor.newInstance();
```

### 2. 정적 팩터리 방식

`item1`에서 봤던 것과 같이 이번에는 정적 팩터리 방식으로 인스턴스를 가져오는 것이다.

```java
public class DateTimeUtil {
    private static final DateTimeUtil INSTANCE = new DateTimeUtil();
    private DateTimeUtil() { ... }
    
    public static DateTimeUtil getInstance() { return INSTANCE; }
    
    ...

    public String getPassedTime(LocalDateTime localDateTime) {
        ...
    }
}
```

1번에서 봤던 방식과 크게 달라 보이는 점은 없지만, API를 바꾸지 않고도 싱글톤이 아니게 변경할 수 있다는 장점이 있다.

```java
public class DateTimeUtil {
    // 스레드별 독립적으로 관리할 TreadLocal 선언
    // threadLocalInstance를 초기화하기 위해 스레드별로 인스턴스를 생성하는 withInitial() 사용
    // 이를 통해 스레드별 새로운 객체 생성
    private static final ThreadLocal<DateTimeUtil> threadLocalInstance = ThreadLocal.withInitial(() -> new DateTimeUtil());

    // 스레드별로 할당된 DateTimeUtil 인스턴스 반환
    public static DateTimeUtil getInstance() {
        // 현재 스레드에 할당된 DateTimeUtil 인스턴스 반환
        // 각 스레드별로 자신만의 인스턴스를 사용할 수 있음
        return threadLocalInstance.get();
    }

    // 몇 번째로 생성된 인스턴스인지 나타내는 변수 
    // AtomicInteger -> 멀티 스레드 환경에서도 값의 일관성을 보장함
    /*
      1, 2번 스레드에서 int에 동시에 접근할 경우에 대한 예시
     1번 스레드 : -----(1)--------------(1)-----
     2번 스레드 : -----------(2)---(2)----------
     - 1번 스레드가 변수를 읽고 1을 증가시키기 전에 2번 스레드가 변수를 읽고 1을 증가시킨다면,
     - 1번 스레드가 1을 증가시키기 전에 2번 스레드가 증가시킨 결과를 반영하지 못해 무결성이 깨짐 
    */
    private static final AtomicInteger counter = new AtomicInteger(1);

    private final int instanceNumber;

    private DateTimeUtil() {
        // 멀티 스레드 환경에서 객체가 생성될 때마다 값을 증가시킴
        instanceNumber = counter.getAndIncrement();
    }

    public void showCurrentTime() {
        // 인스턴스 번호와 현재 시간 출력
        System.out.println("Instance " + instanceNumber + ": " + System.currentTimeMillis());
    }
}
```

앞서 봤던 코드와 동일하게 `getInstance()`라는 메소드를 통해 `DateTimeUtil` 객체를 가져오지만,
객체를 가져오는 방식은 완전히 변경되었다.

앞서 진행했던 테스트에서 `showCurrentTime()`을 추가해 실행하면 아래와 같은 결과가 나온다.

```java
Instance 1: 1691418372053
Instance 2: 1691418372058
```

분명 1개의 스레드만 사용하고 있을텐데 2가 나오는 이유는 `Reflection` 테스트 때문이다.
우리는 단일 스레드 환경에서 다른 객체가 생성되는 것을 방지하기 위해 방어 코드를 작성했지만,
이와 같이 `ThreadLocal`을 사용하면 새로운 객체가 만들어져도 다른 스레드에서 작업하기 때문에 문제가 발생할 수 없다.

#### 공급자 사용

이해 못한 부분 : 공급자를 사용해서 꺼내오는 객체는 모두 싱글톤이라 동일한 객체인데,
왜 굳이 공급자를 사용해서 여러 객체를 사용하는 것인지?

> 공급자란, 정적 팩터리 메소드를 `Supplier` 인터페이스에 대한 참조로 바꿔서 객체를 생성하는 것

```java
public class DateTimeUtil {
    ...
    public static Supplier<DateTimeUtil> getDateTimeUtilSupplier() {
        return DateTimeUtil::getInstance;
    }
}
```

```java
public class UtilTest {
    @Test
    void utilSupplierTest() {
        Supplier<DateTimeUtil> dateTimeUtilSupplier = DateTimeUtil.getDateTimeUtilSupplier();

        DateTimeUtil dateTimeUtil1 = dateTimeUtilSupplier.get();
        DateTimeUtil dateTimeUtil2 = dateTimeUtilSupplier.get();

        assertTrue(dateTimeUtil1 == dateTimeUtil2);
    }
}
```

#### 클래스 직렬화

> 직렬화란 객체를 Stream으로 변환하는 과정을 의미

이해 못한 부분
- 직렬화란 List처럼 여러 객체를 나열(?)하는 것으로 이해했는데, 싱글톤 객체를 굳이 직렬화하는 이유를 모르겠음
- 싱글톤은 어차피 하나의 객체만을 사용하는 것이 목적인데, 굳이 직렬화를 사용해서 얻는 이점이 무엇인지?.? 

### 3. 열거 타입 방식의 싱글턴 방식

#### 열거 타입이란?

`enum`은 클래스와 같이 멤버 변수, 메소드 등을 정의할 수 있다.
가장 다른 점은 접근 제어자나 `static` 키워드가 없어도 상수를 사용할 수 있다는 점이다.

```java
public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}
```

```java
DayOfWeek today = DayOfWeek.MONDAY;
```

위 코드를 예시로 `MONDAY`의 값을 갖는 객체로도 활용할 수 있다.

#### 싱글톤 생성 방식

위에서 설명한대로 열거형 상수를 1개만 선언한다는 것은 해당 객체는 무조건 싱글톤이라는 것과 동일한 말이다.

```java
public enum DateTimeUtil {
    
    INSTANCE;
    
    public String getPassedTime(LocalDateTime localDateTime) {
        ...
    }

}
```

앞서 봤던 방식과 다르게 가장 간단하다.
대부분의 상황에서 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다.