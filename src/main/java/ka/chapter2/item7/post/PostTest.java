package ka.chapter2.item7.post;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostTest {
    @Test
    void saveTest1 () {
        // null을 처리하는 경우
        PostService service = PostService.INSTANCE;
        PostDto dto = new PostDto("Hello", "world!");

        PostEntity entity = service.savePost(dto);

        dto = null;

        assertTrue(entity.title.equals("Hello"));
    }

    @Test
    void saveTest2 () {
        // 유효 범위 밖으로 밀어내는 경우
        PostService service = PostService.INSTANCE;
        PostDto dto = new PostDto("Hello", "world!");

        PostEntity entity = service.savePost(dto);

        dto = null;

        assertTrue(entity.title.equals("Hello"));
    }
}
