package ka.chapter5.item28.choose;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ChooseTest {
    static class Post {
        int id;
        String title, content;

        public Post(int id, String title, String content) {
            this.id = id;
            this.title = title;
            this.content = content;
        }
    }

    private List<Post> initPost() {
        List<Post> pl = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pl.add(
                    new Post(i, "title%d", "content%s".formatted(i))
            );
        }
        return pl;
    }

    @Test
    void chooseInstanceTest() {
        Chooser c = new Chooser(initPost());
        Object o = c.choose();
        if (o instanceof Object) {
            Post p = (Post) o;
            System.out.println("TRUE : " + p.id);
        }
    }

    @Test
    void chooseTest() {
        Chooser c = new Chooser(List.of(1, 2, 3, 4, 5, 6, 7, 8));
        System.out.println(c.choose());

        c = new Chooser(List.of("Hello", "World", "Java"));
        System.out.println(c.choose());
    }

    @Test
    void chooseGenericTest() {
        List<Integer> list = new ArrayList<>();
        ChooserGeneric<Number> cg = new ChooserGeneric<>(list);
    }
}
