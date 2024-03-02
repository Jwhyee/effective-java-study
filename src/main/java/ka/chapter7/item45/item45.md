# 스트림은 주의해서 사용하라.

스트림은 다량의 데이터 처리 작업을 돕고자 자바 8에 추가되었다. 이 API가 제공하는 추상 개념 중 핵심은 두 가지가 있다.

## 1. 스트림 파이프라인은 연산 단계를 표현하는 개념

스트림 파이프라인은 소스 스트림에서 시작해 종단 연산(terminal operation)으로 끝나며, 그 사이에 하나 이상의 중간 연산(intermediate operation)이 있을 수 있다.

각 중간 연산은 스트림을 어떠한 방식으로 변환(transform) 한다. 중간 연산들은 모두 한 스트림을 다른 스트림으로 변환하는데, 변환된 스트림의 원소 타입은 변환 전 스트림의 원소 타입과 같을 수도 있고 다를 수도 있다.

종단 연산은 마지막 중간 연산이 내놓은 스트림에 최수의 연산을 가한다. 원소를 정렬해 컬렉션에 담거나, 특정 원소 하나를 선택하거나, 모든 원소를 출력하는 식이다.

### 지연 평가

스트림 파이프라인은 지연 평가(lazy evaluation)된다. 즉, 종단 연산이 호출될 때 이뤄지며, 종단 연산이 없는 스트림 파이프라인은 아무 일도 하지 않는 명령어인 `no-op(No Operation)`과 같다.

```java
List<String> strList = List.of("hello", "hell", "hel", "he", "h")  
Stream<String> hell = strList.stream()  
	.filter(it -> it.startsWith("hell"));
```

### 다재다능

스트림 API는 어떠한 계산이라도 해낼 수 있다. 스트림을 제대로 사용하면 프로그램이 짧고 깔끔해지지만, 잘못 사용하면 읽기 어렵고 유지보수도 힘들어진다.

아래 코드는 아나 그램을 구현한 코드이다. 아나 그램이란, 철자를 구성하는 알파벳이 같고 순서만 다른 단어를 의미하며, 아래 코드에서는 사용자가 명시한 사전 파일에서 각 단어를 읽어 맵을 저장하는 형태로 구현했다.

```java
public class AnagramTest {  
	public static void main(String[] args) {  
		File dictionary = new File(args[0]);  
		int minGroupSize = Integer.parseInt(args[1]);  
		  
		Map<String, Set<String>> groups = new HashMap<>();  
		try (Scanner sc = new Scanner(dictionary)) {  
			while (sc.hasNext()) {  
				String word = sc.next();  
				groups.computeIfAbsent(alphabetize(word),  
					(unused) -> new TreeSet<>()).add(word);  
			}  
		} catch (FileNotFoundException e) {  
			throw new RuntimeException(e);  
		}  
		  
		for (Set<String> group : groups.values()) {  
			if (group.size() >= minGroupSize) {  
				System.out.println(group.size() + " : " + group);  
			}  
		}  
	}  
	  
	public static String alphabetize(String s) {  
		char[] a = s.toCharArray();  
		Arrays.sort(a);  
		return new String(a);  
	}  
}
```

`computeIfAbsent`를 사용하면 맵 안에 키가 있는지 찾은 다음, 있으면 단순히 그 키에 매핑된 값을 반환한다. 키가 없을 경우 건네진 함수 객체를 키에 적용하여 값을 계산한 다음, 그 키와 값을 매핑해놓고 계산된 값을 반환한다. 만약 위 코드를 스트림만 사용해서 구현하면 어떨까?

```java
public class AnagramTest {  
	public static void main(String[] args) {  
		Path dictionary = Paths.get(args[0]);  
		int minGroupSize = Integer.parseInt(args[1]);  
		  
		try (Stream<String> words = Files.lines(dictionary)) {  
			words.collect(  
				groupingBy(word -> word.chars().sorted()  
					.collect(StringBuilder::new,  
						(sb, c) -> sb.append((char) c),  
						StringBuilder::append).toString()))
			.values().stream()  
			.filter(group -> group.size() >= minGroupSize)  
			.map(group -> group.size() + " : " + group)  
			.forEach(System.out::println);  
		} catch (IOException e) {  
			throw new RuntimeException(e);  
		}  
	}  
}
```

