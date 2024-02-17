package ka.chapter6.item39;

public class SampleTest {
    @Test
    public static void test1() { }

    public static void test2() {
        throw new RuntimeException("BOOM");
    }

    @Test
    void test3() { }

    @ExceptionTest(RuntimeException.class)
    public static void test4() {
        throw new RuntimeException("FAIL TEST");
    }
}
