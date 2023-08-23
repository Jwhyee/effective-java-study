# finalizer와 cleaner 사용을 피하라.

객체를 생성하는 `new`라는 키워드가 있듯이, 자바는 두 가지 객체 소멸자를 제공한다.

- finalizer
- cleaner

하지만 이 두 기능은 예측할 수 없고, 상황에 따라 위험할 수 있어 일반적으로 불필요하다.

## finalizer

`finalizer` 메소드는 `Object` 클래스 내부에 있어, `@Override`를 통해 재정의할 수 있다.

```java
@Deprecated(since="9")
protected void finalize() throws Throwable { }
```

하지만 해당 메소드를 살펴보면,
위와 같이 `finalze`는 Java 9 버전부터 `Deprecated` 처리된 것을 알 수 있다.

```java
public class Post {
    private int id;
    private String title, content;

    public Post(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        System.out.println(id + "번 객체 생성");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println(id + "번 객체 소멸");
        Util.cnt++;
        super.finalize();
    }
}
```

위와 같이 생성자를 통해 객체가 생성될 때는 생성에 대한 메세지가 나오고,
객체가 소멸될 때는 소멸에 대한 메세지와 소멸된 객체의 개수를 구하기 위해 카운트 변수 증가를 해줬다.

```java
public class FinalizerTest {

    void makeObject() {
        for (int i = 1; i <= 1000000; i++) {
            Post p = new Post(i, "title", "content");
        }
    }

    @Test
    void postFinalizeTest1() throws InterruptedException {
        makeObject();
        // 10초간 스레드 재우기
        Thread.sleep(10000);
        System.out.println("Util.cnt = " + Util.cnt);
        
        // 테스트 실패!
        assertTrue(Util.cnt == 1000000);
    }

}
```

```bash
...
947625
```

결과를 확인해보면 100만이 아닌 94만 7천개의 객체만 소멸된 것을 알 수 있다.
**item7**에서 공부한 내용대로라면, 함수를 통해 **scope 밖으로 다 쓴 객체 참조를 밀어내면 객체가 소멸**이 될텐데,
왜 모든 객체가 소멸되지 않은 것일까?

그 이유는 `finalizer`가 제때 실행되지 않기 때문이다.
그렇기 때문에 `cnt` 값도 다른 것이며, 실제로 객체가 소멸이 되었더라도,
`finalize()` 메소드가 실행되기까지 얼마나 걸릴지 알 수 없는 것이다.

그러면 `System.gc()`를 통해 강제로 반납하면 어떻게 될까?

```java
public class FinalizerTest {

    void makeObject() {
        for (int i = 1; i <= 1000000; i++) {
            Post p = new Post(i, "title", "content");
        }
    }

    @Test
    void postFinalizeTest2() throws InterruptedException {
        Util.cnt = 0;
        makeObject();
        System.gc();
        Thread.sleep(10000);
        System.out.println("Util.cnt = " + Util.cnt);
        
        // 테스트 통과!
        assertTrue(Util.cnt == 1000000);
    }

}
```

```bash
169번 객체 소멸
168번 객체 소멸
668번 객체 소멸
667번 객체 소멸
```

결과를 보면, 객체가 생성된 순서대로 소멸되는 것이 아닌 무작위로 소멸이 되는 것을 확인할 수 있다.

즉, `finalizer`는 예측할 수 없기 때문에 제때 실행되어야하는 작업에는 쓸 수 없으며,
매 번 `System.gc()`를 통해 실행할 수 없기 때문에 일반적으로 불필요한 기능이다.

`gc()`가 아닌 `runFinalization()`도 동일하다.

```java
public class Post {
    private int id;
    private String title, content;

    public Post(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 생성");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 소멸");
        Util.cnt++;
        super.finalize();
    }
}
```

```java
public class FinalizerTest {

    void makeObject() {
        for (int i = 1; i <= 100000; i++) {
            Post p = new Post(i, "title", "content");
        }
    }

    @Test
    void postFinalizeTest2() throws InterruptedException {
        Util.cnt = 0;
        makeObject();
        System.runFinalization();
        Thread.sleep(10000);
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }

}
```

```bash
Thread[main,5,main] - 100000번 객체 생성
Util.cnt = 21912
```

`System.gc()`는 모든 객체를 수거하기라도 해주는데,
`runFinalization()`는 수거 대상 중 일부 객체의 `finalize()` 메소드를 호출하고, 간간히 실행 된 것은 알 수 있지만,
생성된 모든 객체의 `finalize()`를 호출하지는 않았다.

그럼 왜 `finalize()`는 제때 실행되지 않는 것일까? 그 이유는 단순하다.
`finalizer` 스레드가 다른 스레드에 비해 우선 순위가 낮기 때문에 실행될 기회 조차 얻지 못한 것이기 때문이다.

> finalizer 쓰레드의 우선순위가 낮은 이유는 객체의 finalize() 메소드가 실행되는 동안에는 객체가 살아있어야하기 때문이다.
> 만약 finalizer 쓰레드의 우선순위가 높았다면, 사용을 마지치 않은 상태에서 finalize() 메소드가 호출되거나,
> 다른 중요한 작업이 진행되는 중에 finalizer 쓰레드가 활성화 되면서 예기치 못한 동작을 유발할 수 있다.

