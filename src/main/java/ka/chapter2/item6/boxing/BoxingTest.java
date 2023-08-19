package ka.chapter2.item6.boxing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoxingTest {
    long startTime, endTime;

    @BeforeEach
    void startTimeCheck() {
        startTime = System.nanoTime();
    }

    @AfterEach
    void endTimeCheck() {
        endTime = System.nanoTime();

        long totalNanoTime = endTime - startTime;
        double timeMillis = (double) totalNanoTime / 1000000.0;
        System.out.println("timeMillis = " + timeMillis + "ms");
    }

    @Test
    void longBoxingTest1() {
        Long sum = 0L;
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }

        assertTrue(sum == 2305843005992468481L);
    }

    @Test
    void longBoxingTest2() {
        long sum = 0L;
        for (long i = 0; i < Integer.MAX_VALUE; i++) {
            sum += i;
        }

        assertTrue(sum == 2305843005992468481L);
    }
}
