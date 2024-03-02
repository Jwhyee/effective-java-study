# 스트림은 주의해서 사용하라.

## 함수형 인터페이스

`java.util.function` 패키지에는 총 43개의 인터페이스가 담겨있는데 이 중 6개만 기억하면 나머지는 충분히 유추할 수 있다.

| 인터페이스               | 함수 시그니처               | 예                     |
| ------------------- | --------------------- | --------------------- |
| `UnaryOperator<T>`  | `T apply(T t)`        | `String::toLowerCase` |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | `BigInteger::add`     |
| `Predicate<T>`      | `boolean test(T t)`   | `Collection::isEmpty` |
| `Function<T, R>`    | `R apply(T t)`        | `Arrays::asList`      |
| `Supplier<T>`       | `T get()`             | `Instant::now`        |
| `Consumer<T>`       | `void accept(T t)`    | `System.out::println` |

**Operator** :
- 반환값과 인수의 타입이 같은 함수
- 인수가 1개인 `Unary`
- 인수가 2개인 `Binary`

```java
strList.replaceAll(it -> it + "1");
```

```java
default void replaceAll(UnaryOperator<E> operator) {  
	Objects.requireNonNull(operator);  
	final ListIterator<E> li = this.listIterator();  
	while (li.hasNext()) {  
		li.set(operator.apply(li.next()));  
	}  
}
```

**Predicate**
- 인수 하나를 받아 boolean을 반환

```java
List<String> filteredList = strList.stream()  
	.filter(it` -> it.startsWith("A"))  
	.toList();`
```

```java
Stream<T> filter(Predicate<? super T> predicate);
```

**Function**
- 인수와 반환 타입이 다른 함수

```java
List<Integer> intList = strList.stream()  
	.map(Integer::parseInt)  
	.toList();
```

```java
<R> Stream<R> map(Function<? super T, ? extends R> mapper);
```

**Supplier**
- 인수를 받지 않고, 값을 반환(혹은 제공)하는 함수

**Consumer**
- 인수를 하나 받고 반환값은 없는(인수를 소비하는) 함수

```java
strList.stream().forEach(System.out::println);
```

```java
void forEach(Consumer<? super T> action);
```

### 특징

표준 함수형 인터페이스는 대부분 기본 타입만 지원한다. 즉, 기본 함수형 인터페이스에 박싱된 기본 타입을 넣어 사용하는 것은 안 좋다. 동작은 하겠지만, 박싱과 언박싱을 하는 과정에서 성능이 처참히 느려질 수 있다.

`Comparator`는 구조적으로 `ToIntBiFunction<T, U>`와 동일한데, `Comparator`로 독자적인 인터페이스로 살아남은 이유는 다음과 같다.

1. 자주 쓰이며, 이름 자체가 용도를 명확히 설명해준다.
2. 반드시 따라야하는 규약이 있다.
3. 유용한 디폴트 메소드를 제공할 수 있다.

이 중 하나 이상을 만족한다면 전용 함수형 인터페이스를 구현해야 하는건 아닌지 진중히 고민해야 한다.

직접 만든 함수형 인터페이스에 `@FunctionalInterface` 어노테이션을 붙이는 것은 `@Override`를 사용하는 이유와 비슷하다.

1. 해당 클래스의 코드나 설명 문서를 읽을 이에게 람다용으로 설계된 것임을 알린다.
2. 해당 인터페이스가 추상 메소드를 오직 하나만 가지고 있어야 컴파일되게 해준다.
3. 그 결과 유지보수 과정에서 누군가 실수로 메소드를 추가하지 못하게 막아준다.


## 변형

### SrcToResult

입력과 반환 타입이 다를 경우 `Function`을 사용하는데, 입력과 결과 타입이 모두 기본 타입일 경우 접두어로 `SrcToResult` 패턴을 사용한다. 예를 들어 `long`을 `int`로 반환하면 `LongToIntFunction`이 되는 것이다.

### ToResult

`SrcToResult`와 달리 입력이 객체 참조이고, 결과가 `int`, `long`, `double`인 변형일 경우 입력을 매개변수화하고, 접두어를 `ToResult`를 사용한다. 예를 들어 `int[]`를 `long`으로 반환하면 `ToLongFunction<int[]>`가 되는 것이다.

### Bi

기본 함수형 인터페이스 중 3개에는 인수를 2개씩 받는 변형이 있으며, `BiFunction`에는 기본 타입을 반환하는 3개의 변형이 존재한다. `BiConsumer`의 경우에도 객체 참조와 기본 타입하나 즉, 인수를 2개 받는 3개의 변형이 존재한다.

- `BiPredicate<T, U>`
- `BiFunction<T, U, R>`
    - `ToIntBiFunction<T, U>`
    - `ToLongBiFunction<T, U>`
    - `ToDoubleBiFunction<T, U>`
- `BiConsumer<T, U>`
    - `ObjDoubleConsumer<T>`
    - `ObjIntConsumer<T>`
    - `ObjLongConsumer<T>`
