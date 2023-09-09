package ka.chapter3.item12.phone;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class PhoneTest {

    @Test
    void postToStringTest() {
        Phone p = new Phone("010-1234-1234");

        System.out.println(p.toString());
    }

    @Test
    void contactMapTest() {
        Map<String, Phone> map = new HashMap<>();
        map.put("준영", new Phone("010-1234-1234"));

        System.out.println(map);
    }

    @Test
    void phoneGetTest() {
        Phone p = new Phone("010-1234-1234");
        String[] split = p.toString().split("-");
        int areaCode = Integer.parseInt(split[0]);
        int prefix = Integer.parseInt(split[1]);
        int lineNum = Integer.parseInt(split[2]);
    }

}
