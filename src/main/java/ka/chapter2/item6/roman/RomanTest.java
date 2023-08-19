package ka.chapter2.item6.roman;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RomanTest {

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
    @Order(2)
    void romanTest1() {
        System.out.println("s.matches");
        boolean result = RomanNumeral.isRomanNumeral("IX");
        assertTrue(result);
    }

    @Test
    @Order(1)
    void romanTest2() {
        System.out.println("Pattern");
        boolean result = RomanNumeral.isRomanNumeralByPattern("IX");
        assertTrue(result);
    }
}
