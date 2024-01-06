# 타입 안전 이종 컨테이너를 고려하라.

제네릭은 Set<E>, Map<K, V> 등의 컬렉션과 ThreadLocal<T>, AtomicReference<T> 등의 단일 원소 컨테이너에서도 흔히 쓰인다.
이런 모든 쓰임에서 매개변수화되는 대상은 원소가 아닌 컨테이너 자신이다.

즉, 한 컨테이너가 매개변수화할 수 있는 타입의 수가 제한된다.
예를 들어 Set<E>는 단 하나의 타입 매개변수만 있으면 되고, Map<K, V>는 키와 값의 타입 2개만 필요한 것이다.

## 타입 안전 이종 컨테이너 패턴

아래 클래스는 타입별로 즐겨 찾는 인스턴스를 저장하고, 검색할 수 있는 `Favorites` 클래스이다.

```java
public class Favorites {

    private Map<Class<?>, Object> db = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        db.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(db.get(type));
    }
}

public class FavoritesTest {

    private final Favorites f = new Favorites();

    @Test
    @DisplayName("성공 테스트")
    void successTest() {
        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebebe);
        f.putFavorite(Class.class, Favorites.class);

        String favoriteString = f.getFavorite(String.class);
        int favoriteInteger = f.getFavorite(Integer.class);
        Class<?> favoriteClass = f.getFavorite(Class.class);

        System.out.printf("%s %x %s\n", favoriteString, favoriteInteger, favoriteClass.getSimpleName());
    }
}

```

위처럼 `Class<T>`로 감싸서, 값을 받을 경우 타입 안전하게 이용할 수 있다.
이러한 설계 방식을 바로 **타입 안전 이종 컨테이너 패턴(Type safe heterogeneous container pattern)**이라 한다.

### 테스트

#### 올바르지 않은 인스턴스 대입 실패 테스트

`String.class`를 넣어주고, 인스턴스에 `Integer` 타입을 넣어줄 경우 컴파일 에러가 발생한다.

```java
@Test
@DisplayName("올바르지 않은 인스턴스 대입 테스트")
void failTest1() {
    f.putFavorite(String.class, 1);
}
```

```
reason: no instance(s) of type variable(s) exist so that Integer conforms to String inference variable T has incompatible bounds: equality constraints: String lower bounds: Integer
```

#### 이종 컨테이너를 사용하지 않은 실패 테스트

Class<T>로 감싸지 않고, 보통의 제네릭만을 사용할 경우 컴파일은 되지만, 실행결과 Null을 반환하게 된다. 

```java
public class Favorites {
    ...
    
    public <T> void putFavorite2(T type, T instance) {
        db.put(Objects.requireNonNull(type.getClass()), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(db.get(type));
    }
}
```

```java
public class FavoritesTest {

    private final Favorites f = new Favorites();

    @Test
    @DisplayName("이종 컨테이너를 사용하지 않은 테스트")
    void failTest2() {
        f.putFavorite2(String.class, 1);
        System.out.println(f.getFavorite(String.class));
    }
}
```

위 코드를 보면 `String.class`를 타입으로 넣고, `Integer` 타입의 1을 넣으면, `getFavorite`을 하는 과정에서 `type.cast`를 못받게 된다.

### 코드 분석

앞서 본 `Favorites` 코드를 하나씩 훑어보자.

```java
private Map<Class<?>, Object> db = new HashMap<>();
```

위 맵은 키와 값 사이의 타입 관계를 보증하지 않는다.
즉, String.class가 들어온다고 하더라도, Integer 타입이 값으로 들어올 수 있다는 것이다.

```java
public <T> T getFavorite(Class<T> type) {
    return type.cast(db.get(type));
}
```

우선, db에서 주어진 클래스 객체에 해당하는 값을 찾아 꺼낸 뒤, 클래스 타입에 캐스팅한 후 반환한다.

### 제약

#### 타입 안정성이 쉽게 깨질 수 있다.

악의적인 클라이언트가 `Class` 객체를 제네릭이 아닌 로 타입으로 넘기면 Favorites 인스턴스의 타입 안전성이 쉽게 깨진다.

```java
@Test
@DisplayName("악의적인 로 타입 인스턴스 테스트")
void test1() {
    f.putFavorite((Class) Integer.class, "Integer의 인스턴스가 아닙니다.");
    System.out.println(f.getFavorite(Integer.class));
}
```

```
java.lang.ClassCastException: Cannot cast java.lang.String to java.lang.Integer
```

위처럼 타입에 `Integer`를 넣어주고, 인스턴스에 `String` 타입을 넣어준 뒤, 타입을 Class로 다시 캐스팅할 경우 컴파일 에러는 뜨지 않지만 비검사 경고가 발생하게 된다.
이후, 코드를 실행해보면 `getFavorite` 함수에서 에러가 발생하는 것을 볼 수 있다.

> Unchecked assignment: 'java.lang.Class' to 'java.lang.Class<java.lang.String>' 

이러한 상황을 완전히 막기는 어렵다. 값을 가져오는 부분에서 에러를 확인하는 것보단, 값을 넣을 때 에러가 발생한다면, 에러를 추적하는데 큰 도움이 될 것이다.

```java
public <T> void putFavorite(Class<T> type, T instance) {
    db.put(Objects.requireNonNull(type), type.cast(instance));
}
```

#### 실체화 불가 타입에서는 사용할 수 없다.

즐겨찾는 `String`이나 `String[]`은 저장할 수 있어도, `List<String>`은 저장할 수 없다.

```java
@Test
@DisplayName("실체화 불가 타입 저장 테스트")
void test2() {
    // 에러 발생 : Cannot access class object of parameterized type
    f.putFavorite(List<String>.class, List.of("1", "2", "3"));
    System.out.println(f.getFavorite(List.class));
}
```

List<String>.class, List<Integer>.class는 모두 동일한 List.class라는 같은 클래스 객체를 공유한다.
만약 이를 허용하게 된다면 Favorites 객체의 내부는 아수라장이 될 것이다.

## 정리

- 일반적인 제네릭 형태에서는 한 컨테이너가 다룰 수 있는 타입 매개변수의 수가 고정되어 있다.
  - 컨테이너 자체가 아닌 키를 타입 매개변수로 바꾸면 제약이 없는 안전 이종 컨테이너를 만들 수 있다.
- 안전 이종 컨테이너는 Class를 키로 사용한다.
  - 이런 식으로 쓰이는 Class 객체를 타입 토큰이라 한다.
  - 개발자가 직접 구현한 키 타입도 쓸 수 있다.
