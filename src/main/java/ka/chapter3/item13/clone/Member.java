package ka.chapter3.item13.clone;

public class Member implements Cloneable {
    String name;

    public Member(String name) {
        this.name = name;
    }

    protected Member clone() throws CloneNotSupportedException {
        return new Member(name);
    }
}
