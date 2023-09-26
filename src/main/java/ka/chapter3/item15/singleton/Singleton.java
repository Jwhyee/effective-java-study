package ka.chapter3.item15.singleton;

public class Singleton {
    private static final Singleton s = new Singleton();
    public final int instanceId = 1;

    private Singleton() {

    }

    public static Singleton getInstance() {
        return s;
    }
}
