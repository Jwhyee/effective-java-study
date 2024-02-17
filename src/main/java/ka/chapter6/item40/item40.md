# ITEM 40. @Override 애너테이션을 일관되게 사용하라.


자바가 기본으로 제공하는 애너테이션 중 가장 많이 사용하는 것은 아무래도 `@Override`일 것이다. 이는 메소드 선언에만 달 수 있으며, 상위 타입의 메소드를 재정의했음을 의미한다. 이 애너테이션은 여러 가지 악명 높은 버그를 예방할 수 있는데, 다음 코드를 통해 살펴보자.

```java
public class Bigram {  
	private final char first;  
	private final char second;  
	  
	public Bigram(char first, char second) {  
		this.first = first;  
		this.second = second;  
	}  
	  
	public int hashCode() {  
		return 31 * first + second;  
	}  
	  
	public boolean equals(Bigram b) {  
		return b.first == first && b.second == second;  
	}  
	  
	public static void main(String[] args) {  
		Set<Bigram> s = new HashSet<>();  
		for (int i = 0; i < 10; i++) {  
			for (char ch = 'a'; ch <= 'z'; ch++) {  
				s.add(new Bigram(ch, ch));  
			}  
		}  
		System.out.println(s.size());  
	}  
}
```

위 코드는 소문자 2개로 구성된 바이그램; 영어 알파벳 2개로 구성된 문자열 26개를 10번 반복해 집합에 추가한 다음, 그 집합의 크기를 구한다.

위 코드를 실행해보면 260이 나오게 된다. 알파벳의 개수는 26개인데 어떻게 260이 나오는 것일까? 그 이유는 바로 `equals`의 문제이다. 위 코드에 보이는 `equlas`는 `@Override`를 붙이지 않아, 재정의가 아닌 다중 정의(Overloading)을 한 것이다.

때문에 `Hash`를 사용하는 `Set`에서는 해당 객체에 대한 비교를 하는데, 위에서 정의한 `equals`를 사용하지 않아 260개의 객체가 들어가게 된 것이다. 이를 `@Override` 어노테이션을 사용해 다시 재정의해보자.

```java
@Override  
public boolean equals(Bigram b) {  
	return b.first == first && b.second == second;  
}
```

이렇게 정의하더라도, 컴파일에 실패하게 된다. 그 이유는, `Object.class`에 정의된 `equals`의 매개 변수는 `Object o`를 받기 때문이다. 이제 이를 정해진 컨벤션에 맞게 수정해보자.

```java
@Override  
public boolean equals(Object o) {  
	if(!(o instanceof Bigram b)) return false;  
	return b.first == first && b.second == second;  
}
```

코틀린의 스마트 캐스팅과 비슷하게, Java 14에서 제공하는 [Pattern Matching](https://blogs.oracle.com/javamagazine/post/pattern-matching-for-instanceof-in-java-14) 사용하면 객체 캐스팅을 사용하지 않고도 쉽게 객체를 변환할 수 있다.

위처럼 상위 클래스의 메소드를 재정의할 경우 모든 메소드에 `@Override` 애너테이션을 다는 것이 좋다. 만약 구체 클래스에서 상위 클래스의 추상 메소드를 재정의할 때에는 굳이 `@Override`를 달지 않아도 된다.

IDE를 사용하면 `@Override`를 기본적으로 붙여주며, 만약 해당 애너테이션을 사용하지 않고, 같은 시그니처를 가진 메소드가 있는데 `@Override`를 사용하지 않으면, 경고를 띄워주게 된다.

## 정리

- 재정의한 모든 메소드에 `@Override`를 의식적으로 달면 실수를 방지할 수 있다.
- 구체 클래스에서 상위 클래스의 추상 메소드를 재정의한 경우 애너테이션을 달지 않아도 된다.
    - 달아도 해로울 것 없다.
