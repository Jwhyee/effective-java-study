package ka.chapter2.item7.stack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StackTest {
    @Test
    void stackNullTest() {
        Stack stack = new Stack();
        stack.push(new Node(0, 0));
        stack.push(new Node(0, 1));

        Node pop = (Node) stack.pop();
        Node object = (Node) stack.getByIndex(1);

        // 테스트 성공!
        assertTrue(object.y == 1);
    }

    static class Node {
        int x, y;
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
