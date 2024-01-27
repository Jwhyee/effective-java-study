# ordinl 메소드 대신 인스턴스 필드를 사용하라.

대부분의 열거 타입 상수는 자연스럽게 하나의 정숫값에 대응된다. 모든 열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지를 반환하는 ordinal이라는 메소드를 제공한다.

```java
public enum Ensemble {  
	SOLO, DUET, TRIO, QUATET, QUINTET,  
	SEXTET, SEPTET, OCTET, NONET, DECTET;  
	  
	public int numberOfMusicians() {return ordinal() + 1;}  
}
```

위 코드는 잘 동작하긴 하지만, 상수 선언 순서를 바꾸는 순간 numberOfMusicians가 오작동하고, 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다. 즉, 8중주(octet) 상수가 이미 있으니 똑같이 8명이 연주하는 복4중주(double quartet)는 추가할 수 없는 것이다.

이를 해결할 방법으로는 열거 타입 상수에 ordinal 메소드에 의존하지 않고, 인스턴스 필드에 저장하는 것이다.

```java
public enum Ensemble {  
	SOLO(1), DUET(2), TRIO(3), QUATET(4), QUINTET(5),  
	SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUATET(8),  
	NONET(9), DECTET(10), TRIPLE_QUATET(12);  
	  
	private final int numberOfMusicians;  
	Ensemble(int size) {this.numberOfMusicians = size;}  
	  
	public int numberOfMusicians() {return numberOfMusicians;}  
}
```

ordinal API 문서를 살펴보면 다음과 같은 글이 있다.

> Most programmers will have no use for this method.<br>
> - 대부분의 프로그래머들은 이 메서드를 사용하지 않을 것입니다.

EnumSet, EnumMap 같이 열거 타입 기반의 범용 자료구조에 쓸 목적이 아니라면 절대 사용하지 말자.