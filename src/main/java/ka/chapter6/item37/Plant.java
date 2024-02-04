package ka.chapter6.item37;

public class Plant {
    enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL;}

    public int getLifeCycle() {
        return lifeCycle.ordinal();
    }

    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}
