package ka.chapter4.item17.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// https://tecoble.techcourse.co.kr/post/2020-07-17-Functional-Interface/
// https://tecoble.techcourse.co.kr/post/2021-09-30-java8-functional-programming/
// https://m.blog.naver.com/PostView.naver?blogId=d_d_o_l_&logNo=223217461160&navType=by

public class Filter {

    @Test
    void test() {
        List<String> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            data.add(UUID.randomUUID().toString());
        }

//        final List<String> result1 = CustomFunctionalFactory.filter(data, (item) -> !item.equals("a"));
        final List<String> result1 = CustomFunctionalFactory.filter(data, (item) -> !item.contains("t"));
//        final List<String> result2 = CustomFunctionalFactory.map(data, (item) -> item.concat("^_^"));
        final List<String> result2 = CustomFunctionalFactory.map(data, (item) -> item.replace("0", "^_^"));

        System.out.println("-------result1-------");
        result1.forEach(System.out::println);
        System.out.println("-------result2-------");
        result2.forEach(System.out::println);


    }

}