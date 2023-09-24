package ka.chapter3.item13.clone;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloneTest {
    @Test
    void objectCloneTest() {
        try {
            Method objectCloneMethod = Object.class.getDeclaredMethod("clone");
            objectCloneMethod.setAccessible(true);

            Object originalObject = new Object();
            Object cloneObject = objectCloneMethod.invoke(originalObject);
            long originHash = System.identityHashCode(originalObject);
            long cloneHash = System.identityHashCode(cloneObject);

            assertTrue(originHash == cloneHash);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void customCloneTest() {
        Character warrior = new Character("타락파워전사", "전사");
        Character clone = warrior.clone();

        System.out.println(clone.job);
    }
}
