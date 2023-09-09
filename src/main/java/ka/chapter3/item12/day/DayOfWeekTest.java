package ka.chapter3.item12.day;

import org.junit.jupiter.api.Test;

public class DayOfWeekTest {
    @Test
    void enumToStringTest() {
        for (DayOfWeek value : DayOfWeek.values()) {
            System.out.println(value.toString());
        }
    }
}
