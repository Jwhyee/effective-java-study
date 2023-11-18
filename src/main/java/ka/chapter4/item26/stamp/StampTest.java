package ka.chapter4.item26.stamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class StampTest {
    static class Coin {
        public void print() {
            System.out.println("Coin.print");
        }
    }

    static class Stamp {
        public void print() {
            System.out.println("Stamp.print");
        }
    }

    public static void main(String[] args) {
        // Stamp 인스턴스만 취급한다.
        Collection<Stamp> stamps = new ArrayList<>();

        stamps.add(new Stamp());
//        stamps.add(new Coin());

        for (Iterator i = stamps.iterator(); i.hasNext();) {
            Stamp s = (Stamp) i.next();
            s.print();
        }
    }
}
