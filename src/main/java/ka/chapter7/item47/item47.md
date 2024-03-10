# 반환 타입으로는 스트림보다 컬렉션이 낫다.

`Stream`은 `Iterable`을 확장(extends)하지 않았기 때문에 반복(iteration)을 지원하지 않는다. 따라서 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다.

만약 스트림을 `iterable` 하도록 만드려면 다음과 같이 만들 수 있을 것이다.

```java
for (ProcessHandle ph : (Iterable<ProcessHandle>) 
						 ProcessHandle.allProcesses()::iterator) {
	// ..
}
```

위 코드는 강제 형변환까지 하면서까지 반복할 수 있게 만들었다. 이런 코드를 실제 상황에서 쓰기엔 너무 난잡하고, 직관성도 떨어진다. 이런 상황에서 어댑터를 사용하면 조금 더 나아진다.

```java
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
	return stream::iterator;
}
```

```java
for (ProcessHandle ph : iterableOf(ProcessHandle.allProcesses())) {
	// ..
}
```

이럴 경우 어떤 스트림도 for-each 문으로 반복해서 사용할 수 있어진다. 그렇다면 반대로 `Iterable`만 반환하는 코드가 스트림 파이프 라인에서 처리하려면 어떻게 해야할까?

```java
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
	return StreamSupport.stream(iterable.spliterator(), false);
}
```

`Collection` 인터페이스는 `Iterable`의 하위 타입이고, `stream` 메소드도 제공하니 반복과 스트림을 동시에 지원한다. 따라서 원소 시퀀스를 반환하는 공개 API의 반환 타입에는 `Collection`이나 그 하위 타입을 쓰는게 일반적으로 최선이다. 단지 컬렉션을 반환한다는 이유로 덩치 큰 시퀀스를 메모리에 올려서는 안 된다.

## 정리

- 컬렉션을 반환할 수 있다면 가능한 컬렉션을 반환하자.