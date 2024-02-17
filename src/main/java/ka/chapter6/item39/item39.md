# ITEM 39. 명명 패턴보다 애너테이션을 사용하라.

## 명명 패턴

> 명명 패턴이란, 변수나 함수의 이름을 일관된 방식으로 작성하는 패턴을 의미한다.

테스트 프레임워크인 JUnit의 경우 JUnit3까지 테스트 메소드 이름을 `test`로 시작해야 실제 테스트가 적용되는 명명 패턴을 사용했었다.

```java
public class PostServiceTest extends TestCase { 
	public void testPostSave(){ 
		//... 
	}
}
```

만약 위 테스트 메소드의 이름을 `tsetPostSave`으로 잘못 지으면, 해당 메소드를 무시하게 되고, 개발자는 실패하지 않았으니 그냥 넘어가버릴 수 있다.

### 단점

### 1. 올바른 프로그램 요소에서만 사용되리라 보증할 방법이 없다.

JUnit의 명명 패턴으로 인해 메소드 이름이 아닌 클래스 이름을 `TestSafetyMechanisms`로 지으면 왜인지 클래스에 있는 전체 테스트 메소드를 실행해줄 것만 같다. 하지만 실제로 해당 클래스를 JUnit에 던질 경우, 아무런 테스트도 진행하지 않는다. JUnit은 클래스가 아닌 메소드에만 관심이 있기 때문이다.

### 2. 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.

특정 예외를 던져야 성공하는 테스트가 있다고 가정하자. 이 때, 기대하는 예외 타입을 테스트에 매개변수로 전달해야하는데 방법이 마땅히 없으며, 테스트 이름을 `testPostFindThrowNullPointerException`과 같이 지을 경우 보기도 나쁘고, 컴파일러가 메소드 이름에 덧붙인 문자열이 예외를 가리키는지 알 도리가 없다.

## 애너테이션

위에서 본 모든 단점은 애너테이션을 도입해 해결할 수 있다. 실제로 JUnit4부터 애너테이션을 도입하면서, 명명 패턴 방식을 없애버렸다.

```java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface Test {  
  
}
```

위 `@Test` 애노테이션에는 `@Retention`과 `@Target`이라는 애노테이션이 달려있다. 이렇게 애너테이션 선언에 다는 애너테이션을 메타 애너테이션(meta-annotation)이라 한다.

`@Retention(RetentionPolicy.RUNTIME)`은 `@Test`가 런타임에도 유지되어야 한다는 표시이다. 해당 메타 애너테이션을 생략할 경우 테스트 도구는 `@Test`를 인식할 수 없게 된다.

`@Target(ElementType.METHOD)`는 `@Test` 메소드가 반드시 메소드 선언에만 사용돼야 한다고 알리는 메타 애너테이션이다. 따라서 클래스 혹은 필드 등에는 사용할 수 없다.

만약 위에서 생성한 애너테이션을 클래스 단에 달 경우 다음과 같은 컴파일 에러가 발생한다.

```java
@Test  
public class SampleTest {  
	@Test  
	public static void test1() {  }  
}
```

```
'@Test' not applicable to type
```

## 애너테이션을 활용한 테스트 러너

실제 테스트 러너를 만들어 위에서 만든 애너테이션을 활용해보자.

### 함수 실행을 위한 애너테이션

함수 실행을 위한 애너테이션은 위에서 본 `@Test`와 동일하며, 이를 실행하기 위한 러너를 만들어보자.

```java
public class SampleTest {  
	@Test  
	public static void test1() { }  
	  
	public static void test2() {  
		throw new RuntimeException("BOOM");  
	}  
	  
	@Test  
	void test3() { }  
	  
	@Test  
	public static void test4() {  
		throw new RuntimeException("FAIL TEST");  
	}  
}
```

