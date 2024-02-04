# ordinal 인덱싱 대신 EnumMap을 사용하라.

아이템35에서 본 것과 같이 `ordinal()` 함수를 사용하면 현재 상수에 대한 순서 값을 반환한다. 함수에 대한 구현을 살펴보면 다음과 같다.

```java
protected Enum(String name, int ordinal) {  
	this.name = name;  
	this.ordinal = ordinal;  
}

private final int ordinal;

public final int ordinal() {  
	return ordinal;  
}
```

Enum 클래스에 대한 인스턴스가 만들어질 때, 각 상수에 대한 순서값을 저장하고, 반환한다. 아이템 35에 나왔던 것처럼 ordinal의 단점은 다음과 같다.

```java
// 한해살이, 여러해살이, 두해살이
enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL;}  

private void savePlanetInfo(Dto dto) {
	Entity entity = Entity.builder()
		.cycle(dto.cycle.ordinal())
		.build();
	repository.save(entity);
}
```

만약 위 코드처럼 DB에 데이터를 저장할 때, `@Enumerated`를 사용한 Enum 자체를 저장하는 것이 아닌, 정수 값으로 저장한다고 가정해보자. 이럴 경우 `ANNUAL`은 위치가 가장 앞이므로 0이 저장될 것이다. 하지만 누군가가 LifeCycle의 순서를 `PERENNIAL, BIENNIAL, ANNUAL`로 변경할 경우 `PERENNIAL`이 0을 반환하게 되게 된다.

## 한 열거 타입 값 매핑

이제 배열에서 `ordinal`을 사용하게 될 경우에 대해 살펴보자.

```java
public class Plant {  
	enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL;}  
	  
	public int getLifeCycle() {  
		return lifeCycle.ordinal();  
	}  
	  
	final String name;  
	final LifeCycle lifeCycle;  
	  
	public Plant(String name, LifeCycle lifeCycle) {  
		this.name = name;  
		this.lifeCycle = lifeCycle;  
	}  
	  
	@Override  
	public String toString() {  
		return name;  
	}  
}
```

```java
// ...  
Set<Plant>[] plantsByLifeCycle = 
	(Set<Plant>[]) new Set[Planet.LifeCycle.values().length];  
  
for (int i = 0; i < plantsByLifeCycle.length; i++) {  
	plantsByLifeCycle[i] = new HashSet<>();  
}  
  
for (Plant p : garden) {  
	plantsByLifeCycle[p.lifeCycle.ordinal()].add(p);  
}  
  
for (int i = 0; i < plantsByLifeCycle.length; i++) {  
	System.out.printf("%s: %s\n", 
		Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);  
}
```

```
ANNUAL: [나팔꽃, 강낭콩]
PERENNIAL: [구일초]
BIENNIAL: [나생이]
```

위 코드는 식물들을 생애주기별로 묶어 총 3개의 집합을 만드는 코드이다. 동작 자체에는 크게 문제가 없으나, 프로그램적인 문제는 여럿 있다.

- 배열 자체가 제네릭과 호환되지 않으니, 비검사 형변환을 수행해야 한다.
    - 깔끔히 컴파일되지 않는다.
- 배열은 각 인덱스의 의미를 모르니, 출력 결과에 직접 레이블을 달아야 한다.
- 정확한 정숫값을 사용한다는 것을 개발자가 직접 보증해야 한다.
    - 정수는 열거 타입과 달리 타입 안전하지 않다.

이런 문제점들은 `EnumMap`을 사용해서 모두 해결할 수 있다.

```java
Map<LifeCycle, Set<Plant>> plantsByLifeCycle =
	new EnumMap<>(LifeCycle.class);  

// 키와 키에 대한 Set 초기화
for (LifeCycle lc : values()) {  
	plantsByLifeCycle.put(lc, new HashSet<>());  
}  

// 정원을 돌면서 식물이 가진 생애주기에 대한 Set에 식물 추가
for (Plant plant : garden) {  
	plantsByLifeCycle.get(plant.lifeCycle).add(plant);  
}  
  
System.out.println(plantsByLifeCycle);
```

각각의 LifeCycle을 키로 갖는 `Map` 형태로 만들었다. 이로 인해 배열의 인덱스에 대해 신경 쓸 필요도 없고, 해당 map 자체를 출력하면 된다. 또한, 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 원천봉쇄된다.

`EnumMap`의 성능이 `ordinal`을 사용한 배열과 비슷한 이유는 `HashMap`과 달리 내부에서 배열을 사용하기 때문이다. 또한, `EnumMap`을 생성할 때, 넣어준 LifeCycle의 클래스 정보 덕분에 런타임 제네릭 타입 정보를 제공할 수 있게 된다.

만약 스트림을 사용할 경우 간단하게 2줄로 위 코드를 대체할 수 있다.

```java
System.out.println(Arrays.stream(garden)
	.collect(Collectors.groupingBy(p -> p.lifeCycle)));
```

`groupingBy`를 사용할 경우 기본적으로 일반적인 `HashMap`을 반환하지만, 추가적인 인자로 `mapFactory`에 사용할 `Map`의 구현체 정보를 넘겨 `EnumMap`을 사용한 공간과 성능적인 이점을 그대로 사용할 수 있다.

