package ka.chapter2.item4.factory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeProviderImpl implements DateTimeProvider {
    private static final int SEC = 60;
    private static final int MIN = 60;
    private static final int HOUR = 24;
    private static final int DAY = 30;
    private static final int MONTH = 12;

    @Override
    public String getPassedTime(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        long curTime = System.currentTimeMillis();
        long regTime = instant.toEpochMilli();
        long diffTime = getDiffTime(curTime, regTime);

        if (diffTime < SEC) {
            return diffTime + "초 전";
        } else if ((diffTime /= SEC) < MIN) {
            return diffTime + "분 전";
        } else if ((diffTime /= MIN) < HOUR) {
            return diffTime + "시간 전";
        } else if ((diffTime /= HOUR) < DAY) {
            return diffTime + "일 전";
        } else if ((diffTime /= DAY) < MONTH) {
            return diffTime + "달 전";
        } else {
            return diffTime + "년 전";
        }
    }

    private long getDiffTime(long curTime, long regTime) {
        return (curTime - regTime) / 1000;
    }
}
