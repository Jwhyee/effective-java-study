# Item 31

## 생산자(Producer)와 소비자(Consumer)

> 생산자란, 작업을 생성하는 부분
> 소비자란, 해당 작업을 처리하는 부분

아래 코드는 `Collections.copy` 메소드이며, `src`에 대한 내용을 `dest`에 복사해주는 역할을 한다.

```java
public class Collections {
    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        ...
        if (srcSize < COPY_THRESHOLD ||
                (src instanceof RandomAccess && dest instanceof RandomAccess)) {
            for (int i=0; i<srcSize; i++)
                dest.set(i, src.get(i));
        } else {
            ListIterator<? super T> di=dest.listIterator();
            ListIterator<? extends T> si=src.listIterator();
            for (int i=0; i<srcSize; i++) {
                di.next();
                di.set(si.next());
            }
        }
    }
}
```

PECS 공식에 의하면 `dest` 객체는 `super`를 사용했기 때문에 소비자인 것을 알 수 있고,
`src` 객체는 `extends`를 사용했으므로, 생산자임을 알 수 있다.

`if`문 내부에 있는 반복문을 보면 `src`의 크기만큼 반복하며, 값을 가져와 저장할 데이터를 **생산**한다.
이후, 생산된 데이터를 `dest`에 소비하게 된다.

즉, 소비자란 어떠한 데이터가 자기 자신(매개 변수)에게 소비되는 것을 의미하고,
생산자는 자기 자신(매개 변수)를 소비해 무언가를 만드는 것을 의미한다.

### 예제1

```java
public Chooser(Collection<? extends T> choices) {
    choiceList = new ArrayList<>(choices);
}
```

매개변수인 `choices`를 소비해 `ArrayList`를 생산했다.

### 예제2

```java
public static <E> Set<E> union2(Set<? extends E> s1, Set<? extends E> s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```

- Set<? extends E> s1 : `s1`을 소비해 `HashSet`을 생산
- Set<? extends E> s2 : `s2`를 소비해 `result`에 데이터를 넣어줌(생산)

### 예제3

```java
public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
    if (comp==null)
        return (T)min((Collection) coll);

    Iterator<? extends T> i = coll.iterator();
    T candidate = i.next();

    while (i.hasNext()) {
        T next = i.next();
        if (comp.compare(next, candidate) < 0)
            candidate = next;
    }
    return candidate;
}
```

- Collection<? extends T> coll : `coll`을 소비해 `candidate` 생성
- Comparator<? super T> comp : `coll`을 통해 생성된 객체를 `comp`에게 소비

# Item 32

## 힙 오염(Heap pollution)

> JVM의 힙 영역에 저장되어 있는 객체가 불량 데이터를 참조해 런타임 에러가 발생할 수 있는 것

- 원시 타입과 매개변수 타입을 동시에 사용하는 경우
- 확인되지 않은 형변환을 수행하는 경우

```java
// String 서브 타입의 ArrayList 선언
List<String> list1 = new ArrayList<>();
list1.add("홍길동");
list1.add("임꺽정");

// 최상위 타입으로써 다루기 위해 Object 타입으로 업 캐스팅
Object obj = list1;

doSomething(obj);

// ArrayList로 되돌릴 때, String이 아닌 Double로 다운 캐스팅
// 소수값을 리스트에 추가
// Heap 오염 발생
List<Double> list2 = (List<Double>) obj;
list2.add(1.0);
list2.add(2.0);

// 출력 [홍길동, 임꺾정, 1.0, 2.0]
System.out.println(list2);

// list2의 요소 출력
// 문자열과 소수값이 혼합되어 있어 ClassCastException 발생
for(double n : list2) {
    System.out.println(n);
}
```

# 레퍼런스

- [와일드카으돠 PECS](https://goodteacher.tistory.com/606)
- [힙 오염이란](https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%A0%9C%EB%84%A4%EB%A6%AD-%ED%9E%99-%EC%98%A4%EC%97%BC-Heap-Pollution-%EC%9D%B4%EB%9E%80)
