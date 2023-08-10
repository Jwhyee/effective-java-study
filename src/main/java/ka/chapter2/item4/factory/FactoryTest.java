package ka.chapter2.item4.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FactoryTest {
    @Test
    @DisplayName("싱글톤 정적 팩토리 테스트")
    void factoryTest1() {
        DateTimeProvider factory = DateTimeProviderFactory.getInstance();
        String passedTime = factory.getPassedTime(LocalDateTime.now());
        System.out.println(passedTime);
        assertTrue(passedTime.equals("0초 전"));
    }

    @Test
    @DisplayName("프로바이더 객체 생성 테스트")
    void factoryTest2() {
        DateTimeProviderImpl impl = new DateTimeProviderImpl();
        String passedTime = impl.getPassedTime(LocalDateTime.now().minusMinutes(2));
        System.out.println(passedTime);
        assertTrue(passedTime.equals("2분 전"));
    }
}
