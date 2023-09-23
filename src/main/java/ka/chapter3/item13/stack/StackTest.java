package ka.chapter3.item13.stack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StackTest {
    @Test
    void stackCloneTest() {
        Stack originalStack = new Stack();
        originalStack.push(20);
        originalStack.push(30);
        originalStack.push(40);

        Stack cloneStack = originalStack.clone();
        cloneStack.pop();

        assertTrue((Integer) originalStack.pop() == 40);
    }
}