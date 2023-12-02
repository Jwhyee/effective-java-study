package ka.chapter4.item17.functional;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class StreamFunctionalTest {

    @Test
    void integerToStringMap() {
        int[][] arr = {{1, 0, 0, 0},
                        {1, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 1, 1}
        };

        Arrays.stream(arr)
                .map(Arrays::toString)
                .forEach(System.out::println);
    }

}
