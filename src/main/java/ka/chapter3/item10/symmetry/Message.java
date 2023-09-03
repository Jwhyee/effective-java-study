package ka.chapter3.item10.symmetry;

public class Message {
    private final String msg;

    public Message(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Message &&
                ((Message) o).msg.equalsIgnoreCase(msg);
    }
}
