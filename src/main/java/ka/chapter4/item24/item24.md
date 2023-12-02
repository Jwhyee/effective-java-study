# 멤버 클래스는 되도록 static으로 만들라.

> 중첩 클래스(nested class)란, 다른 클래스 안에 정의된 클래스를 의미한다.

중첩 클래스는 아래 상황을 제외하곤 톱레벨 클래스로 두는 것이 좋다.

- 자신을 감싸고 있는 클래스에서 사용

## 중첩 클래스의 종류

언제 어떤 방식으로 사용하는지 알아보자.

### 1. 정적 멤버 클래스

#### public 정적 메소드

정적 멤버 클래스의 특징

- 다른 클래스 안에 선언된다.
- 바깥 클래스의 `private` 멤버에도 접근할 수 있다.

```java
public class Color {

    private static Set<Pos> posSet = new HashSet<>();

    public Color() {}

    public void addPos(int x, int y) {
        new Pos(x, y);
    }

    public static class Pos {
        final int x, y;

        Pos(int x, int y) {
            this.x = x;
            this.y = y;
            posSet.add(this);
        }
    }
}
```

위와 같이 바깥 클래스의 `private` 정적 멤버에도 접근할 수 있는 것을 제외하고는 일반 클래스와 동일한다.

정적 멤버 클래스는 다른 정적 멤버와 동일하게, `static`이 붙은 멤버에만 접근할 수 있는 접근 규칙을 적용 받는다.

#### private 정적 메소드

흔히 바깥 클래스가 표현하는 객체의 한 부분을 나타낼 때 사용한다.

K-V를 매핑시키는 Map 인스턴스를 생각해보면, 각각의 K-V 쌍을 표현하는 엔트리 객체를 가지고 있다.

```java
public interface Map<K, V> {
    interface Entry<K, V> {
        K getKey();
        V getValue();
        V setValue(V value);
    }
}
```

모든 엔트리가 맵과 연관되어 있지만, 엔트리의 메소드들은 맵을 직접 사용하지는 않는다.

```java
// 값을 꺼내오는 용도로만 사용
for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
    K key = e.getKey();
    V value = e.getValue();
    putVal(hash(key), key, value, false, evict);
}
```

따라서 엔트리를 비정적 멤버 클래스로 표현하는 것은 낭비이며, `private` 정적 멤버 클래스가 가장 알맞다.

### 2. 비정적 멤버 클래스

정적 멤버 클래스와의 구문상 차이점은 단순히 `static`이 붙어 있고, 없고 뿐이지만, 의미상으로는 큰 차이를 갖고 있다.

#### 인스턴스의 연결

비정적 멤버 클래스의 인스턴스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다.

```java
public class OuterClass {
    private int outerData;

    public void outerMethod(int num) {
        NonStaticInnerClass inner = new NonStaticInnerClass();
        this.outerData = num;
        inner.innerMethod();
    }

    public class NonStaticInnerClass {
        public void innerMethod() {
            System.out.println("Inner method called");
            int data = OuterClass.this.outerData;
            System.out.println("Accessing outer class data: " + data);
        }
    }
}
```

위에서 `OuterClass.this.outerData`라는 코드와 같이 정규화된 this를 사용해 바깥 인스턴스의 참조 혹은 메소드를 호출할 수 있다.

> 정규화된 this란, `ClassName.this` 형태로 바깥 클래스의 이름을 명시하는 용법을 말한다.

따라서 개념상 중첩 클래스의 인스턴스가 바깥 인스턴스와 독립적으로 존재할 수 있다면, 정적 멤버 클래스로 만들어야 한다.
비정적 멤버 클래스는 바깥 인스턴스 없이는 생성할 수 없기 때문이다.

```java
@Test
void instanceFailTest() {
    // 에러 발생!
    OuterClass.NonStaticInnerClass nsic = new OuterClass.NonStaticInnerClass();
}
```

비정적 멤버 클래스의 인스턴스와 바깥 인스턴스 사이의 관계는 멤버 클래스가 인스턴스화될 때 확립되며, 더 이상 변경할 수 없다.

이 관계는 바깥 클래스의 인스턴스 메소드에서 비정적 멤버 클래스의 생성자를 호출할 때 자동으로 만들어지는 것이 보통이지만,
드물게는 `OuterClass.new InnerClass(args)`와 같이 직접 호출해 수동으로 만들기도 한다.

```java
@Test
void instanceTest() {
    OuterClass o = new OuterClass();
    OuterClass.NonStaticInnerClass nsic = o.new NonStaticInnerClass();

    nsic.innerMethod();
}
```

> 이러한 관계 정보는 비정적 멤버 클래스의 인스턴스 안에 만들어져 메모리 공간을 차지하게 된다.
> 또한, 생성하는 시간도 더 걸리게 되므로, 좋지 않다.

#### 어댑터 정의

비정적 멤버 클래스는 어댑터를 정의할 때 자주 쓰인다.

> 즉, 어떤 클래스의 인스턴스를 감싸 마치 다른 클래스의 인스턴스처럼 보이게하는 뷰로 사용되는 것이다.

