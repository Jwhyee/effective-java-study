# 다 쓴 객체 참조를 해제하라.

C언어를 사용해본 사람이라면, 아래 코드와 같이 메모리 동적할당을 사용해본 경험이 있을 것이다.

```cpp
#include <stdio.h>

// 동적 할당 및 해제를 위한 헤더
#include <stdlib.h>

int main() {
    int n;

    // 사용자로부터 배열의 크기 입력 받기
    printf("배열의 크기를 입력하세요: ");
    scanf("%d", &n);

    // 동적으로 int 배열 할당
    int *arr = (int *)malloc(n * sizeof(int));

    // 메모리 할당 실패 시 예외 처리
    if (arr == NULL) {
        printf("메모리 할당 실패\n");
        return 1;
    }

    // 입력 받은 크기만큼 배열에 값 입력 받기
    printf("배열의 값을 입력하세요: ");
    for (int i = 0; i < n; i++) {
        scanf("%d", &arr[i]);
    }

    // 할당된 메모리 출력
    printf("할당된 배열: ");
    for (int i = 0; i < n; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");

    // 메모리 해제
    free(arr);

    return 0;
}
```

`Java`에는 가비지 컬렉터가 있어, 굳이 메모리를 할당하고, 해제해줄 필요가 없다.
하지만, 아무리 가비지 컬렉터가 열일을 한다고 해도 메모리 관리를 신경쓰지 않는 것은 위험한 일이다.

## 메모리 누수

메모리 누수란, 사용을 마친 객체가 할당한 메모리를 반환하지 않고, 계속 유지되는 상태를 의미한다.

메모리 누수가 누적되면 가비지 컬렉션 활동과 메모리 사용량이 늘어나 성능이 저하가 될 것이다.
최악의 경우 디스크 페이징이나, `OutOfMemoryError`를 일으켜, 프로그램이 예기치 않게 종료될 수 있다.

### 스택의 메모리 누수

아래 코드를 통해 메모리 누수가 일어나는 곳을 찾아보자.

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

육안상으로는 크게 문제는 없어 보이지만 스택을 오래 사용하면,
메모리 누수가 누적되어 프로그램이 예기치 않게 종료될 수 있다.

아래 코드를 통해 확인해보면 `pop()`을 해도, 스택 내부 배열에 아직 `Node`가 남아있는 것을 볼 수 있다.

```java
public class StackTest {
    @Test
    void stackNullTest() {
        Stack stack = new Stack();
        stack.push(new Node(0, 0));
        stack.push(new Node(0, 1));

        Node pop = (Node) stack.pop();
        Node object = (Node) stack.getByIndex(1);
        
        // 테스트 성공!
        assertTrue(object.y == 1);
    }

    static class Node {
        int x, y;
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
```

`pop()`이라는 함수 이름 때문에, 꺼낸 것처럼 보이겠지만,
사실상 아래 사진과 같이 배열에 있는 객체를 복사해 반환해준 것이 끝이다.

