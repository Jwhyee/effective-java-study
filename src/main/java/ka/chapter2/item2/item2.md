# 생성자에 매개변수가 많다면 빌더를 고려하라.

## 생성자(constructor)란 무엇일까?

생성자란, 말 그대로 객체를 생성(초기화)하는 역할을 하는 것이다.
우리는 보통 아래 코드와 같이 `new` 키워드와 `Person()`라는 생성자를 통해 새로운 객체를 만든다.

```java
Person p = new Person();
```

`new` 키워드를 사용하면 힙 영역에 객체를 저장할 공간이 할당된다.
즉, 기본 생성자를 사용하면 해당 공간에 비어있는 객체를 저장하게 되는 것이고,
생성자에 값을 추가해서 사용하면 해당 값이 들어있는 객체를 저장하는 것이다.

## 객체 생성 패턴

앞서 봤던 정적 팩터리를 사용하면 간단한 메소드를 통해 이미 만들어 놓은 객체를 반환할 수 있었다.
하지만 선택적 매개변수가 많을 때, 적절히 대응하기 어렵다.
이를 해결하기 위해 생겨난 패턴들을 알아보자!

### 점층적 생성자 패턴

점층적 생성자 패턴을 설명하기 전에 아래 코드와 같이 `Phone`이라는 클래스가 있다.
해당 클래스는 핸드폰 판매 서비스에서 사용자가 등록해야하는 정보다.

```java
public class Phone {
    private final String modelName;
    private final String brand;
    private final int price;
    private final int storage;
    private final int maxBattery;
}
```

이 중에서 `modelName, brand`는 필수로 입력해야하는 정보지만,
`price, size, maxBattery`는 선택사항이다.

하지만 위와 같이 필드에 `final` 키워드를 붙일 경우 생성자를 통해 값을 전달받아야 한다.
이렇게 필수적인 정보와 선택적인 정보가 공존할 때, 점층적 생성자 패턴을 이용한다.

- 필수 매개변수만 받는 생성자
- 필수 매개변수와 선택 매개변수 1개를 받는 생성자
- 필수 매개변수와 선택 매개변수 2개를 받는 생성자
- ...
- 필수 매개변수와 모든 선택 매개변수를 받는 생성자

```java
public Phone(String modelName, String brand) {
    this(modelName, brand, 0);
}

public Phone(String modelName, String brand, int price) {
    this(modelName, brand, price, 0);
}

public Phone(String modelName, String brand, int price, int storage) {
    this(modelName, brand, price, storage, 0);
}

public Phone(String modelName, String brand, int price, int storage, int maxBattery) {
    this.modelName = modelName;
    this.brand = brand;
    this.price = price;
    this.storage = storage;
    this.maxBattery = maxBattery;
}
```

이 방식의 가장 큰 단점은 **매개변수가 많아질수록 클라이언트 코드를 작성하거나 읽기 어려워진다는 것**이다.
아래 코드만 봐도 이해할 수 있을 것이다.

