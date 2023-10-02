package ka.chapter3.item15.singleton;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SingletonTest {
    @Test
    void firstSingletonTest() {
        Singleton s = Singleton.getInstance();
        assertTrue(s.instanceId == 1);

//        s.instanceId = 10;

    }

    @Test
    void secondSingletonTest() {
        Singleton s = Singleton.getInstance();
        assertTrue(s.instanceId == 1);
    }
}
