package ka.chapter5.item31;

import ka.chapter5.item29.Stack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Item31Test {
    @Test
    void stackTest() {
        Stack<Number> numberStack = new Stack<>();
        Iterable<Integer> integers = List.of(1, 2, 3, 4);
        numberStack.pushAll(integers);
    }

    @Test
    void stackPopAllTest() {
        Stack<Number> numberStack = new Stack<>();
        Collection<Object> objects = List.of(1, 2, 3, 4, 5);
        numberStack.popAll(objects);
    }
}