```java
public class RunTests {  
	public static void main(String[] args) throws Exception {  
	
	int tests = 0;  
	int passed = 0;  
	Class<?> testClass = Class.forName(args[0]);  
	  
	for (Method m : testClass.getDeclaredMethods()) {  
		if (m.isAnnotationPresent(Test.class)) {  
			tests++;  
			try {  
				m.invoke(null);  
				passed++;  
			} catch (InvocationTargetException wrappedExc) {  
				Throwable exc = wrappedExc.getCause();  
				System.out.println(m.getName() + "() -> Fail : " + exc);  
			} catch (Exception exc) {  
				System.out.printf("Mistake : %s()%n", m.getName());  
			}  
		}  
	}  
	  
	System.out.printf("성공 : %d, 실패 : %d%n",  
	passed, tests - passed);  
	  
	}  
}
```

```
Mistake : test3()
test4() -> Fail : java.lang.RuntimeException: FAIL TEST
성공 : 1, 실패 : 2
```

결과를 보면 `test3()`의 경우 잘못 사용했다는 것을 알 수 있다. `main`은 `static` 함수지만, 해당 함수는 `static`이 아닌 일반 함수기 때문이다. 이제 위에서 사용한 특정 코드들의 특징을 살펴보자.

- `m.isAnnotationPresent(Test.class)`
    - 메소드에 `Test`라는 어노테이션이 붙어있는지 확인
- `m.invoke(null)`
    - 메소드 매개변수로 `null`을 넣어주고, 해당 메소드를 실행
- `InvocationTargetException wrappedExc`
    - 메소드가 예외를 던질 경우 해당 예외 타입으로 wrapping

### 예외 처리를 위한 애너테이션

이제 특정 예외를 던져야 성공하는 테스트를 만들기 위해 새로운 애너테이션을 생성해보자.

```java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface ExceptionTest {  
	Class<? extends Throwable> value();  
}
```

해당 애너테이션을 사용할 경우 매개변수로 들어오는 타입은 무조건 `Throwable`을 확장한 클래스 객체여야 한다.

```java
public class SampleTest2 {  
	@ExceptionTest(ArithmeticException.class)  
	public static void test1() {  
		int i = 0;  
		i = i / i;  
	}  
	  
	@ExceptionTest(ArithmeticException.class)  
	public static void test2() {  
		int[] a = new int[0];  
		int i = a[1];  
	}  
	  
	@ExceptionTest(ArithmeticException.class)  
	public static void test3() {  
	  
	}  
}
```

1번 테스트는 성공하며, 2번 테스트는 다른 예외가 발생해 실패할 것이며, 3번은 예외가 발생하지 않기 때문에 실패할 것이다. 이제 기존 러너의 예외 처리 부분을 수정해보자.

```java
if (m.isAnnotationPresent(Test.class) ||
	m.isAnnotationPresent(ExceptionTest.class)) {
	// ...
	try {
		Throwable exc = wrappedExc.getCause();  
		Class<? extends Throwable> excType = 
				m.getAnnotation(ExceptionTest.class).value();  
		  
		if (excType.isInstance(exc)) passed++;  
		else System.out.printf("""  
			// ...
			"""
			);
		}
	// ...
}
```

메소드에 붙은 `@ExceptionTest`의 `value()` 함수를 불러와 예외 객체 타입을 불러온 다음, 해당 타입이 `exc`와 동일한지 비교한다. `SampleTest2`를 실행하면 다음과 같은 결과가 나오는 것을 확인할 수 있다.

```
Test Fail : test2()
 - expected : ArithmeticException
 - actual : java.lang.ArrayIndexOutOfBoundsException
 - cause : Index 1 out of bounds for length 0 
성공 : 2, 실패 : 1
```

만약 여러 개의 예외를 받고 싶다면 다음과 같은 방식으로 변경할 수 있다.

### 다중 Exception 처리

한 함수에서 여러 `Exception`을 던질 수 있도록 다중 예외 처리 애너테이션을 만들어보자.

#### 방식 1

첫 번째 방식은 배열 매개변수를 받는 애너테이션이다.