## cleaner

`cleaner` 기능은 `AutoCloseable`을 구현해 사용하는 것이다.

```java
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // 이미 Room에서 State를 참조하고 있기 때문에, Room을 참조할 경우 순환참조 발생!
    // 때문에 생성자를 통해 쓰레기를 넘겨 받음
    private static class State implements Runnable {
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        // cleanable.clean()이 실행되면 run() 메소드 실행
        @Override public void run() {
            System.out.println("방 청소");
            numJunkPiles = 0;
        }
    }

    private final State state;

    // cleanable 객체, 수거 대상이 되면 방을 청소함
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    // 객체가 소멸될 때, 실행되는 메소드
    @Override public void close() {
        // cleanable에 청소를 실행할 객체(Runnable 구현체)로 state를 지정함
        // 때문에 clean()이 실행되면 State 클래스 내부 run() 메소드 실행
        cleanable.clean();
    }
}
```

아래 코드와 같이 `try-with-resources` 블록으로 감싼 뒤, 해당 객체 사용을 마치면 자동으로 소멸된다.

```java
public class RoomTest {
    @Test
    void adultTest() {
        // try가 끝나면 객체를 소멸시킴
        // 때문에 Room 내부에 있는 close()메소드 실행
        try (Room myRoom = new Room(7)) {
            System.out.println("청소 시작");
        }
    }
}
```

때문에 아래와 같은 순서로 코드가 실행된다.

**실행 순서**

- `Room` 생성자 메소드
  - `state`, `cleanable` 주입
- `"청소 시작"` 출력
- `Room.close()` 실행
  - `Room.cleanable.clean()` 실행
  - `Room.State.run()` 실행
- 종료

하지만 위와 같이 `try-with-resources` 블록으로 감싸지 않고,
일반 객체처럼 사용하면 `"방 청소"`가 출력되지 않는다.

```java
public class RoomTest {
    @Test
    void teenagerTest() {
        new Room(99);
        System.out.println("청소 시작");
    }
}
```

`System.gc()`를 통해 청소를 진행할 수는 있지만,
매 번 `gc()`를 호출할 수 없기 때문에 `Cleaner` 또한 예측할 수 없는 것이다.

## 문제점

### 1. 즉시 수행되지 않는다.

위 코드 예시를 통해 확인 가능하다.

### 2. 예외 처리를 무시한다.

아래 코드와 같이 소멸되는 과정에서 특정 id를 가진 객체가 소멸될 때, `Exception`을 던지도록 하였다.

```java
public class Post {
    @Override
    protected void finalize() throws Throwable {
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 소멸");
        if (id == 50000) {
            throw new RuntimeException("에러 ㅅㄱ");
        }
        Util.cnt++;
        super.finalize();
    }
}
```

```java
public class FinalizerTest {
    void makeObject() {
        for (int i = 1; i <= 100000; i++) {
            Post p = new Post(i, "title", "content");
        }
    }

    @Test
    void postFinalizeTest2() throws InterruptedException {
        Util.cnt = 0;
        makeObject();
        System.gc();
        Thread.sleep(10000);
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }
}
```

```bash
Util.cnt = 99999
```

분명 우리가 아는 사실은 `Exception`이 발생하면 프로그램이 종료되어야 한다.
하지만, 결과 `cnt`를 보면 알 수 있듯,
해당 객체가 소멸될 때, `Exception`이 발생해 뒤에 코드는 무시하고 그냥 넘어간 것이다.
즉, `Exception`도 무시 당했고, 뒤에 코드도 함께 무시 당했다.

이와 같이 잡지 못한 예외 때문에 해당 객체는 마무리가 덜 된 상태로 남을 수 있다.

### 3. 심각한 성능 문제

아래 코드는 `Thread.sleep(...)` 기능을 지운 코드이다.

```java
public class FinalizerTest {
    @Test
    void postFinalizeTest2() throws InterruptedException {
        Util.cnt = 0;
        makeObject();
        System.gc();
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }
}
```

`gc()` 메소드를 사용하지 않으면 해당 테스트는 대략 `260ms`가 걸린다.
하지만, `gc()`를 사용해 `finalize()`가 호출되게 만들면 `420ms`가 걸린다.

즉, 우리가 `gc()`를 호출하지 않아도, 해당 객체는 수거가 자동으로 되지만,
객체를 생성하고, `finalize()`를 굳이 호출해서 파괴까지 하니 시간이 더 오래 걸린 것이다.
`finalizer`가 GC의 효율을 떨어뜨리기 때문이다.

### 4. finalizer 공격에 취약하다.

`finalizer`를 사용한 클래스는 `finalizer` 공격에 노출되어 심각한 보안 문제를 일으킬 수도 있다.

## 대비책

`AutoCloseable`을 구현해주고, 클라이언트에서 인스턴스를 다 쓰고 나면 `close()` 메소드를 호출하면 된다.

