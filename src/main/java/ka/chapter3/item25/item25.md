# 톱 클래스는 한 파일에 하나만 담으라.

한 소스 파일에 톱레벨 클래스를 여러 개 선언하더라도 컴파일 시 문제되지 않는다.

하지만 아래와 같이 여러가지 큰 문제점이 생길 수 있다.

- 한 클래스를 여러 가지로 정의할 수 있게 된다.
- 그 중 어느 것을 사용할지는 어느 소스 파일을 먼저 컴파일하느냐에 따라 달라진다.

한 마디로, 사과를 양파랑 동일하다고 정의하는 것과 같은 것이다.

## 클래스 정의

아래 클래스를 보면 `Utensil`과 `Dessert`이라는 톱레벨 클래스가 2개 정의되어 있다. 

```java
// Utensil.java
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```

위 클래스를 통해 테스트를 돌려보면 `pancake`가 나오는 것을 알 수 있다.

```java
public class TestClass {
    @Test
    void utensilTest() {
        final String result = Utensil.NAME + Dessert.NAME;
        // 테스트 성공!
        assertThat(result).isEqualTo("pancake");
    }
}
```

만약 우연히 `Dessert.java` 클래스를 담은 파일을 만들었다고 가정하자.

```java
// Dessert.java
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```

클래스를 생성하는 순간부터 아래 사진과 같은 에러가 발생하게 된다.

<center>
<img width="812" alt="스크린샷 2023-10-24 오후 7 32 08" src="https://github.com/Jwhyee/effective-java-study/assets/82663161/0aca6a56-da08-4056-9538-5bdbef2acafc">
</center>

### 컴파일

```
Main.java == TestClass.java
javac src/main/java/ka/chapter3/item25/TestClass.java src/main/java/ka/chapter3/item25/Utensil.java
```

아래 명령어들을 통해 컴파일할 경우 어떤 결과가 나오는지 확인해보자.

```bash
# 컴파일 성공
javac Main.java Dessert.java
```

```bash
# 컴파일 실패
javac Main.java
```

```
src/main/java/TestClass.java:5: error: cannot find symbol
        final String result = Utensil.NAME + Dessert.NAME;
                              ^
  symbol:   variable Utensil
  location: class TestClass
src/main/java/TestClass.java:5: error: cannot find symbol
        final String result = Utensil.NAME + Dessert.NAME;
                                             ^
  symbol:   variable Dessert
  location: class TestClass
2 errors
```

위와 같이 에러가 발생하게 된다.

## 해결 방법

단순히 톱레벨 클래스들을 서로 다른 소스 파일로 분리하면 된다.

굳이 여러 톱레벨 클래스를 한 파일에 담고 싶다면, 정적 멤버 클래스를 사용하는 방법을 고민해볼 수 있다.

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
    
    private static class Utensil {
        static final String NAME = "pan";
    }

    private static class Dessert {
        static final String NAME = "cake";
    }
}
```

이렇게 부차적인 클래스라면 읽기 좋고, `private`로 선언하면 접근 범위도 취소로 관리할 수 있게 된다.

## 정리

- 소스 파일 하나에는 반드시 톱레벨 클래스(혹은 톱레벨 인터페이스)를 하나만 담자.
- 이 규칙만 따른다면, 컴파일러가 한 클래스에 대한 정의를 여러개 만들어 내는 일은 사라진다.
- 소스 파일을 어떤 순서로 컴파일하든 바이너리 파일이나 프로그램의 동작이 달라지는 일은 결코 일어나지 않을 것이다.