![image](https://github.com/Jwhyee/Jwhyee.github.io/assets/82663161/a52aeb7b-94b1-43b2-ad98-82ea8daa6466)

`Stack` 클래스 내부에 있는 `elements` 배열에는 `pop()`을 통해 가져온 객체의 다 쓴 참조를 아직도 갖고 있다.
즉, `pop()`을 했기 때문에 `elemetns` 배열의 활성 영역은 0번 인덱스가 끝이지만, 값을 꺼냈더라도 배열에 잔존하고 있는 상태인 것이다.

> 다 쓴 참조란, 문자 그대로 앞으로 다시 쓰지 않을 참조를 의미

가비지 컬렉션 언어에서는 의도치 않게 객체를 살려두는 경우가 있어 메모리 누수를 찾기가 아주 까다롭다.
객체 참조 하나를 살려두면, 가비지 컬렉터는 그 객체 뿐이 아니라 그 객체가 참조하는 모든 객체(그리고 또 그 객체들이 참조하는 모든 객체 ...)를 회수해가지 못한다.
이와 같이 단 몇 개의 객체가 매우 많은 객체를 회수되지 못하게 할 수 있고, 잠재적으로 성능에 악영향을 줄 수 있다.

해법은 굉장히 간단하다.

```java
public class Stack {
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        // 다 쓴 객체 참조 해제
        elements[size] = null;
        return result;
    }
}

```

위와 같이 사용을 마친 객체에 대해서는 `null`을 사용해 참조 해제해주면 된다.
이렇게 `null` 처리를 하면, 실수로라도 해당 참조를 다시 사용하려 하면 `NullPointerException(NPE)`를 던지며 종료시켜 준다.

하지만, 다 쓴 객체에 대해서 계속 `null`을 추가해주면 코드가 지저분해질 뿐만아니라,
기능보단 `null` 처리에 대해 더 혈안이 될 수 있다. 
가능한 `null` 처리하는 일보단, 다 쓴 참조를 담은 변수를 유효 범위(scope) 밖으로 밀어내는 것이다.

```java
public class PostService {
    public void addAll(PostDto... arr) {
        for (int i = 0; i < arr.length; i++) {
            PostEntity entity = PostEntity.builder()
                    .id(arr[i].id)
                    .title(arr[i].title)
                    .content(arr[i].content)
                    .build();
            repository.add(entity);
        }

        // addAll 메소드가 끝나면, 쓸데없이 만들어졌던 객체들(참조 되지 않는 객체들)이 더 이상 접근되지 않음
    }
}
```

#### 왜 메모리 누수에 취약할까?

`Stack` 클래스는 자기 메모리를 직접 관리하기 때문에 취약해질 수 밖에 없다.
우리가 만든 스택은 객체 자체가 아니라 참조를 담는 `elements` 배열로 저장소 풀을 만들어 원소들을 관리한다.

> 객체 자체를 저장하는 것은 기본형 데이터 타입과 래퍼 클래스 등이 있으며,
> 객체 참조를 담는 것은, 사용자 정의 클래스나 String 등의 객체를 저장할 때 발생된다.<br>
> 실제 객체 자체는 `Heap` 영역에 저장되고, 변수에는 객체의 참조 주소가 저장된다.

아래 코드를 확인해보면, 우리가 육안상으로 `size`를 통해 배열의 활성 영역을 확인할 수 있지만,
실제로는 `elements` 배열에는 자리를 차지하고는 있지만, 활성 영역에 포함되어 있지 않은 객체가 존재할 수 있다.

```java
public class Stack {
    
    private Object[] elements;
    private int size = 0;
    
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return elements[--size];
    }
    
}
```

위 코드를 토대로 3번의 `push()`와 2번의 `pop()`을 했다고 가정하면, 아래 사진과 같은 형태이다.

![image](https://github.com/Jwhyee/problem-solving/assets/82663161/60eec6ab-54d6-41b0-a2a4-fd55b8531f42)

프로그래머 입장에서는 사용되지 않는 것을 알 수 있지만, GC 관점에서는 비활성 영역에 있더라도 유효한 객체이다.
그렇기 때문에 더 이상 사용하지 않는다면 `null` 처리를 통해 GC에게 사용하지 않는다는 것을 알려야한다.

```java
public class Stack {

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
    
}
```

### 캐시의 메모리 누수

간단하게 `LinkedHashMap`을 통해 LRU(Least Recently Used) 캐시를 구현한 코드를 확인해보자!

**LRU 캐시**
- 데이터에 접근하면, 가장 최근에 사용한 것으로 판단한다.
- 캐시 공간이 가득 찰 경우, 가장 오래 전에 사용된 데이터를 제거한다.
- 항상 최근에 사용된 데이터만 캐시에 유지된다.

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        // 캐시 크기, 해시 테이블이 사용 중인 공간 비율, 요소에 접근한 순서를 기준으로 정렬
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

```java
public class CacheTest {
    @Test
    void cacheTest() {
        // 크기가 3인 LRU 캐시 생성
        LRUCache<String, PostEntity> cache = new LRUCache<>(3);

        // 1, 2, 3 추가 -> {one=1, two=2, three=3}
        cache.put("one", new PostEntity(1, "공지1", "내용1"));
        cache.put("two", new PostEntity(2, "공지2", "내용2"));
        cache.put("three", new PostEntity(3, "공지3", "내용3"));

        // 1 사용 -> {two=2, three=3, one=1}
        cache.get("one");

        // 4 추가 -> {three=3, one=1, four=4}
        cache.put("four", new PostEntity(4, "공지4", "내용4"));

        // 테스트 성공!
        assertTrue(cache.toString().equals("{three=PostEntity{id=3}, one=PostEntity{id=1}, four=PostEntity{id=4}}"));
    }
}
```

위 코드를 보면, 키 값이 `two`인 `PostEntity` 객체는 사용하지 않아 GC에서 처리하지만,
이미 사용을 마친 키 값이 `one`인 객체는 캐시의 가장 뒤 쪽으로 이동했다.
이렇게 사용을 마친 객체 참조는 제거해야하기 때문에 `remove()`를 통해 참조를 해제해주거나,
`WeakHashMap`을 통해 다 쓴 엔트리는 자동적으로 제거되도록 하면 된다.

```java
public class CacheTest {
    @Test
    void weakHashMapCacheTest() {
        // WeakHashMap을 캐시로 사용
        Map<String, String> cache = new WeakHashMap<>();

        // 데이터를 생성하여 캐시에 추가
        String key1 = "key1";
        String value1 = "Value1";
        cache.put(key1, value1);

        // 캐시에서 데이터를 가져와 사용
        if (cache.containsKey(key1)) {
            String cachedValue = cache.get(key1);
            System.out.println("Cached Value: " + cachedValue);
        }

        // null을 이용한 참조 해제
        key1 = null;

        // 가비지 컬렉션 시도
        System.gc();

        // 일반적으로 WeakHashMap은 가비지 컬렉션 후에 참조가 사라진 항목을 자동으로 제거
        assertTrue(!cache.containsKey(key1));
    }
}
```

`key`에 대한 참조를 해제하기만 해도, 키와 값 모두 제거가 된다.

#### 엔트리 유효 기간 정의

캐시를 만들어서 사용할 때, 엔트리의 유효 기간을 정확히 정의하기 어렵다.
때문에 시간이 지날수록 엔트리의 가치를 떨어뜨리는 방식을 흔히 사용한다.

`ScheduledThreadPoolExecutor`와 같은 백그라운드 스레드를 활용하거나, 캐시에 새 엔트리를 추가할 때,
부수 작업으로 수행하는 방법이 있다.

`LinkedHashMap`으로 구현한 `LRU Cache`는 `removeEldestEntry()` 메소드를 이용한다.
이 방식은 새 엔트리를 추가할 때, 캐시의 크기를 넘으면, 자동으로 가장 오래전에 사용한 엔트리를 제거한다.

## 정리

- 메모리 누수는 철저한 코드리뷰나 힙 프로파일러 같은 디버깅 도구를 동원해야만 발견되기도 한다.
- 때문에, 위와 같이 메모리 누수를 방지할 수 있도록 코드를 작성할 수 있어야한다.