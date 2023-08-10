package ka.chapter2.item4.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTest {
    @Test
    void staticUtilTest1() {
        StaticDateTimeUtil.localDateTime = LocalDateTime.now().minusHours(2);
        String passedTime = StaticDateTimeUtil.getPassedTime();
        System.out.println(passedTime);
        assertTrue(passedTime.equals("2시간 전"));
    }

    @Test
    void staticUtilTest2() {
        String passedTime = StaticDateTimeUtil.getPassedTime();
        System.out.println(passedTime);
        assertTrue(passedTime.equals("0초 전"));
    }

}
