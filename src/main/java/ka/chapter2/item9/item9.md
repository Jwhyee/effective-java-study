# try-finally 보다는 try-with-resources를 사용하라.

`item8`에서 공부한 것과 같이 `AutoCloseable`을 구현한 뒤,
`close()` 메소드를 통해 직접 닫아줘야 하는 자원이 많다.

## try-finally

```java
public class TryTest {

    private String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }

    @Test
    void tryFinallyTest() throws IOException {
        String line = firstLineOfFile("README.md");
        
        // 테스트 성공!
        assertTrue(line.equals("# EFFECTIVE JAVA 3/E STUDY"));
    }
}
```

위와 같이 자원을 사용하고, `finally`를 통해 자원을 해제할 수 있는 방법이 있다.
하지만, 자원을 하나 더 사용하면 어떻게 될까?

```java
public class TryTest {

    private final int BUFFER_SIZE = 20;

    private void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.write(buf, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    @Test
    void tryFinallyTest() throws IOException {
        copy("README.md", "PROGRESS.md");
    }
}
```

`try`를 이용해 사용한 `InputStream`, `OutputStream` 자원을 두 번의 `finally`를 통해 각각 닫아줘야 한다.

여기서 `try` 블록과 `finally` 블록 모두 예외가 발생할 수 있다.
기기에 물리적인 문제가 생기면, `firstLineOfFile()` 메소드 안의 `readLine()` 메소드가 예외를 던질 것이고,
같은 이유로 `close()` 또한 실패할 것이다.
이런 상황이라면 `close()`에서 발생한 예외가 첫 번째 예외를 집어삼켜 버린다.

## try-with-resources

앞서 `try-finally`에서 본 문제들은 자바 7이 투척한 `try-with-resources`를 통해 해결되었다.

이 구조를 사용하려면 해당 자원이 item8에서 공부했던 `AutoCloseable` 인터페이스를 구현해야한다.

간단하게 앞서 작성했던 코드를 리팩터링을 하면 아래 코드와 같다.

```java
public class TryTest {

    private final int BUFFER_SIZE = 20;

    private void copy(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        }
    }

    @Test
    void tryFinallyTest2() throws IOException {
        copy("README.md", "PROGRESS.md");
    }
}
```

이와 같이 코드를 작성하면 이전에 작성했던 코드보다 훨씬 읽기 쉽고, 문제를 진단하기도 수월하다.

앞서 `try-finally`는 두 번째 예외가 첫 번째로 발생한 예외를 아예 덮어버려 문제를 진단하기 어려웠는데,
`try-with-resources`는 덮는 것이 아닌 발생한 예외를 숨김(suppressed) 처리를 하여 스택 추적 내역에서도 확인할 수 있다.
또한, `Throwable`에 추가된 `getSuppressed()` 메소드를 통해 가져올 수도 있다.

보통의 `try-finally` 처럼 `try-with-resources` 역시 `catch` 절을 사용할 수 있다.

```java
public class TryTest {
    private String firstLineOfFile(String path, String defaultVal) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    @Test
    void tryFinallyTest() throws IOException {
        String line = firstLineOfFile("READM2.md", "NON-FILE");

        // 테스트 성공!
        assertTrue(line.equals("# EFFECTIVE JAVA 3/E STUDY"));
    }
}
```

`try`를 중첩해서 사용하지 않고도 다수의 예외를 처리할 수 있다.

## 정리

- 꼭 회수해야 하는 자원을 다룰 때는 `try-finally` 대신 `try-with-resources`를 사용하자.
- 예외 정보나, 코드의 가독성, 성능 등 모두 우수하기 때문에 훨씬 이득이다.