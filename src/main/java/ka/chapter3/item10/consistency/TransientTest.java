package ka.chapter3.item10.consistency;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransientTest {
    @Test
    void test1() {
        Member member = new Member("tester");
        Post p = new Post("title", "content", member);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("post.ser"))) {
            out.writeObject(p);
            System.out.println(p.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("post.ser"))){
            Post restoredPost = (Post) in.readObject();

            // 테스트 성공!
            assertTrue(restoredPost.getWriter() == null);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
