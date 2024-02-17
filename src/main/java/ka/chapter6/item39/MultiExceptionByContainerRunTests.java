package ka.chapter6.item39;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MultiExceptionByContainerRunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName("ka.chapter6.item39.SampleTest4");
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(MultiExceptionContainer.class)
                    || m.isAnnotationPresent(MultiExceptionTestByContainer.class)
            ) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (Throwable wrappedExc) {
                    StringBuilder sb = new StringBuilder();
                    Throwable exc = wrappedExc.getCause();
                    int oldPassed = passed;
                    MultiExceptionTestByContainer[] excTests =
                            m.getAnnotationsByType(MultiExceptionTestByContainer.class);

                    for (MultiExceptionTestByContainer excTest : excTests) {
                        sb.append(excTest.value().getSimpleName());
                        sb.append(", ");
                        if (excTest.value().isInstance(exc)) {
                            passed++;
                            break;
                        }
                    }
                    if (passed == oldPassed) {
                        String str = sb.substring(0, sb.length() - 2);
                        System.out.printf("""
                                Test Fail : %s()
                                 - expected : [%s]
                                 - actual : %s
                                 - cause : %s %n
                                """.trim(),
                                m.getName(),
                                str,
                                exc.getClass().getSimpleName(),
                                exc.getMessage());
                    }
                }
            }
        }

        System.out.printf("성공 : %d, 실패 : %d%n",
                passed, tests - passed);

    }
}
