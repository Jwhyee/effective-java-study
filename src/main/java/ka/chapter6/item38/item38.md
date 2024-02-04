# 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라.

## 타입 안전 열거 패턴

책의 초판에서는 다음과 같은 타입 안전 열거 패턴(typesafe enum pattern)을 소개했었다.

```java
public class TypesafeOperation {  
	private final String type;  
	private TypesafeOperation(String type) {  
		this.type = type;  
	}  
	  
	public String toString() {  
		return type;  
	}  
	  
	public static final TypesafeOperation PLUS = 
		new TypesafeOperation("+");  
	public static final TypesafeOperation MINUS = 
		new TypesafeOperation("-");  
	public static final TypesafeOperation TIMES = 
		new TypesafeOperation("*");  
	public static final TypesafeOperation DIVIDE = 
		new TypesafeOperation("/");  
}
```

이러한 타입 안전 열거 패턴은 클래스를 활용하기 때문에 확장할 수 있다. 하지만 enum 타입은 확장할 수 없다. 만약 enum 타입이 확장 가능하게 되면, 확장한 타입의 원소는 기반 타입의 원소로 취급되지만, 그 반대로는 성립할 수 없다.

## 인터페이스를 이용한 열거 타입 확장

연산 코드(operation code 혹은 opcode)의 경우 API가 제공하는 기본 연산 외에 사용자 확장 연산을 추가할 수 있도록 열어줘야할 때가 있다. 기본적인 아이디어로는 열거 타입이 인터페이스를 구현할 수 있다는 사실을 이용하는 것이다.

```java
public interface Operation {  
	double apply(double a, double b);  
}
```

```java
public enum BasicOperation implements Operation {  
	PLUS("+") {  
		public double apply(double a, double b) { return a + b; }  
	},  
	MINUS("-") {  
		public double apply(double a, double b) { return a - b; }  
	},  
	TIMES("*") {  
		public double apply(double a, double b) { return a * b; }  
	},  
	DIVIDE("/") {  
		public double apply(double a, double b) { return a / b; }  
	};  
	  
	private final String symbol;  
	  
	BasicOperation(String symbol) {  
		this.symbol = symbol;  
	}  
	  
	@Override  
	public String toString() {  
		return symbol;  
	}  
}
```

열거 타입인 `BasicOperation` 자체를 확장할 수는 없지만, 인터페이스인 `Operation`은 확장할 수 있다. 때문에 이 인터페이스를 연산의 타입으로써 활용하면 된다.

```java
public class OperationTest {  
	public static void main(String[] args) {  
		Operation basicOp = BasicOperation.valueOf(args[0]);  
		double result = basicOp.apply(10, 20);  
		System.out.println(result);  
	}  
}
```

만약 지수 연산(EXP)과 나머지 연산(REMAINDER)을 사용해야할 경우 추가적인 `Enum` 클래스를 생성해 `Operation`을 구현해주면 된다.

```java
public enum ExtendedOperation implements Operation {  
	EXP("^") {  
		public double apply(double a, double b) { return Math.pow(a, b); }  
	},  
	REMINDER("%") {  
		public double apply(double a, double b) { return a % b; }  
	};
}
```

```java
public static void main(String[] args) {  
	double x = 5;  
	double y = 10;  
	test(ExtendedOperation.class, x, y);  
}  
  
private static <T extends Enum<T> & Operation> void test(  
	Class<T> opEnumType, double x, double y  
) {  
	for (Operation op : opEnumType.getEnumConstants()) {  
		System.out.printf("%f %s %f = %f\n",  
			x, op, y, op.apply(x, y));  
	}  
}  
```

위 코드에서 사용된 `<T extends Enum<T> & Operation>`은 `Class<T>`의 타입이 `Enum`인 동시에 `Operation`의 하위 타입이어야 한다는 의미이다. 때문에 `opEnumType`은 원소를 순회할 수 있고, 그에 대한 연산을 수행할 수 있는 것이다.

다른 방법으로는 Class 객체 대신 한정적 와일드카드 타입을 넘기는 방법이다.

```java
public static void main(String[] args) {  
	double x = 5;  
	double y = 10;  
	test(Arrays.asList(ExtendedOperation.values()), x, y);  
}  

private static void test(Collection<? extends Operation> opSet,
						 double x, double y) {  
	for (Operation op : opSet) {  
		System.out.printf("%f %s %f = %f\n", 
			x, op, y, op.apply(x, y));  
	}  
}
```

아이템 31에 나오는 것과 같이 `opSet`을 소비하면서 무언가를 만들기 때문에 PECS 공식 중 PE를 적용했다. 위처럼 `asList`를 통해 `ExtendedOperation`의 각 상수들을 넘겨준다.

> 소비자란 어떠한 데이터가 자기 자신(매개 변수)에게 소비되는 것을 의미하고, 생산자는 자기 자신(매개 변수)를 소비해 무언가를 만드는 것을 의미한다.

## 문제점

인터페이스를 통한 확장 흉내는 열거 타입끼리 구현을 상속할 수 없다는 문제점을 갖고 있다. 아무 상태에도 의존하지 않는 경우 디폴트 구현을 사용하면 된다. 하지만 `Operation`의 경우 값으로 연산 기호를 갖고 있기 때문에 이를 저장하고, 찾는 로직이 `BasicOperation`, `ExtendedOperation`에 모두 들어가 있어야 한다.

이런 경우 중복량이 적으니 문제되진 않지만, 공유하는 기능이 점점 많아진다면, 동일한 코드를 모든 클래스에 계속 작성해야 하므로, 힘들어질 것이다. 이런 상황에서는 별도의 도우미 클래스나 정적 도우미 메소드로 분리해 중복 코드를 없애는 것이 좋다.

## 정리

- 열거 타입 자체는 확장할 수 없다.
- 인터페이스와 그를 구현하는 기본 열거 타입을 함께 사용하면 확장의 효과를 낼 수 있다.
    - 클라이언트는 해당 인터페이스를 구현해 자신만의 열거 타입을 만들 수 있다.
    - API가 인터페이스 기반으로 작성되었다면, 모든 곳을 새로 확장한 열거 타입의 인스턴스로 대체해 사용할 수 있다.