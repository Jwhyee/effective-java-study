# int 상수 대신 열거 타입을 사용하라.


열거 타입은 일정 개수의 상수 값을 정의하고, 그 외의 값은 허용하지 않는 타입이다.

## 1. 정수 열거 패턴

```java
public class Fruit {  
	public static final int APPLE_FUJI = 0;  
	public static final int APPLE_PIPPIN = 1;  
	public static final int APPLE_GRANNY_SMITH = 2;  
	  
	public static final int ORANGE_NAVEL = 0;  
	public static final int ORANGE_TEMPLE = 1;  
	public static final int ORANGE_BLOOD = 2;  
}
```

정수 열거 패턴(int enum pattern) 기법에는 단점이 많다. 타입 안전을 보장할 방법이 없으며, 표현력도 좋지 않다. 오렌지를 건네야할 메소드에 사과를 보내고, 동등 연산자(`==`)로 비교하더라도 컴파일러는 아무런 경고 메시지를 출력하지 않는다.

```java
int i = (APPLE_FUJI - ORANGE_TEMPLE) / APPLE_PIPPIN;
```

자바가 정수 열거 패턴을 위한 별도 이름 공간(namespace)을 지원하지 않기 때문에 어쩔 수 없이 접두어를 써서 이름 충돌을 방지한다. 이러한 패턴을 사용한 프로그램은 깨지기 쉽다. 또한, 디버깅을 할 때, 이름이 아닌 숫자로만 보이기 때문에 도움이 되지 않는다.

이를 변형한 문자열 열거 패턴(string enum pattern)도 있는데, 상수는 의미라도 출력할 수 있지만, 문자열 상수는 하드 코딩을 하기 때문에 문자열에 오타가 있어도 컴파일러는 확인할 길이 없어 런타임 버그가 생겨버린다.

## 2. 열거 타입

위 문제를 해결하기 위해 단순한 열거 타입을 사용하면 편하다.

```java
enum Apple { FUJI, PIPPIN, GRANNY_SMITH }  
enum Orange { NAVEL, TEMPLE, BLOOD }
```

열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스를 만들어 `public static final` 필드로서 활용한다. 열거 타입은 외부에서 접근할 수 있는 생성자를 제공하지 않으므로, 사실상 final이며, 싱글톤이다.

아래 코드와 같이 Apple 타입에 대한 매개 변수를 받은 함수를 선언했다면, 당연히 Apple 타입에 대해서만 값을 받을 수 있다.

```java
private static void printApple(Apple apple) {  
	switch (apple) {  
		case FUJI -> System.out.println("FUJI");  
		case PIPPIN -> System.out.println("PIPPIN");  
		case GRANNY_SMITH -> System.out.println("GRANNY_SMITH");  
	}  
}
```

만약 정수 열거 패턴을 사용했다면 아무런 값을 넣어도 컴파일 때 에러를 보내지 않을 것이다. 열거 타입에는 각자의 이름 공간이 있어서 이름이 같은 상수도 공존할 수 있다. 새로운 상수를 추가하거나 순서를 바꿔도 문제가 없다.

### 2-1. 열거 타입 내부 함수 선언

열거 타입은 정수 열거 패턴의 단점들을 모두 해소해주고, 임의의 메소드나 필드를 추가할 수 있고, 인터페이스 또한 구현할 수 있다.

```java 
public enum Planet {  
	MERCURY(1.111e+11, 2.111e1),  
	VENUS(1.222e+22, 2.222e2),  
	EARTH(1.333e+33, 2.333e3),  
	MARS(1.444e44, 2.444e4),  
	JUPITER(1.555e55, 2.555e5),  
	SATURN(1.666e66, 2.666e6),  
	URANUS(1.777e77, 2.777e7),  
	NEPTUNE(1.888e88, 2.888e8);  
	  
	private final double mass;  
	private final double radius;  
	private final double surfaceGravity;  
	  
	private static final double G = 6.67300E-11;  
	  
	Planet(double mass, double radius) {  
		this.mass = mass;  
		this.radius = radius;  
		surfaceGravity = G * mass / (radius * radius);  
	}  
	  
	public double mass() {return mass;}  
	public double radius() {return radius;}  
	public double surfaceGravity() {return surfaceGravity;}  
	  
	public double surfaceWeight(double mass) {  
		return mass * surfaceGravity;  
	}  
}
```

