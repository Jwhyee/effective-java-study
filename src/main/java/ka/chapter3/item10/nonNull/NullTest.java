package ka.chapter3.item10.nonNull;

import ka.chapter3.item10.consistency.Post;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NullTest {
    @Test
    void objectArrayNullTest() {
        Post[] arr = new Post[5];

        assertTrue(arr[0].equals(null));
    }
}
