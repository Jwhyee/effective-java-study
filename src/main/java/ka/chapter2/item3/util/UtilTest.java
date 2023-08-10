package ka.chapter2.item3.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTest {

    @Test
    void utilTest1() {
        // 테스트 실패!
        // 메소드가 static이 아니기 때문에 컴파일 에러 발생!
        // String time = DateTimeUtil.getPassedTime(LocalDateTime.now().minusHours(2));
        // System.out.println(time);
        // assertTrue(time.equals("2시간 전"));
    }

    @Test
    void utilTest2() {
        // 테스트 성공!
        // 정상 접근 가능!
        // DateTimeUtil util = DateTimeUtil.INSTANCE;
        // String time = util.getPassedTime(LocalDateTime.now().minusHours(2));
        // System.out.println(time);
        // assertTrue(time.equals("2시간 전"));
    }

    @Test
    void utilTest3() {
        // 테스트 성공!
        DateTimeUtil util = DateTimeUtil.getInstance();
        String time = util.getPassedTime(LocalDateTime.now().minusHours(2));
        System.out.println(time);
        util.showCurrentTime();
        assertTrue(time.equals("2시간 전"));
    }

    @Test
    void utilReflectionTest() {
        try {
            Constructor<DateTimeUtil> constructor = DateTimeUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            DateTimeUtil util = constructor.newInstance();

            util.showCurrentTime();

        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void utilSupplierTest1() {
        Supplier<DateTimeUtil> dateTimeUtilSupplier = DateTimeUtil.getDateTimeUtilSupplier();

        DateTimeUtil dateTimeUtil1 = dateTimeUtilSupplier.get();
        DateTimeUtil dateTimeUtil2 = dateTimeUtilSupplier.get();

        assertTrue(dateTimeUtil1 == dateTimeUtil2);
    }

    @Test
    void utilSupplierTest2() {
        Supplier<String> dateTimeUtilSupplier = () -> DateTimeUtil.getCurrentTime(LocalDateTime.now());

        String time1 = dateTimeUtilSupplier.get();
        System.out.println("time1 = " + time1);
        String time2 = dateTimeUtilSupplier.get();
        System.out.println("time2 = " + time2);

        assertTrue(time1.equals(time2));
    }

    @Test
    void compareTimeTest() {
        LocalDateTime dateTime = LocalDateTime.now();
        String time1 = dateTime.toString();
        System.out.println("time1 = " + time1);
        String time2 = dateTime.toString();
        System.out.println("time2 = " + time2);

        assertTrue(time1.equals(time2));
    }
}
