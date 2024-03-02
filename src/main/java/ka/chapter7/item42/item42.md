# 익명 클래스보다는 람다를 사용하라.

## 익명 클래스

특정 함수를 호출하는 과정에서, 필요한 인자 객체를 넣어줄 때, 이미 만들어진 클래스 객체(혹은 인터페이스 구현 클래스 객체)가 아닌, 함수 인자에서 바로 특정 객체를 생성해 보내주는 것을 익명 클래스라 부른다.

```java
List<String> words = List.of("Hello", "World");  
Collections.sort(words, new Comparator<String>() {  
	@Override  
	public int compare(String o1, String o2) {  
		return Integer.compare(o1.length(), o2.length());  
	}  
});
```

이런 익명 클래스 방식은 코드가 너무 길어 보기 불편하다. 하지만 람다를 사용하면 더욱 간결하고 쉽게 표현할 수 있다.

## 람다

람다는 함수나 익명 클래스와 개념은 비슷하다. 하지만 그에 비해 훨씬 간결하다.

```java
Collections.sort(words,  
	(s1, s2) -> Integer.compare(s1.length(), s2.length()));
```

앞서 본 익명 클래스 방식에 비해 코드량이 훨씬 줄었고, 보기도 쉬워졌다. 또한, 우리는 람다에 리스트가 `String`이라는 정보를 주지 않았음에도 이를 인식해 타입 추론을 해준다. 나아가 다음 방식을 사용할 경우 더욱 간결하게 코드를 작성할 수 있다.

```java
Collections.sort(words, comparingInt(String::length));
```

```java
words.sort(comparingInt(String::length));
```

람다를 언어 차원에서 지원하면서 기존에는 적합하지 않았던 곳에서도 함수 객체를 실용적으로 사용할 수 있게 되었다.

또한, 람다를 활용하면, 아이템 34에서 만들었던 `Operation` 열거 타입의 메소드 구현을 더욱 간결하게 줄일 수 있다.

```java
PLUS("+") {public double apply(double x, double y) {return x + y;}},  
MINUS("-") {public double apply(double x, double y) {return x - y;}},  
TIMES("*") {public double apply(double x, double y) {return x * y;}},  
DIVIDE("/") {public double apply(double x, double y) {return x / y;}}
```

```java
PLUS("+", (x, y) -> x + y),  
MINUS("-", (x, y) -> x - y),  
TIMES("*", (x, y) -> x * y),  
DIVIDE("/", (x, y) -> x / y)
```

이렇게 람다를 사용하면 사실 `PLUS`, `MINUS` 등과 같은 상수별 클래스 몸체는 더 이상 필요 없는 것처럼 느껴진다. 하지만 메소드나 클래스와 달리 람다는 이름이 없고, 문서화를 할 수 없다. 위 코드는 코드 자체로 동작이 명확히 설명되어 그럴 수 있지만, 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 사용하지 말아야 한다.

람다는 한 줄일 때가 가장 읽기 좋고, 길거나 읽기 어렵다면 람다를 사용하지 않는 쪽으로 리팩터링하는 것이 좋다. 또한, 람다로 대체할 수 없는 코드도 있다. 람다는 함수형 인터페이스에서만 사용되기 때문에 추상 클래스의 인스턴스를 만들 수 없어 익명 클래스를 사용해야 한다.

## 정리

익명 클래스 또는 람다를 직렬화하는 일은 극히 삼가해야 한다.

- 자바 8부터 람다가 도입되었다.
- 익명 클래스는 함수형 인터페이스가 아닌 타입의 인스턴스를 만들 때만 사용하자.
- 람다는 작은 함수 객체를 아주 쉽게 표현할 수 있다.
