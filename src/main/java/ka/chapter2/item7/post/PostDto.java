package ka.chapter2.item7.post;

public class PostDto {
    int id;
    String title, content;

    public PostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
