package ka.chapter7.item43;

import java.util.HashMap;
import java.util.Map;

public class MethodReferenceTest {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("KEY", 1);
        map.put("KEY", 2);
        System.out.println(map);
        map.merge("KEY", 1, (cnt, incr) -> cnt + incr);
        System.out.println(map);
        map.merge("KEY", 1, Integer::sum);
    }
}