열거 타입에 값을 부여하려면 생성자를 통해 특정 데이터와 연결지어 필드에 저장하면 된다. 열거 타입은 근본적으로 불변이라 final 필드여야 한다. public으로 선언해도 문제는 없지만 private로 두고, 별도의 public 접근자 메소드를 두는 것이 낫다.

### 2-2. 열거 타입의 나열

이러한 열거 타입은 정적 메소드인 values를 제공해 선언된 순서대로 iteration을 사용할 수 있다.

```java
public class PlanetTest {  
	public static void main(String[] args) {  
		double earthWeight = Double.parseDouble("1.1");  
		double mass = earthWeight / Planet.EARTH.surfaceGravity();  
		for (Planet p : Planet.values()) {  
			System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));  
		}  
	}  
}
```

열거 타입에서 상수를 하나 제거하더라도 해당 참조를 사용하지 않을 경우 클라이언트에는 아무런 영향도 가지 않는다. 널리 쓰이는 열거 타입은 톱 레벨 클래스로 만들고, 특정 톱레벨 클래스에서만 쓰인다면, 해당 클래스의 멤버 클래스로 만들어라.

### 2-3. 열거 타입의 상수별 메소드 구현

만약 계산기 기능을 구현하기 위해 연산에 따른 계산을 할 경우 다음과 같이 구현할 수 있다.

```java
public enum Operation {  
	PLUS, MINUS, TIMES, DIVIDE;  
	  
	public double apply(double x, double y) {  
		switch (this) {  
			case PLUS: return x + y;  
			case MINUS: return x - y;  
			case TIMES: return x * y;  
			case DIVIDE: return x / y;    
		}
		throw new AssertionError("알 수 없는 연산");
	}  
}
```

위 코드에서 default에 도달할 일은 없지만 이를 생략하면 컴파일 에러가 발생한다. 또한, 새로운 상수를 추가하면, 해당 case 문도 추가해야하는 번거로움이 있다. 이를 개선한 버전은 2가지가 있다.

#### 개선안 1. 자바 13의 람다식 적용

앞서 봤던 코드에서는 새로운 상수를 추가한 뒤 apply에 해당 분기를 추가하지 않고 실행해도 throw 구문이 있어 컴파일 에러가 발생하지 않는다. 하지만 JDK 12에 추가된 람다식과 expression을 활용하면 thorw를 사용하지 않아도 된다.

```java
public enum Operation {  
	PLUS, MINUS, TIMES, DIVIDE;  
	  
	public double apply(double x, double y) {  
		return switch (this) {  
			case PLUS -> x + y;  
			case MINUS -> x - y;  
			case TIMES -> x * y;  
			case DIVIDE -> x / y;  
		};  
	}  
}
```

때문에 새로운 상수를 추가할 경우 apply에서 꼭 추가하도록 강제하기 때문에 컴파일 에러를 띄워준다.

#### 개선안 2. 상수별 메소드 구현

책에 나오는 것과 같이 각 상수마다 apply 메소드를 구현하는 방법이다.

```java
public enum Operation {  
	PLUS {public double apply(double x, double y) {return x + y;}},  
	MINUS {public double apply(double x, double y) {return x - y;}},  
	TIMES {public double apply(double x, double y) {return x * y;}},  
	DIVIDE {public double apply(double x, double y) {return x / y;}};  
	  
	public abstract double apply(double x, double y);  
}
```

apply를 추상 메소드로 두고, 각 상수에서 구현하도록 만들면 새로운 상수하고, apply를 구현하지 않으면 컴파일 에러가 발생하게 된다.

### 2-4. 열거 타입의 toString 구현

