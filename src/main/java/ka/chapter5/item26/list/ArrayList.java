package ka.chapter5.item26.list;

public class ArrayList<T> {
    private Object[] datum;
    private int length;

    public ArrayList() {
        datum = new Object[10];
        length = 0;
    }

    public void add(T data) {
        datum[length++] = data;
    }
}
