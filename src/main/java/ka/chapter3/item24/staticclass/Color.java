package ka.chapter3.item24.staticclass;

import java.util.HashSet;
import java.util.Set;

public class Color {

    private static Set<Pos> posSet = new HashSet<>();

    public Color() {}

    public void addPos(int x, int y) {
        new Pos(x, y);
    }

    static class Pos {
        final int x, y;

        Pos(int x, int y) {
            this.x = x;
            this.y = y;
            posSet.add(this);
        }
    }
}
