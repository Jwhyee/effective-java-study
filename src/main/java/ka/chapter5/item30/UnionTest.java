package ka.chapter5.item30;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class UnionTest {
    @Test
    void unionTest() {
        Set<Node> nodeSet = Set.of(new Node(1, 2), new Node(3, 4));
        Set<Integer> integerSet = Set.of(1, 2, 3, 4);
        Set union1 = UnionTest.union1(nodeSet, integerSet);
        System.out.println(union1);

        Set<Integer> integerSet1 = Set.of(1, 2, 3, 4);
        Set<Integer> integerSet2 = Set.of(5, 6, 7, 8);
        Set<Integer> union2 = union2(integerSet1, integerSet2);
        System.out.println(union2);

    }
    public static Set union1(Set s1, Set s2) {
        Set result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }
    public static <E> Set<E> union2(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    static class Node {
        int x, y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
