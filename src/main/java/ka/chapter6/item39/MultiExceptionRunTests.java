package ka.chapter6.item39;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MultiExceptionRunTests {
    public static void main(String[] args) throws Exception {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName("ka.chapter6.item39.SampleTest2");
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(MultiExceptionTestByArray.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    Class<? extends Throwable>[] excTypes =
                            m.getAnnotation(MultiExceptionTestByArray.class).value();

                    for (Class<? extends Throwable> excType : excTypes) {
                        if (excType.isInstance(exc)) passed++;
                        else System.out.printf("""
                                Test Fail : %s()
                                 - expected : %s
                                 - actual : %s
                                 - cause : %s %n
                                """.trim(),
                                m.getName(),
                                excType.getSimpleName(),
                                exc.getClass().getName(),
                                exc.getMessage());
                    }


                } catch (Exception exc) {
                    System.out.printf("Mistake : %s()%n", m.getName());
                }
            }
        }

        System.out.printf("성공 : %d, 실패 : %d%n",
                passed, tests - passed);

    }
}
