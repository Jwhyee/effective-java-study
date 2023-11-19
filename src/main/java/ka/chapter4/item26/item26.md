# 로 타입은 사용하지 말라.

## 제네릭

> 제네릭(generic)은 자바 5부터 사용할 수 있으며, 안정적인 형변환을 할 수 있도록 지원하는 타입이다.

제네릭은 클래스 혹은 인터페이스에 타입 매개변수를 통해 붙여 사용할 수 있다.

```java
public class ArrayList<E> {...}

public interface List<E> {...}
```

이러한 형태를 **제네릭 클래스** 혹은 **제네릭 인터페이스**라고 부르며, 이를 통 틀어서 제네릭 타입이라고도 부른다.

### 매개변수화 타입

각각의 제네릭 타입은 일련의 **매개변수화 타입**(Parameterized type)을 정의한다.

```java
public class ArrayList<E> {...}
```

위 코드와 같이 클래스(혹은 인터페이스)이름을 작성하고, 꺾쇠 괄호 안에 실제 타입 매개변수들을 나열한다.
`List<String>`의 경우에는 원소의 타입인 `String`인 리스트를 뜻하는 `매개변수화` 타입이다.

### 로 타입

제네릭 타입을 하나 정의하면 그에 딸린 **로 타입**(Raw type)도 함께 정의된다.

```java
public class ArrayList<E> {
    ...
}

ArrayList = new ArrayList();
```

위 코드와 같이 `ArrayList`가 제네릭 타입이어도, 타입 정보 없이 객체를 생성할 수 있게 된다.
이는, 제네릭이 도래하기 전 코드와 호환되도록 하기 위한 것이다.

```java
public class StampTest {
    static class Coin {
        public void print() {
            System.out.println("Coin.print");
        }
    }

    static class Stamp {
        public void print() {
            System.out.println("Stamp.print");
        }
    }
    @Test
    void stampTest() {
        Collection stamps = new ArrayList();

        stamps.add(new Stamp());
        stamps.add(new Coin());

        for (Iterator i = stamps.iterator(); i.hasNext();) {
            Stamp s = (Stamp) i.next();
            s.print();
        }
    }
}
```

`stamps.add()`를 확인해보면 `Coin`과 `Stamp`를 모두 넣은 것을 볼 수 있다.
이것이 가능한 이유는 `ArrayList`를 로타입으로 사용했기 때문이다.
위 코드를 실행해보면 다음과 같은 결과가 나온다.

```bash
Stamp.print
Exception in thread "main" java.lang.ClassCastException ...
```

여기서 주의해야할 점은 바로 컴파일타임이 아닌 런타임에서 에러를 발견하고 잡는다는 것이다.

`Stamp 인스턴스만 취급한다.`라는 주석이 있어도 개발자가 그 외의 것을 넣어도 에러를 알아차릴 수 없다.
이와 같이 로타입만을 사용하면 런타임에 문제를 겪는 코드와 원인ㅇ르 제공한 코드가 물리적으로 상당히 떨어져 있을 가능성이 커진다.

그렇기 때문에 제네릭을 사용해 다른 인스턴스를 추가할 경우 컴파일러가 경고를 내뱉도록 해야한다.

```java
Collection<Stamp> stamps = new ArrayList<>(); 
```

이렇게 선언하고 `Coin`의 인스턴스를 넣을 경우 `Required Type : Stamp`라는 컴파일 에러가 발생한다.

컴파일러는 컬렉션에서 원소를 꺼내는 모든 곳에 보이지 않는 형변환을 추가하여 절대 실패하지 않음을 보장한다.

#### 왜 존재할까?

로 타입을 쓰는걸 언어 차원에서 막아 놓지는 않았지만, 절대로 사용해서는 안 된다.
**로 타입을 쓰면 제네릭이 안겨주는 안전성과 표현력을 모두 잃게 된다.**

그러면 쓸모도 없는 로 타입을 왜 만들어놓은 것일까? 자바가 제네릭을 받아들이기까지 거의 10년이 걸렸다.
그 과정에서 제네릭 없이 짠 코드가 이미 세상을 뒤 엎은 상태였고, 모든 코드를 수용하면서 제네릭을 사용하는 새로운 코드와도 맞물려 돌아가게 해야만 했다.


#### List VS List<Object>

`List`는 제네릭 타입에서 완전히 발을 뺀 것이고, `List<Object>`는 모든 타입을 허용한다는 의사를 컴파일러에게 명확히 전달한 것이다.

