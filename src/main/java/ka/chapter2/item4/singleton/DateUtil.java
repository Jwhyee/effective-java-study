package ka.chapter2.item4.singleton;

import java.time.LocalDateTime;

public abstract class DateUtil {

    protected DateUtil() {
        // 기본 생성자가 만들어지는 것을 막는다(인스턴스화 방지용)
        throw new AssertionError();
    }

    public static String currentTime() {
        return LocalDateTime.now().toString();
    }

}
