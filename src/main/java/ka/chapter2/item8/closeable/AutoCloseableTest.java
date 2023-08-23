package ka.chapter2.item8.closeable;

import ka.chapter2.item8.finalizer.Util;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoCloseableTest {
    void makeObject() throws Exception {
        for (int i = 1; i <= 100000; i++) {
            Obj p = new Obj(i);
            p.close();

            if(!p.isClose()) {
                System.out.println(p);
            }
        }
    }
    @Test
    void autoCloseableTest() throws Exception {
        Util.cnt = 0;
        makeObject();
        System.out.println("Util.cnt = " + Util.cnt);
        assertTrue(Util.cnt == 100000);
    }
}