`List<Object>` 같은 매개변수화 타입을 사용할 때와 달리 `List` 같은 로 타입을 사용하면 타입 안전성을 잃게 된다.

```java
public class TestCode {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0);
    }

    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }
}
```

이 코드는 컴파일은 되지만 로 타입인 `List`를 사용하였고, 값을 호출한 부분에서 `Integer`를 `String`으로 변환하려 시도했기 때문에 다음과 같은 경고가 발생한다.

```
Exception in thread "main" java.lang.ClassCastException: class java.lang.Integer cannot be cast to class
```

`unsafeAdd()`의 첫 번째 매개변수를 `List<Object>`로 바꾸면 컴파일조차 되지 않는다.

이쯤 되면 포기할 법도 한데, 원소의 타입을 몰라도 되는 로 타입을 쓰고싶어질 수 있다고 한다.

다음 코드를 보면 원소의 타입을 몰라도 정상적으로 동작하는 것을 알 수 있다.

```java
public class TestCode {
    public static void main(String[] args) {
        Set<String> s1 = new HashSet<>();
        s1.add("123");
        s1.add("234");

        Set<String> s2 = new HashSet<>();
        s2.add("23");
        s2.add("234");

        System.out.println(numElementInCommon(s1, s2));

    }

    private static int numElementInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1) {
            if(s2.contains(o1)) result++;
        }
        return result;
    }
}
```

동작은 하지만 로 타입을 사용해 안전하지는 않은 코드이다.
따라서 비한정적 와일드 카드 타입(unbounded wildcard type)을 대신 사용하는 게 좋다.
제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 물음표를 사용하자.

```java
private static int numElementInCommon(Set<?> s1, Set<?> s2){ ... }
```

비한정적 와일드카드 타입과 로 타입은 어떤 차이가 있을까?

와일드카드 타입은 안전하고, 로 타입은 안전하지 않다. 로 타입 컬렉션에는 아무 원소나 넣을 수 있으니 타입 불변식을 훼손하기 쉽다.
반면, `Collection<?>`에는 `null` 외에는 어떤 원소도 넣을 수 없다.
다른 원소를 넣으려 하면 컴파일할 때 오류를 내뿜는다.

```java
private static int numElementInCommon(Set<?> s1, Set<?> s2) {
    s2.add("123"); // 에러 발생!
    ...
}
```

```
Required type: capture of ?
```

`s1`은 원본을 참조하고 있는 매개변수이다. 그렇기에 외부 변수를 캡쳐해서 사용하고 있기에 이러한 변수를 수정하려고 하면 `capture of ?`에러가 발생하게 된다.
만약 `Set s2`와 같이 로 타입으로 받았다면 이와 같은 에러 없이 값을 넣었을 것이다.

이러한 부분에서 로 타입과 비한정적 와일드카드 타입의 차이점을 명확히 알 수 있다.

## 이 외 소소한 예외

### 클래스 리터럴에는 로타입을 써야 한다.

자바 명세는 class 리터럴에 매개변수화 타입을 사용하지 못하게 했다.
단, 배열과 기본 타입은 허용한다.

```
허용 O : List.class, String[].class, int.class
허용 X : List<String>.class, List<?>.class
```

```java
// 허용 O
Class<String[]> arrayClass = String[].class;
// 허용 X
Class<List<String>> listClass = List<String>.class;
```

### instanceof 연산자

런타임에는 제네릭 타입의 정보가 지워지기 때문에 `instanceof` 연산자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다.

로 타입이든 비한정적 와일드카드 타입이든 `instanceof`는 완전히 똑같이 작동한다.

```java
private static boolean isInstnceOf(Set o) {
    if (o instanceof Set<?>) {
        Set<?> s = (Set<?>) o;
        return true;
    }
    return false;
}
```

비한정적 와일드카드 타입의 꺽쇠괄호와 물음표는 아무런 역할 없이 코드만 지저분하게 만드므로, 차라리 로 타입을 쓰는 편이 깔끔하다.

## 정리

- 로 타입을 사용하면 런타임에 예외가 일어날 수 있으니 사용하지 말자.
- 로 타입은 제네릭이 도입되기 이전 코드와의 호환성을 위해 제공될 뿐이다.
- 로 타입인 `Set`은 제네릭 타입 시스템에 속하지 않는다.
  - `Set<Object>`는 어떤 타입의 객체도 저장할 수 있는 매개변수화 타입
  - `Set<?>`는 모종의 타입 객체만 저장할 수 있는 와일드카드 타입
  - `Set<Object>`, `Set<?>`은 안전하지만 로 타입인 `Set`은 안전하지 않다.