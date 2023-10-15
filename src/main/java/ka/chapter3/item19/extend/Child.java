package ka.chapter3.item19.extend;

public class Child extends Parent {

    private final String name;

    public Child(String pName, String cName) {
        super(pName);
        this.name = cName;
    }
}
