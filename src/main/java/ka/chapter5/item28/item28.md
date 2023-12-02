# 배열보다는 리스트를 사용하라.

## 타입 차이

배열과 제네릭 타입에는 중요한 차이가 두 가지 있다.

- 공변과 불공변
- 실체화(reify)
  
### 공변과 불공변

배열은 공변;함께 변한다(covariant)이다.
`Sub`가 `Super`의 하위 타입이라면, `Sub[]`는 `Super[]`의 하위 타입이 된다.

`Long`은 `Object`의 하위에 속해있기 때문에 `Object[]`와 `Long[]`은 함께 사용이 가능하다.

```java
@Test
void objArrTest() {
    Object[] objectArr = new Long[1];
    // 런타임 에러 발생 : java.lang.ArrayStoreException: java.lang.String
    objectArr[0] = "타입이 달라 넣을 수 없다.";
}
```

반면, 제네릭은 불공변(invariant)이다.
서로 다른 타입이 있을 때, `List<Type1>`은 `List<Type2>`의 하위 타입도 아니고, 상위 타입도 아니다.

제네릭의 경우 타입 자체를 `Object`로 선언했기 때문에 `Long` 타입의 `ArrayList`를 받을 수 없다.

```java
@Test
void objListTest() {
    // 컴파일 에러 발생 : 타입이 호환되지 않음
    List<Object> ol = new ArrayList<Long>();
    ol.add("타입이 달라 넣을 수 없다.");
}
```

위 두 코드의 가장 큰 차이점은 어느 시점에 에러를 잡아주느냐이다.

어느 쪽이든 `Long`용 저장소에 `String`을 넣을 수는 없다.
다만 배열에서는 그 실수를 런타임에야 알게 되지만, 리스트를 사용하면 컴파일할 때 바로 알 수 있다.

### 실체화(reify)

배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다.
위 코드에서 보았듯이 `Long[]`에 `String`을 넣으려고 하면 `ArrayStoreException` 에러가 발생한다.

반면, 제네릭은 타입 정보가 런타임에 소거(erasure)된다.
원소 타입을 컴파일타임에만 검사하며, 런타임에는 알수조차 없게 되는 것이다.

즉, 여기서 말하는 실체화란 타입의 정보가 런타임에도 계속 남아있느냐를 의미하는 것이다.

## 제네릭 배열

제네릭 배열을 만들지 못하게 막은 이유는 타입 안전하지 않기 때문이다.
이를 허용한다면 컴파일러가 자동 생성한 형변환 코드에서 런타임에 `ClassCastException`이 발생할 수 있다.

```java
@Test
void arrayWithListTest() {
    // String 타입 리스트의 배열 생성
    List<String>[] strList = new List<String>[1];   // (1)
    // Integer 타입 Immutable 리스트 생성
    List<Integer> intList = List.of(42);            // (2)
    
    // Object 타입 배열 생성 후 문자열 리스트 배열 주입
    Object[] objects = strList;                     // (3)
    // 배열 내부 0번 인덱스에 리스트 추가
    objects[0] = intList;                           // (4)
    
    String s = strList[0].get(0);                   // (5)
    System.out.println("s = " + s);                 // (6)
}
```

만약 제네릭 배열을 생성하는 (1)이 허용된다고 가정해보자.
- (2)는 원소가 하나인 `List<Integer>`를 생성한다.
- (3)은 (1)에서 생성한 `List<String>`의 배열을 `Object` 배열에 할당한다.
- (4)는 (2)에서 생성한 `List<Integer>`의 인스턴스를 `Object` 배열의 첫 원소로 저장한다.
  - 제네릭은 소거 방식으로 구현되어서 이 역시 성공한다.

즉, 런타임에는 `List<Integer>` 인스턴스의 타입은 단순히 `List`가 되고, `List<Integer>[]` 인스턴스의 타입은 `List[]`가 된다.
따라서 (4)에서도 `ArrayStoreException`을 일으키지 않는다.

이제부터 문제가 발생한다.
`List<String>` 인스턴스만 담겠다고 선언한 `strList` 배열에는 지금 `List<Integer>` 인스턴스가 저장돼 있다.
그리고 (5)는 이 배열의 처음 리스트에서 첫 원소를 꺼내려고 한다.

컴파일러는 꺼낸 원소를 자동으로 `String`으로 형변환하는데, 이 원소는 `Integer`이므로 런타임에 ``ClassCastException`이 발생한다.
이런 일을 방지하려면 제네릭 배열이 생성되지 않도록 (1) 과정에서 컴파일 오류를 내야한다.

### 형변환 방식

아래 클래스는 컬렉션 안의 원소 중 하나를 무작위로 선택해 반환하는 `choose` 메소드를 제공한다.

```java
public class Chooser {
    private final Object[] choiceArray;

    public Chooser(Collection choices) {
        choiceArray = choices.toArray();
    }

    public Object choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceArray[rnd.nextInt(choiceArray.length)];
    }
}
```

```java
public class ChooseTest {
    @Test
    void chooseTest() {
        Chooser c = new Chooser(List.of(1, 2, 3, 4, 5, 6, 7, 8));
        System.out.println(c.choose());
    }
}
```

위 코드와 같이 기본형이라면 크게 문제되는 일은 없을 것이다.
하지만 사용자가 정의한 클래스에 대한 인스턴스가 들어가있다면 어떨까?

```java
public class ChooseTest {
    static class Post {
        int id;
        String title, content;

        public Post(int id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }
    }

    private List<Post> initPost() {
        List<Post> pl = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pl.add(
                    new Post(i, "title%d", "content%s".formatted(i))
            );
        }
        return pl;
    }

    @Test
    void chooseInstanceTest() {
        Chooser c = new Chooser(initPost());
        Object o = c.choose();
        if (o instanceof Object) {
            Post p = (Post) o;
            System.out.println("TRUE : " + p.title);
        }
    }
}
```

```
TRUE : chapter5.item28.choose.ChooseTest$Post@6d3af739
```

당연히 `Object` 타입으로 반환되었기 때문에 해당 객체를 사용하기 위해서는 형변환을 해주어야 한다.
만약 이 상황에서 제네릭을 사용했다면 더 유연하게 설계를 할 수 있다.

```java
public class ChooserGeneric<T> {
    private final T[] choiceArray;

    public ChooserGeneric(Collection<T> choices) {
        choiceArray = choices.toArray();
    }
}
```

위와 같이 설계할 경우 `choices.toArray()`에서 컴파일 에러가 발생한다.
그 이유는 런타임 과정에서 제네릭에 대한 정보가 소거되기 때문이다.

만약 해당 코드를 `(T[]) choices.toArray()`로 형변환을 할 경우 안전한지 보장할 수 없다는 경고 메시지가 뜬다.

즉, 배열과 제네릭은 잘 맞물리지 않기 때문에 `List`를 이용하면 위와 같은 불편함 없이 코드를 작성할 수 있다.

```java
public class ChooserGeneric<T> {
    private final List<T> choiceList;

    public ChooserGeneric(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }
}
```

## 정리

- 배열과 제네릭에는 매우 다른 타입 규칙이 적용된다.
- 배열은 공변이고, 실체화된다.
  - 배열은 런타임에는 타입 안전하지만 컴파일 타임에는 그렇지 않다.
- 제네릭은 불공변이고, 타입 정보가 소거된다.
- 이 둘을 섞어 쓸 때 에러를 만나면, 가능한 배열을 리스트로 대체해서 해결하자.