![스크린샷](https://github.com/Jwhyee/CodingTestStudy/assets/82663161/cf946c24-d385-4297-9475-b6bc9705b57a)

또한, 저장공간, 배터리와 같이 입력하고 싶지 않은 곳에도 필수로 넣어줘야하는 강제성이 붙는다.

```java
Phone p = new Phone("iPhone 14 Pro", "Apple", 1550000, 0, 0);
```

만약 이렇게 코드를 작성하다가 순서를 헷갈려서 잘못 넣더라도 컴파일러는 타입이 다르지 않은 이상 실수를 잡아주지도 않는다.

### 자바 빈즈 패턴

위 점층적 생성자 패턴의 단점을 보완하기 위해, 매개변수가 없는 생성자로 객체를 만든 뒤,
세터(setter) 메소드들을 호출해 원하는 매개변수의 값을 설정하는 방식이다.

```java
public class Phone {
    private String modelName;
    private String brand;
    private int price;
    private int storage;
    private int maxBattery;

    public Phone() {}

    public void setModelName(String modelName) { this.modelName = modelName; }

    public void setBrand(String brand) { this.brand = brand; }

    public void setPrice(int price) { this.price = price; }

    public void setStorage(int storage) { this.storage = storage; }

    public void setMaxBattery(int maxBattery) { this.maxBattery = maxBattery; }
}
```

앞서 본 점층적 생성자 패턴에 비해서 우리에게 가장 익숙한 패턴이며,
메소드 이름을 통해 무엇을 설정할지 쉽게 확인할 수도 있다.

```java
Phone iPhone14Pro = new Phone();
iPhone14Pro.setModelName("iPhon 14 Pro");
iPhone14Pro.setBrand("Apple");
iPhone14Pro.setPrice(1550000);
```

하지만 자바빈즈 패턴은 심각한 단점을 지니고 있다.
**객체 하나를 만드려면 메서드를 여러개 호출해야하고,
객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.**

점층적 생성자 패턴은 매개변수들이 유효한지를 생성자에서만 확인하면 일관성을 유지할 수 있었지만,
이렇게 자바빈즈 패턴을 이용해 일관성이 깨진 객체가 만들어지면,
버그를 심은 코드와 그 버그 때문에 런타임에 문제를 겪는 코드가 물리적으로 멀리 떨어져 있을 것이므로 디버깅도 어렵다.

즉, **일관성이 무너지는 문제 때문에 자바빈즈 패턴에서는 클래스를 불변으로 만들 수 없으며**,
스레드 안정성을 얻으려면 프로그래머가 추가 작업을 해줘야한다.

### 빌더 패턴

필수 매개변수만으로 생성자(혹은 정적 팩터리)를 호출해 빌더 객체를 얻는다.
이후 빌더 객체가 제공하는 일종의 세터 메소드들로 원하는 선택 매개변수들을 설정한다.
마지막으로 매개변수가 없는 `build` 메소드를 호출해 우리에게 필요한 객체를 얻는다.

```java
public class Phone {
    private final String modelName;
    private final String brand;
    private final int price;
    private final int storage;
    private final int maxBattery;
    
    public static class Builder {
        private final String modelName;
        private final String brand;
        private int price = 0;
        private int storage = 0;
        private int maxBattery = 0;

        public Builder(String modelName, String brand) {
            this.modelName = modelName;
            this.brand = brand;
        }

        public Builder price(int val) {
            price = val;
            return this;
        }

        public Builder storage(int val) {
            storage = val;
            return this;
        }

        public Builder maxBattery(int val) {
            maxBattery = val;
            return this;
        }

        public Phone build() {
            return new Phone(this);
        }
    }

    private Phone(Builder builder) {
        modelName = builder.modelName;
        brand = builder.brand;
        price = builder.price;
        storage = builder.storage;
        maxBattery = builder.maxBattery;
    }
}
```

이렇게 `Phone` 클래스는 불변이며, 모든 매개변수의 기본값들을 한 곳에 모아 뒀다.
빌더의 세터 메소드들은 자기 자신(this)를 반환하기 때문에 연쇄적으로 호출할 수 있다.
이런 방식을 메소드 호출을 흐르듯 연결된다는 뜻으로 플루언트 API(fluent API) 혹은 **메소드 연쇄(method chaining)**라 한다.

```java
Phone newPhone = Phone.Builder("iPhone 14 Pro", "Apple")
        .price(1550000)
        .storage(128)
        .build();
```

이 클라이언트 코드는 쓰기 쉽고, 무엇보다도 읽기 쉽다.

> 불변(immutable||immutability)은 어떠한 변경도 허용하지 않는다는 뜻으로,
> 주로 변경을 허용하는 가변(muttable) 객체와 구분하는 용도로 쓰인다.
> 대표적으로 String 객체는 한 번 만들어지면 절대 값을 바꿀 수 없는 불변객체이다.
> > String 클래스가 불변임에도 값을 변경할 수 있는 이유는
> > 수정할 때 들어오는 값으로 참조를 변경하는 방식이기 때문임!

#### 빌더 패턴 응용

```java
public abstract class Pizza {
    ...
    abstract static class Builder<T extends Builder<T>> {
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }

        abstract Pizza build();

        protected abstract T self();
    }
}
```

`Pizza`라는 추상 클래스에는 추상 `Builder`를 갖게 하였고,
[NyPizza](https://github.com/Jwhyee/effective-java/blob/master/src/item2/NyPizza.java) 클래스와
[CalzonePizza](https://github.com/Jwhyee/effective-java/blob/master/src/item2/CalzonePizza.java) 클래스는
구체 클래스(concrete class)이므로 구체 빌더를 갖게 하였다.

```java


NyPizza newYorkPizza=new NyPizza.Builder(NyPizza.Size.SMALL)
        .addTopping(SAUSAGE)
        .addTopping(ONION)
        .build();

        CalzonePizza calzonePizza=new CalzonePizza.Builder()
        .addTopping(HAM)
        .sauceInside()
        .build();
```

위 코드는 각 구체 클래스의 빌더를 통해 만든 객체이다.

`.addTopping()` 메소드는 `Pizza.Builder`에 속해있다. 그런데 왜 형변환 없이 메소드 연쇄를 사용할 수 있었을까?

```java
// Pizza.Builder 내부 메소드
abstract static class Builder<T extends Builder<T>> {
    public T addTopping(Topping topping) {
        toppings.add(Objects.requireNonNull(topping));
        return self();
    }
    
    protected abstract T self();
}

// NyPizza.Builder 내부 메소드
public static class Builder extends Pizza.Builder<Builder> {
    @Override
    protected Builder self() {
        return this;
    }
}
```

`Pizza`는 추상 클래스이며, 그 안에 있는 `Builder` 또한 추상 클래스에 속하므로 `this`를 반환하면 안 된다.

때문에 상속받은 `Builder` 클래스를 그대로 반환하기 위해 `self()` 만들어 메소드 연쇄를 지원할 수 있게 하였다.

`NyPizza.Builder`, `CalzonePizza.Builder`와 같은 구체 하위 클래스에서 `Pizza.Builder`를 상속받고,
구체 하위 클래스에 정의한 `Builder`를 제네릭스로 활용해 **재귀적 타입 한정**에 대한 이점을 볼 수 있다.

## 정리

이와 같이 빌더 패턴은 **점층적 생성자 패턴**과 **자바빈즈 패턴**에 비해 다양한 이점이 있다.

1. 가변인수(varargs) 매개변수를 여러개 사용할 수 있다.
2. 객체의 일관성을 보장한다.

하지만, 객체를 만들기 전에 빌더 패턴 먼저 만들어야하며, 매개변수 4개 이상은 되어야 값어치를 한다.

> 생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면, 빌더 패턴을 선택하는게 더 낫다.