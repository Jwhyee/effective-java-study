package ka.chapter2.item4.factory;

import java.time.LocalDateTime;

public interface DateTimeProvider {
    String getPassedTime(LocalDateTime localDateTime);
}
