package ka.chapter3.item14;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class ComparableTest {

    @Test
    void wordList() {
        String[] words = {"world", "banana", "apple"};
        Arrays.sort(words);
        System.out.println(Arrays.toString(words));
    }

}