예를 들어 `Map` 인터페이스의 구현체들은 보통 자신의 컬렉션 뷰를 구현할 때, 비정적 멤버 클래스를 사용한다.

```java
public class HashMap<K,V> {
    ...
    final class KeySet extends AbstractSet<K> {
        ...
    }
    final class Values extends AbstractCollection<V> {
        ...
    }
}
```

비슷하게 `Set`, `List` 같은 다른 컬렉션 인터페이스 구현들도 자신의 반복자를 구현할 때, 비정적 멤버 클래스를 주로 사용한다.

```java
public class MySet<E> extends AbstractSet<E> {
    @Override public Iterator<E> iterator() {
        return new MyIterator();
    }
    
    private class MyIterator implements Iterator<E> {
        ...
    }
}
```

멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면, 무조건 `static`을 붙여서 정적 멤버 클래스로 만들자.

> `static`을 생략하면 바깥 인스턴스로의 숨은 외부 참조를 갖게 되어 시간과 공간이 소비된다.

더 심각한 문제는 GC가 바깥 클래스의 인스턴스를 수거하지 못해 메모리 누수가 생길 수 있다는 것이다.

### 3. 익명 클래스

이름과 같이 익명 클래스에는 당연히 이름이 없으며, 바깥 클래스의 멤버도 아니다.

일반적인 멤버 클래스와 달리, 쓰이는 시점에 선언과 동시에 인스턴스가 만들어지는 형태이다.

> 위에서 소개한 정적 및 비정적 멤버 클래스는 바깥 클래스가 생성될 때 인스턴스가 생성된다.

이러한 특징 때문에 코드의 어디서든 만들 수 있으며, 오직 비정적인 문맥에서 사용될 때만 바깥 클래스의 인스턴스를 참조할 수 있다.
정적 문맥에서라도 상수 변수 이외의 정적 멤버는 가질 수 없다.
즉, 상수 표현을 위해 초기화된 final 기본 타입과 문자열 필드만 가질 수 있다.

```java
static List<Integer> intArrayAsList(int[] arr) {
    Objects.requireNonNull(a);
    
    return new AbstractList<>() {
        @Override public Integer get(int idx) {
            return a[idx]; // 오토박싱
        }
        
        @Override public Integer set(int idx, Integer val) {
            int oldVal = a[idx];
            a[idx] = val;   // 오토언박싱
            return oldVal;  // 오토박싱
        }
        
        @Override public int size() {
            return arr.length;
        }
    };
}
```

item20에 나왔던 추상 골격 구현과 같이 사용된 `return new AbstractList() {...}`이 익명 클래스에 속한다.

#### 제약

1. 선언한 지점에서만 인스턴스를 만들 수 있다.
2. `instanceof` 혹은 클래스 이름이 필요한 작업을 수행할 수 없다.
3. 여러 인터페이스를 구현할 수 없다.
4. 인터페이스를 구현하는 동시에 다른 클래스를 상속할 수 없다.
5. 익명 클래스를 사용하는 클라이언트는 그 익명 클래스가 상위 타입에서 상속한 멤버 외에는 호출할 수 없다.
6. 익명 클래스는 표현식 중간에 등장하므로, 짧지 않으면 가독성이 떨어진다.

### 4. 지역 클래스

지역 클래스는 앞서 등장한 중첩 클래스 중 가장 드물게 사용된다.

```java
public class OuterClass {
    private int outerData = 10;

    public void outerMethod() {
        int localData = 5;

        class LocalClass {
            public void localMethod() {
                System.out.println("Outer data: " + outerData);
                System.out.println("Local data: " + localData);
            }
        }

        LocalClass localObj = new LocalClass();
        localObj.localMethod();
    }
}
```

지역 클래스는 지역변수를 선언할 수 있는 곳이면, 실질적으로 어디서든 선언할 수 있고, 유효 범위도 지역변수와 같다.

다른 세 중첩 클래스와의 공통점도 하나씩 갖고 있다.

1. 멤버 클래스처럼 이름이 있고, 반복해서 사용할 수 있다.
2. 익명 클래스처럼 비정적 문맥에서 사용될 때만 바깥 인스턴스를 참조할 수 있다.
3. 정적 멤버는 가질 수 없으며, 가독성을 위해 짧게 작성해야 한다.

## 정리

- 멤버 클래스
  - 메소드 밖에서도 사용한다.
  - 메소드 안에 정의하기엔 너무 길다.
- 비정적 멤버 클래스
  - 멤버 클래스의 인스턴스 각각이 바깥 인스턴스를 참조한다.
- 정적 멤버 클래스
  - 멤버 클래스의 인스턴스 각각이 바깥 인스턴스를 참조하지 않는다.
- 익명 클래스
  - 중첩 클래스가 한 메소드 안에서만 쓰인다.
  - 중첩 클래스의 인스턴스를 생성하는 지점이 단 한 곳이다.
  - 해당 타입으로 쓰기에 적합한 클래스나 인터페이스가 이미 있다.
- 지역 클래스
  - 위 모든 사항에 해당되지 않을 경우