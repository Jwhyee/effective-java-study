package ka.chapter4.item15.com.project;

import org.junit.jupiter.api.Test;

public class MostImportantTest {
    public String veryVeryImportantInfo = "hello world";
    @Test
    void importantTest() {
        System.out.println(veryVeryImportantInfo);
    }
}
