package ka.chapter2.item8.finalizer;

public class Post {
    private int id;
    private String title, content;

    public Post(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 생성");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 소멸");
        if (id == 50000) {
            throw new RuntimeException("에러 ㅅㄱ");
        }
        Util.cnt++;
        super.finalize();
    }
}