```java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface MultiExceptionTestByArray {  
	Class<? extends Throwable>[] value();  
}
```

```java
@MultiExceptionTestByArray({  
	IndexOutOfBoundsException.class,  
	NullPointerException.class  
})  
public static void doublyBad() {  
	List<String> list = new ArrayList<>();  
	list.addAll(5, null);  
}
```

위처럼 예외를 배열 형식으로 입력 받은 뒤, 처리하는 것이다. 이에 따라 러너도 다음과 같이 수정해줘야 한다.

```java
Class<? extends Throwable>[] excTypes =  
m.getAnnotation(MultiExceptionTestByArray.class).value();  
  
for (Class<? extends Throwable> excType : excTypes) {  
	if (excType.isInstance(exc)) passed++;  
	else System.out.printf("""  
		// ...
		""".trim());  
}
```

#### 방식 2

이번에는 배열을 사용한 방식 대신, `@Repeatable` 메타 애너테이션을 사용하는 방식이다. 이를 사용할 때, 주의해야할 점이 세 가지가 있다.

- `@Repeatable`을 단 애너테이션을 반환하는 컨테이너 애너테이션을 하나 더 정의한다.
    - 해당 컨테이너 애너테이션의 `class` 객체를 매개변수로 전달해주어야 한다.
- 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 메소드를 정의해야 한다.
- 컨테이너 애너테이션 타입에는 적절한 보존 정책(Retention)과 적용 대상(Target)을 명시해야 한다.
    - 명시하지 않을 경우 컴파일되지 않는다.

```java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
@Repeatable(MultiExceptionContainer.class)  
public @interface MultiExceptionTestByContainer {  
	Class<? extends Throwable> value();  
}
```

```java
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
public @interface MultiExceptionContainer {  
	MultiExceptionTestByContainer[] value();  
}
```

위와 같이 여러 개를 반복적으로 사용할 어노테이션을 정의하고, 이를 감싸는 컨테이너 애너테이션을 정의하면 된다.

```java
@MultiExceptionTestByContainer(ClassCastException.class)  
@MultiExceptionTestByContainer(IllegalStateException.class)  
public static void test1() {  
	List<String> list = new ArrayList<>();  
	list.addAll(5, null);  
}
```

위와 같이 동일한 애너테이션을 여러 번 사용이 가능하지만, 실제로 러너에 코드를 작성할 때에는 예외 테스트 애너테이션과 컨테이너 애너테이션을 모두 검사해야 한다.

```java
if (m.isAnnotationPresent(MultiExceptionContainer.class)  
	|| m.isAnnotationPresent(MultiExceptionTestByContainer.class)) {  
	tests++;  
	try {  
		m.invoke(null);  
		passed++;  
	} catch (Throwable wrappedExc) {
	
		Throwable exc = wrappedExc.getCause();  
		int oldPassed = passed;
		MultiExceptionTestByContainer[] excTests =  
		m.getAnnotationsByType(MultiExceptionTestByContainer.class);  
		  
		for (MultiExceptionTestByContainer excTest : excTests) {  
			if (excTest.value().isInstance(exc)) {
				passed++;  
				break;
			}
		}
		if (passed == oldPassed)
			System.out.printf(...);
	}
}
```

만약 실패할 경우 다음과 같은 결과가 나온다.

```
Test Fail : test1()
 - expected : [ClassCastException, IllegalStateException]
 - actual : IndexOutOfBoundsException
 - cause : Index: 5, Size: 0 
성공 : 0, 실패 : 1
```

## 정리

애너테이션을 사용할 경우 코드 양이 늘어지고, 복잡해져 오류가 날 가능성이 커질 수 있다. 하지만, 명명 패턴을 사용하는 것보단, 애너테이션을 사용하면 더 넓은 확장성을 가질 수 있고, 더 유연하게 사용할 수 있는 것을 알 수 있다. 굳이 애너테이션으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 전혀 없는 것이다.
