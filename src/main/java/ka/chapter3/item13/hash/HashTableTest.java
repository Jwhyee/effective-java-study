package ka.chapter3.item13.hash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashTableTest {
    @Test
    void hashTestCloneTest() {
        HashTable original = new HashTable();
        original.put(1, "Effective");
        original.put(2, "Java");

        HashTable clone = original.clone();
        clone.put(2, "Kotlin");

        assertTrue(original.get(1).equals("Java"));
    }
}
