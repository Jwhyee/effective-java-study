package ka.chapter2.item8.finalizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinalizerTest {

    void makeObject()  {
        for (int i = 1; i <= 100000; i++) {
            Post p = new Post(i, "title", "content");
        }
    }

    @Test
    void postFinalizeTest1() throws InterruptedException {
        makeObject();
        Thread.sleep(10000);
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }

    @Test
    void postFinalizeTest2() throws InterruptedException {
        Util.cnt = 0;
        makeObject();
        System.gc();
        System.runFinalization();
        Thread.sleep(10000);
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }

    @Test
    void autoCloseableTest() {
        Util.cnt = 0;
        makeObject();
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }

}