```java
public class Obj implements AutoCloseable{
    private final int id;

    public Obj(int id) {
        this.id = id;
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 생성");
    }

    @Override
    public void close() throws Exception {
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 소멸");
        Util.cnt++;
    }
}
```

```java
public class AutoCloseableTest {
    void makeObject() throws Exception {
        for (int i = 1; i <= 100000; i++) {
            Obj p = new Obj(i);
            p.close();
        }
    }
    @Test
    void autoCloseableTest() throws Exception {
        Util.cnt = 0;
        makeObject();
        System.out.println("Util.cnt = " + Util.cnt);
        
        // 테스트 성공!
        assertTrue(Util.cnt == 100000);
    }
}
```

```bash
...
Thread[main,5,main] - 100000번 객체 생성
Thread[main,5,main] - 100000번 객체 소멸
Util.cnt = 100000
```

실행 결과와 같이 `finalizer` 쓰레드를 기다릴 필요도 없이 바로 회수되는 것을 알 수 있다.
각 인스턴스는 자신이 닫혔는지를 추적하는 것이 좋다.
즉, `close()` 메소드에서 이 객체는 더 이상 유효하지 않음을 필드에 기록하고,
다른 메소드는 이 필드를 검사해서 객체가 닫힌 후에 불렸다면 `IllegalStateException`을 던지는 것이다.

```java
public class Obj implements AutoCloseable{
    private final int id;
    // 자신이 닫혔는지 추적할 수 있도록 필드 추가 
    private boolean isClose;

    public Obj(int id) {
        this.id = id;
        isClose = false;
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 생성");
    }

    @Override
    public void close() throws Exception {
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 소멸");
        isClose = true;
        Util.cnt++;
    }
    
    public boolean isClose() {
        // 소멸된 객체에 접근하면 Exception 발생
        if(isClose) {
            throw new IllegalStateException("소멸된 객체입니다.");
        }
        return false;
    }
}
```

```java
public class AutoCloseableTest {
    void makeObject() throws Exception {
        for (int i = 1; i <= 100000; i++) {
            Obj p = new Obj(i);
            p.close();
            
            // 객체가 유효한지 확인 후 기능 
            if(!p.isClose()) {
                System.out.println(p);
            }
        }
    }
    @Test
    void autoCloseableTest() throws Exception {
        Util.cnt = 0;
        makeObject();
        System.out.println("Util.cnt = " + Util.cnt);
        
        // 테스트 실패!
        assertTrue(Util.cnt == 100000);
    }
}
```

```bash
Thread[main,5,main] - 1번 객체 생성
Thread[main,5,main] - 1번 객체 소멸
java.lang.IllegalStateException: 소멸된 객체입니다.
```

## 사용법

### 안전망 역할

자원의 소유자가 `close()` 메소드를 호출하지 않았을 때를 대비한 안전망 역할을 할 수 있다.

```java
public class Obj implements AutoCloseable {
    ...

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
```

```java
public class AutoCloseableTest {
    void makeObject() throws Exception {
        for (int i = 1; i <= 100000; i++) {
            Obj p = new Obj(i);
        }
    }
    @Test
    void autoCloseableTest() throws Exception {
        Util.cnt = 0;
        makeObject();
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }
}
```

`close()` 메소드를 호출하지 않아 객체가 소멸되지 않았다. 이렇게 메모리에 남아있는 것보단,
언젠가 `finalize()` 메소드가 호출되어 객체를 소멸시키는게 아예 안 하는 것보단 낫다.

하지만 생성 비용이 비싼 객체는 그렇다 쳐도, 이 외 상황에서는 그럴만한 값어치가 있는지 확인하고 쓰는게 좋다.

### 네이티브 피어와 연결된 객체

**네이티브 피어란?**

- 일반 자바 객체가 네이티브 메소드를 통해 기능을 위임한 네이티브 객체를 의미한다.

```java
public class NativeUtil {
    public native int calculateSum(int a, int b);

    static {
        System.loadLibrary("NativeLibrary");
    }
}
```

```cpp
#include <jni.h>

JNIEXPORT jint JNICALL Java_NativeUtil_calculateSum(JNIEnv *env, jobject obj, jint a, jint b) {
    printf("%d", a);
    return a + b;
}
```

위와 같이 Java 클래스에서는 기능만 정의를 해놓고, 실제 구현은 C와 같은 네이티브 코드를 이용해 구현한 것이다.
이러한 네이티브 피어는 자바 객체가 아니다보니 GC가 그 존재를 알지 못한다.
즉, 자바 피어를 회수할 때, 네이티브 객체까지 회수하지 못한다는 것이다.

이러한 상황에서 `cleaner` 혹은 `finalizer`를 이용해 처리하기 적합하다.
단, 성능 저하를 감당할 수 있고, 네이티브 피어가 심각한 자원을 가지고 있지 않을 때에만 해당이 된다.

## 정리

- cleaner(자바 8까지는 finalizer)는 안전망 역할이나 중요하지 않은 네이티브 자원 회수용으로만 사용하자.
- 위 경우라고 하더라도 실행이 될지는 불확실하며, 성능 저하가 될 수 있으니 주의해야 한다.