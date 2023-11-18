package ka.chapter4.item26;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestCode {
    public static void main(String[] args) {
        Set<String> s1 = new HashSet<>();
        s1.add("abc");
        s1.add("bcd");

        System.out.println(isInstnceOf(s1));
    }

    private static boolean isInstnceOf(Set o) {
        if (o instanceof Set<?>) {
            Set<?> s = (Set<?>) o;
            return true;
        }
        return false;
    }
}