각 상수가 어떤 기호를 의미하는지 보고 싶다면 toString을 재정의하면 된다.

```java
public enum Operation {  
	PLUS("+") {public double apply(double x, double y) {return x + y;}},  
	MINUS("-") {public double apply(double x, double y) {return x - y;}},  
	TIMES("*") {public double apply(double x, double y) {return x * y;}},  
	DIVIDE("/") {public double apply(double x, double y) {return x / y;}}  
	;  
	  
	private final String symbol;  
	Operation(String symbol) {this.symbol = symbol;}  
	  
	@Override public String toString() {return symbol;}  
	public abstract double apply(double x, double y);  
}
```

위처럼 구현하면 각 상수를 출력할 때마다 상수의 symbol을 출력해준다. 또한, toString을 재정의할 때, fromString 메소드도 함께 제공하면 좋다.

```java
private static final Map<String, Operation> stringToEnum = 
	Stream.of(values()).collect(  
		toMap(Object::toString, e -> e)  
	);  
  
private static Optional<Operation> fromString(String symbol) {  
	return Optional.ofNullable(stringToEnum.get(symbol));  
}
```

위 코드를 사용하면, 주어진 문자열을 통해 그에 맞는 Operation을 반환하도록 만들 수 있다.

### 2-5. 열거 타입 내부에서 상수 사용

위에서 본 상수별 메소드 구현에는 각 상수끼리 코드를 공유하기 어렵다는 단점이 있다.

```java
public enum PayrollDay {  
	MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;  
	  
	private static final int MINS_PER_SHIFT = 8 * 60;  
	  
	int pay(int minutesWorked, int payRate) {  
		int basePay = minutesWorked * payRate;  
		int overtimePay;  
		  
		switch (this) {  
			case SATURDAY: case SUNDAY:  
				overtimePay = basePay / 2;  
				break;  
			default:  
				overtimePay = minutesWorked <= MINS_PER_SHIFT ?  
					0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;  
		}  
		return basePay + overtimePay;  
	}  
}
```

간결하긴 하지만 관리 관점에서는 위험하다. 휴가와 같은 새로운 열거 타입에 추가하려면 그 값을 처리하는 case 문을 잊지 않고 쌍으로 넣어줘야 한다. 만약 깜빡할 경우 휴가 기간에 일을 해도 평일과 똑같은 임금을 받게 된다.

이를 개선하는 가장 깔끔한 방법은 새로운 상수를 추가할 때, 잔업수당 '전략'을 선택하도록 하는 것이다. 이에 대한 타입을 private 중첩 열거 타입으로 옮기는 것이다.

```java
public enum PayrollDay {  
	MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),  
	THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),  
	SATURDAY(WEEKEND), SUNDAY(WEEKEND);  
	  
	private final PayType payType;  
	PayrollDay(PayType payType) {this.payType = payType;}  
	  
	enum PayType {  
		WEEKDAY {  
			int overtimePay(int minsWorked, int payRate) {  
				return minsWorked <= MINS_PER_SHIFT ?  
					0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2;  
			}  
		},  
		WEEKEND {  
			int overtimePay(int minsWorked, int payRate) {  
				return minsWorked * payRate / 2;  
			}  
		};  
		  
		abstract int overtimePay(int mins, int payRate);  
		private static final int MINS_PER_SHIFT = 8 * 60;  
		  
		int pay(int minutesWorked, int payRate) {  
			int basePay = minutesWorked * payRate;  
			return basePay + overtimePay(minutesWorked, payRate);  
		}  
	}  
}
```

위처럼 구현하면, PayrollDay 열거 타입의 생성자에서 이 중 적당한 것을 선택해, 잔업수당 계산을 그 전략 열거 타입에 위임해준다. switch 문보다 복잡하지만, 안전하고 유연하다.

## 정리

필요한 원소를 컴파일 타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자.

- 열거 타입은 정수 상수보다 뛰어나다.
    - 더 읽기 쉽고, 안전하고, 강력하다.
- 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자.
