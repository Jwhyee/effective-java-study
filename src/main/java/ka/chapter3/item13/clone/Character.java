package ka.chapter3.item13.clone;

public class Character extends Member implements Cloneable {
    String name;
    String job;

    public Character(String name, String job) {
        super(name);
        this.name = name;
        this.job = job;
    }


    @Override
    public Character clone() {
        try {
            Character clone = (Character) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
