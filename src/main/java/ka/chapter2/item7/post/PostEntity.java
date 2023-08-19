package ka.chapter2.item7.post;

public class PostEntity {
    int id;
    String title, content;

    public PostEntity(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "PostEntity{" +
                "id=" + id +
                '}';
    }
}