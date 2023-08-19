package ka.chapter2.item7.post;

import java.util.LinkedList;
import java.util.List;

public class PostService {
    public static final PostService INSTANCE = new PostService();
    private PostService() {

    }

    List<PostEntity> repository = new LinkedList<>();

    public PostEntity savePost(PostDto dto) {
        PostEntity entity = new PostEntity(repository.size() + 1, dto.title, dto.content);
        repository.add(entity);
        return entity;
    }

    public void addAllById(int id, PostDto... arr) {
        // 여러 Dto 중에 해당되는 id에 대해서만 저장
        for (int i = 0; i < arr.length; i++) {
            PostEntity entity = new PostEntity(arr[i].id, arr[i].title, arr[i].content);
            if (arr[i].id == id) {
                repository.add(entity);
            }
        }

        // 위 for문이 끝나면 쓸데없이 만들어졌던 객체들(참조 되지 않는 객체들)이 더 이상 접근되지 않음
    }
}
