package ka.chapter7.item45;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {
        List<String> strList = List.of("hello", "hell", "hel", "he", "h");
        Stream<String> hell = strList.stream()
                .filter(it -> it.startsWith("hell"));
    }
}
