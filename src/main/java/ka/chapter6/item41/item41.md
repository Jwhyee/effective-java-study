# ITEM 41. 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라.

## 마커 인터페이스

> 마커 인터페이스란, 아무 메소드도 담고 있지 않고, 단지 자신을 구현하는 클래스가 특정 속성을 가짐을 표시해주는 인터페이스이다.

`Serializable` 인터페이스가 대표적인 마커 인터페이스이다.

```java
public interface Serializable {  }
```

위 인터페이스에는 아무런 함수도 없지만, 이를 구현한 클래스에서는 `ObjectOutputStream`을 통해 `write`를 할 수 있다. 즉, 직렬화(serialization)할 수 있다.

## 마커 인터페이스의 장점

### 1. 자신을 구현한 클래스의 인스턴스들을 구분하는 타입으로 쓸 수 있다.

인터페이스의 경우 어엿한 타입이기 때문에 오류를 컴파일 타임에 잡을 수 있다. 하지만 마커 애너테이션의 경우, 인스턴스를 구분하는 타입으로써 활용할 수 없으며, 에러를 런타임에서야 확인할 수 있다.

자바의 직렬화는 `Serializable` 마커 인터페이스를 보고 그 대상이 직렬화할 수 있는 타입인지를 확인한다.

#### 장점을 살리지 못한 예시

`ObjectOutputStream.writeObject` 메소드를 확인해보자.

```java
public final void writeObject(Object obj) throws IOException {  
	// ...
}
```

해당 함수 내부를 계속 타고 들어가면 다음과 같은 메소드가 있는데, 해당 부분에서 `Serializable`을 구현한 객체인지 확인하게 된다.

```java
private void writeObject0(Object obj, boolean unshared)  
throws IOException  
{  
	boolean oldMode = bout.setBlockDataMode(false);  
	depth++;  
	try {  
		// ...
		if ((obj = subs.lookup(obj)) == null) {  
			// ... 
		} else if (!unshared && (h = handles.lookup(obj)) != -1) {  
			// ...
		} else if (obj instanceof Class) {  
			// ...
		} else if (obj instanceof ObjectStreamClass) {  
			// ...
		}  
		// ...
	}
```

`writeObject` 함수는 당연히 인수로 받은 객체가 `Serializable`을 구현했을 거라고 가정한다. 하지만, 실제로 이를 검사하는 코드는 더 깊이 있으며, `ObjectStreamClass`의 인스턴스인지 확인하는 과정에서 컴파일 타임이 아닌 런타임에 잡게 된다. 이는 마커 인터페이스의 이점을 살리지 못한 것이다.

### 2. 적용 대상을 더 정밀하게 지정할 수 있다.

애너테이션의 경우 적용 대상을 `@Target`이라는 메타 애너테이션을 통해 지정할 수 있다. 하지만 `ElementType.Type`으로 지정할 경우 모든 타입(클래스, 인터페이스, 열거 타입, 애너테이션)에 달 수 있게 되는데, 부착할 수 있는 타입을 더 세밀하게 제한하지 못하게 된다.

그에 비해 마커 인터페이스는 타입에 대해 자유롭게 제한이 가능하다. 가령 특정 클래스에만 특정 인터페이스를 구현하고 싶을 경우 단순히 해당 마커 인터페이스를 구현하기만 하면 된다.

## 마커 애너테이션의 장점

그렇다면 마커 애너 테이션의 장점은 무엇일까? 바로 거대한 애너테이션 시스템의 지원을 받는 다는 것이다. Spring 프레임워크에 있는 다양한 애너테이션을 봐보자.

```java
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
@Indexed  
public @interface Component {  
	String value() default "";
}
```

```java
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
@Component  
public @interface Service {  
	@AliasFor(annotation = Component.class)  
	String value() default "";  
}
```

위처럼 컴포넌트 애너테이션과 동일한 속성이라고 취급하는 정의만 들어있다. 하지만 해당 애너테이션을 사용할 경우 컴포넌트 스캔에 의해 스프링 빈에 등록되게 된다. 이는 `Serializable`과 같이 특정 어노테이션을 붙일 경우 직렬화할 수 있도록 하는 것과 유사하다.

이렇듯 마커 애너테이션은 프레임워크에서 일관성을 지키는데 유용하게 사용된다.

## 정리

클래스와 인터페이스 외의 프로그램 요소(모듈, 패키지, 필드, 지역 변수 등)에 마킹할 때는 애너테이션을 사용할 수 밖에 없다. 만약 마커를 클래스나 인터페이스에만 적용해야 한다면, "이 마킹이 된 객체를 매개 변수로서 받는 메소드를 작성할 일이 있는가?"를 먼저 고민해보고, 이에 대한 답이 "그렇다"일 경우 마커 인터페이스를 사용하도록 하자.

마커 인터페이스를 사용할 경우 컴파일 타임에 오류를 잡아낼 수 있다는 강력한 장점이 있다. 반대로 매개 변수로 활용할 일이 없다면 마커 애너테이션이 나을 수 있다.

- 새로 추가하는 메소드 없이 단지 타입 정의가 목적이라면 마커 인터페이스를 사용하자.
- 클래스, 인터페이스 외 요소에 마킹해야할 경우 애너테이션을 사용하자.
- 적용 대상이 `ElementType.Type`인 마커 애너테이션이 있다면, 정말 애너테이션으로 구현하는게 옳은지 생각해보자.