코드는 분명 짧지만, 읽기도 어렵고, 스트림에 익숙하지 않은 개발자가 본다면 더욱 어렵게 느껴질 것이다. 이렇게 스트림을 과용하면 유지보수하기 어려워지는 단점이 있다.

```java
public class AnagramTest {  
	public static void main(String[] args) {  
		Path dictionary = Paths.get(args[0]);  
		int minGroupSize = Integer.parseInt(args[1]);  
		  
		try (Stream<String> words = Files.lines(dictionary)) {  
			words.collect(groupingBy(word -> alphabetize(word)))  
			.values().stream()  
			.filter(group ->
				group.size() >= minGroupSize)  
			.forEach(group -> 
				System.out.println(group.size() + " : " + group));
		} catch (IOException e) {  
			throw new RuntimeException(e);  
		}  
	}  
	  
	public static String alphabetize(String s) {  
		char[] a = s.toCharArray();  
		Arrays.sort(a);  
		return new String(a);  
	}  
}
```

하지만 위처럼 적절히 스트림을 사용할 경우 깔끔하고, 명료해진다.

> 람다에서는 타입 이름을 자주 생략하기 때문에 매개변수 이름을 잘 지어야 스트림 파이프 라인의 가독성이 유지된다. 또한, 세부 구현의 경우 스트림에서 구현하는 것이 아닌 함수로 빼면 전체적인 가독성을 높일 수 있다.

### 리팩터링

기존 반복문 코드를 모두 스트림으로 바꾸는 것은 항상 좋지만은 않다. 스트림으로 바꾸는게 가능할지라도 코드 가독성과 유지보수 측면에서 손해를 볼 수 있다. 기존 코드는 스트림을 사용하도록 리팩터링하되, 새 코드가 더 나아보일 때만 반영하는 것이 좋다.

함수 객체로는 할 수 없지만 코드 블록으로 할 수 있는일들이 있다.

- 코드 블록에서는 범위 안의 지역 변수를 읽고 수정할 수 있다.
    - 람다에서는 `final`인 변수만 읽을 수 있고, 지역 변수를 수정하는 것은 불가능하다.
- 코드 블록에서는 `return`문을 사용해 빠져나가거나, `break`, `continue`로 제어할 수 있다.
    - 람다에서는 아예 사용이 불가능하다.

계산 로직에서 위와 같은 기능이 필요하다면 스트림과 맞지 않다. 반대로 아래 일들은 스트림이 안성맞춤이다.

- 원소들의 시퀀스를 일관되게 변환한다.
- 원소들의 시퀀스를 필터링한다.
- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다.(사칙연산 등)
- 원소들의 시퀀스를 컬렉션에 모은다.
- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

## 2. 데이터 원소의 유한 혹은 무한 시퀀스

스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이다. 때문에 데이터의 각 단계에서의 값들에 동시에 접근하기는 어렵다.

메르센 소수를 예로 확인해보자. 메르센 수는 `2^p - 1` 형태의 수인데 p가 소수이면 해당 메르센 수도 소수일 수 있는데, 이 때의 수를 메르센 소수라고 한다. 다음 코드는 (무한) 스트림을 반환하는 정적 임포트 메소드이다.

```java
static Stream<BigInteger> primes() {  
	return Stream.iterate(BigInteger.TWO, BigInteger::nextProbablePrime);  
}
```

이 코드는 메소드 이름을 복수 명사로 지정해 스트림의 원소들이 소수임을 말해주어 가독성이 좋다. 이제 20개의 메스센 소수를 출력해보자.

```java
primes().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))  
	.filter(mersenne -> mersenne.isProbablePrime(50))  
	.limit(20)  
	.forEach(System.out::println);
```

이 값은 초기 스트림에만 나타나기 때문에 출력하는 종단 연산에서는 접근할 수 없다. 하지만 첫 번째 중간 연산에서 수행한 매핑을 거꾸로 수행해서, 메르센 수의 지수를 쉽게 계산해낼 수 있다.

```java
.forEach(mp -> System.out.println(mp.bitLength() + " : " + mp));
```

## 정리

- 스트림을 사용해야 멋지게 처리할 수 있는 일이 있고, 반복 방식이 더 알맞은 일도 있다.
- 수많은 작업이 이 둘을 조합했을 때 가장 멋지게 일어난다.
- 스트림과 반복 중 어느 쪽이 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 택하자.