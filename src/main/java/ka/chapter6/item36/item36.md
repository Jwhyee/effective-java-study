# 비트 필드 대신 EnumSet을 사용하라.


## 비트 필드

열거한 값들이 주로 단독이 아닌 집합으로 사용될 경우, 상수에 서로 다른 2의 거듭 제곱 값을 할당한 정수 열거 패턴을 사용해 왔다.

```java
public class Text {  
	public static final int STYLE_BOLD = 1 << 0; // 1  
	public static final int STYLE_ITALIC = 1 << 1; // 2  
	public static final int STYLE_UNDERLINE = 1 << 2; // 4  
	public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8  
	  
	public void applyStyles(int styles) {  
		// ...  
	}  
}
```

다음과 같은 식으로 비트별 OR를 사용해 여러 상수를 하나의 집합으로 모을 수 있다. 이렇게 만들어진 집합을 비트 필드 (bit field)라 부른다. 이를 사용하면 비트별 연산을 사용해 합집합과 교집합 같은 집합 연산을 효율적으로 수행할 수 있지만, 비트 필드는 정수 열거 상수 패턴의 단점을 그대로 지닌다.

또한, 추가적으로 다음과 같은 단점들이 존재한다.

- 값이 그대로 출력되면 단순한 정수 열거 상수를 출력할 때보다 해석하기가 훨씬 어렵다.
- 비트 필드 하나에 녹아있는 모든 원소를 순회하는 것도 어렵다.
- 최대 몇 비트가 필요한지 API 작성시 미리 예측하여 타입(int, long)을 선택해야 한다.

## EnumSet

비트 필드를 사용하는 대신 더 나은 대안으로 EnumSet 클래스를 사용할 수 있다. 이는 Set 인터페이스를 완벽히 구현하며, 타입 안전하고, 다른 Set 구현체와도 함께 사용할 수 있다.

```java
public class Text {  
	public enum Style {BOLD, ITALIC, UNDERLINE, STRIKETHROUGH}  
	  
	public void applyStyles(Set<Style> styles) {  
		// ...  
	}  
}
```

```java
public class TextTest {  
	@Test  
	void test() {  
		Text text = new Text();  
		text.applyStyles(EnumSet.of(BOLD, ITALIC));  
	}  
}
```

applyStyles 메소드가 `EnumSet<Style>`이 아닌 `Set<Style>`을 받은 이유는 모든 클라이언트가 EnumSet을 건내지 않을 수 있기 때문에 Set 인터페이스로 받는게 좋은 습관이다.

## 정리

열거할 수 있는 타입을 한데 모아 집합 형태로 사용한다고 해도, 비트 필드를 사용할 이유는 없다. EnumSet 클래스가 비트 필드 수준의 명료함과 성능을 제공하고, 열거 타입의 장점까지 선사하기 때문이다.