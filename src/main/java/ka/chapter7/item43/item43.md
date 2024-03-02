# 람다보다는 메소드 참조를 사용하라.


람다가 익명 클래스보다 나은 점 중 가장 큰 특징은 간결함이다. 하지만 람다를 사용하는 것보다 메소드 참조(method reference)를 사용하면 더 간결하게 사용이 가능하다.

```java
Map<String, Integer> map = new HashMap<>();  
map.merge("KEY", 1, (cnt, incr) -> cnt + incr);
```

위 함수는 주어진 키가 맵 안에 아직 없을 경우 KEY-VALUE를 그대로 저장하고, 키가 존재한다면 기존 키 값에 함수 값을 적용한다. 여기서 사용한 `cnt`, `incr`은 크게 하는일도 없이 공간만 차지한다. 이런 상황에서 메소드의 참조를 전달하면 동일한 결과를 가져오면서 코드를 줄일 수 있다.

```java
map.merge("KEY", 1, Integer::sum);
```

대부분 람다를 사용할 때, IDE의 추천을 다르면 메소드 참조로 대체하도록 권고한다. 보통은 권고사항대로 변경하는 것이 좋지만, 항상 좋지만은 않다. 예를들어, 클래스 이름이 길거나, 메소드 이름이 람다 식을 사용했을 때보다 길 경우 그 때는 람다를 사용하는 것이 좋다.

```java
service.excute(GoshThisClassNameIsHumongous::action);
service.excute( () -> action() );
```

이런 경우에는 메소드 참조 쪽이 더 짧지도, 명확하지도 않기 때문에 람다를 사용하는 편이 훨씬 낫다.

메소드 참조에는 다섯가지가 존재한다.

| 참조 유형 | 예 | 같은 기능을 하는 람다 |
| ---- | ---- | ---- |
| 정적 | `Integer::parseInt` | `str -> Integer.parseInt(str)` |
| 한정적(인스턴스) | `Instant.now()::isAfter` | `Instant then = Instant.now();`<br>`t -> then.isAfter(t)` |
| 비한정적(인스턴스) | `String::toLowerCase` | `str -> str.toLowerCase()` |
| 클래스 생성자 | `TreeMap<K, V>::new` | `() -> new TreeMap<K, V>()` |
| 배열 생성자 | `int[]::new` | `len -> new int[len]` |

## 정리

- 메소드 참조는 람다의 간단 명료한 대안이 될 수 있다.
- 메소드 참조 쪽이 짧고 명확할 경우 참조를, 그렇지 않을 경우에는 람다를 사용하자.