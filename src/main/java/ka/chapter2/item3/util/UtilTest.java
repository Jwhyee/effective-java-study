package ka.chapter2.item3.util;

import org.junit.jupiter.api.Test;

import java.io.*;
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

    @Test
    void serializeTest() {
        // 직렬화할 파일 경로
        String filePath = "dateTimeUtil.ser";

        // 객체 생성
        DateTimeUtil original = DateTimeUtil.getInstance();

        // 객체를 파일에 직렬화
        serializeToFile(original, filePath);

        // 파일로부터 객체 역직렬화
        DateTimeUtil deserialized = deserializeFromFile(filePath);

        // 역직렬화된 객체 사용
        String passedTime = deserialized.getPassedTime(LocalDateTime.now().minusMinutes(2));
        System.out.println("passedTime = " + passedTime);

        assertTrue(original == deserialized);
    }

    // 객체를 파일에 직렬화하는 메소드
    private static void serializeToFile(Object object, String filePath) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(object);
            System.out.println("Object serialized to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일로부터 객체 역직렬화하는 메소드
    private static DateTimeUtil deserializeFromFile(String filePath) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            DateTimeUtil deserialized = (DateTimeUtil) inputStream.readObject();
            System.out.println("Object deserialized from " + filePath);
            return deserialized;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