```java
System.out.println(Arrays.stream(garden)  
	.collect(Collectors.groupingBy(p -> p.lifeCycle,  
		() -> new EnumMap<>(LifeCycle.class), Collectors.toSet())));
```

만약 정원에 **한해살이**와 **여러해살이** 식물만 있다고 가정하면, 스트림을 사용하지 않은 코드에서는 **두해살이**에 대한 공간도 제공하지만, 스트림을 사용한 버전에서는 **한해살이**와 **여러해살이**에 대한 정보만 갖게 된다.

## 두 열거 타입 값 매핑

아래 코드는 두 가지 상태(Phase)를 전이(Transition)와 매핑하도록 구현한 코드이다. 예를 들어, 액체(LIQUID)에서 고체(SOLID)로의 전이는 응고(FREEZE)가 되고, 액체에서 기체(GAS)로의 전이는 기화(BOIL)가 된다.

```java
public enum Phase {  
	SOLID, LIQUID, GAS;  
	  
	public enum Transition {  
		MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;  
		  
		private static final Transition[][] TRANSITIONS = {  
			{null, MELT, SUBLIME},  
			{FREEZE, null, BOIL},  
			{DEPOSIT, CONDENSE, null}  
		};  
		  
		private static Transition from(Phase from, Phase to) {  
			return TRANSITIONS[from.ordinal()][to.ordinal()];  
		}  
	}  
}
```

위 코드는 앞서 본 정원과 마찬가지로 컴파일러는 ordinal과 배열 인덱스의 관계를 알 수 없다. 즉, Phase나 Transition의 열거 타입을 수정할 때, TRANSITIONS 배열의 표를 함께 수정하지 않거나, 잘못 수정하면 이상하게 동작할 수 있다. 이런 경우에도 `EnumMap`을 사용하는 것이 훨 낫다.

```java
public enum Transition {  
	MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),  
	BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),  
	SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);  
	  
	private final Phase from;  
	private final Phase to;  
	  
	Transition(Phase from, Phase to) {  
		this.from = from;  
		this.to = to;  
	}  
	
	// SOLID={LIQUID=MELT, GAS=SUBLIME}, 
	// LIQUID={SOLID=FREEZE, GAS=BOIL},
	// GAS={SOLID=DEPOSIT, LIQUID=CONDENSE}
	private static final Map<Phase, Map<Phase, Transition>> m =  
		// Transition에 대한 값들을 대상으로 수집
		Stream.of(values()).collect(
			// 그룹 짓기
			Collectors.groupingBy(
				// key를 from으로 지정 예) SOLID
				// Function<? super T, ? extends K> classifier
				t -> t.from,
				// 현재 Map을 Phase를 key로 받는 EnumMap으로 매핑
				// Supplier<M> mapFactory
				() -> new EnumMap<>(Phase.class),
				// 각 from에 대한 value 지정
				// Collector<? super T, A, D> downstream
				Collectors.toMap(
					// key를 to로 지정 예) LIQUID
					// Function<? super T, ? extends K> keyMapper
					t -> t.to,
					// value를 t로 지정 예) MELT
					// Function<? super T, ? extends U> valueMapper
					t -> t,
					// 미사용 / x, y의 타입 : Transition
					// BinaryOperator<U> mergeFunction
					(x, y) -> y,
					// 키 값을 Phase 클래스로 지정하고, EnumMap으로 매핑
					// Supplier<M> mapFactory
					() -> new EnumMap<>(Phase.class))  
		));  
	  
	public static Transition from(Phase from, Phase to) {  
		return m.get(from).get(to);  
	}  
}
```

위 코드는 굉장히 복잡하다. 맵을 초기화하기 위해 수집기(Collectors)를 2번 사용했다. 첫 수집인 `groupingBy`를 통해 from을 기준으로 묶었고, 이후 수집인 `toMap`에서는 to를 전이에 대응시키는 `EnumMap`을 생성한다. 병합 함수인 `(x, y) -> y`는 선언만하고, 실제로는 사용하지 않는다. 단지, `EnumMap`을 얻기 위한 팩토리 인자로  실제로는 쓰이지 않는다.

여기에 새로운 상태인 플라즈마(PLASMA)를 추가해보자. 이 상태와 연결된 전이는 기체에서 플라즈마로 변하는 이온화(IONIZE)와 플라즈마에서 기체로 변하는 탈이온화(DEIONIZE)가 있다.

```java
public enum Phase {  
	SOLID, LIQUID, GAS, PLASMA;  
  
	public enum Transition {  
		MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),  
		BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),  
		SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),  
		IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
		
		// 나머지 코드는 그대로
	}
}
```

이렇게 간단하게 해당하는 상태와 관계만 잘 넣어준다면 잘못 수정할 일도 줄어들고, Map으로 되어있지만 내부 구현 자체는 `EnumMap`을 통한 배열을 사용했기 때문에 낭비되는 공간과, 시간도 거의 없다.

## 정리

- 배열의 인덱스를 얻기 위한 ordinal을 쓰는 것은 좋지 않다.
    - EnumMap을 사용하는 것이 좋다.
- 다차원의 관계는 `EnumMap<..., EnumMap<...>>`을 사용하자.