package ka.chapter3.item15.search;

import java.io.Serializable;

public class Node implements Serializable {
    transient int x, y;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
