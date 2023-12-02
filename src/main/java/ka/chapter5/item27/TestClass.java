package ka.chapter5.item27;

import java.util.HashSet;
import java.util.Set;

public class TestClass {
    static class Node {
        int x, y;
    }
    public static void main(String[] args) {
        @SuppressWarnings("unchecked")
        Set<Node> nodeSet = new HashSet();
    }
}
