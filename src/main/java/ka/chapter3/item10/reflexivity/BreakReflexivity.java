package ka.chapter3.item10.reflexivity;

public class BreakReflexivity {
    private int id;

    public BreakReflexivity(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return false;
        return true;
    }
}
