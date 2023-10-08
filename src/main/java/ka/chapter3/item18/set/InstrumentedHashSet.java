package ka.chapter3.item18.set;

import java.util.Collection;
import java.util.HashSet;

public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet() {}

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (containsOnlyStrings(c)) {
            for (E e : c) {
                if(!((String) e).contains("sec")) return false;
            }
        }
        addCount += c.size();
        return super.addAll(c);
    }

    private boolean containsOnlyStrings(Collection<?> collection) {
        for (Object item : collection) {
            if (!(item instanceof String)) {
                return false;
            }
        }
        return true;
    }


    public int getAddCount() {
        return addCount;
    }
}
