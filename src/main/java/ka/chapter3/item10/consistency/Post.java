package ka.chapter3.item10.consistency;

import java.io.Serializable;

public class Post implements Serializable {
    private int id;
    private String title, content;
    private transient Member writer;

    public Post(String title, String content, Member writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Member getWriter() {
        return writer;
    }
}
