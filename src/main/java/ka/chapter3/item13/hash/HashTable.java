package ka.chapter3.item13.hash;

public class HashTable implements Cloneable {
    private Entry[] buckets;
    private int size;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public HashTable() {
        buckets = new Entry[DEFAULT_INITIAL_CAPACITY];
        size = 0;
    }

    private static class Entry {
        final Object key;
        Object value;
        Entry next;

        public Entry(Object key, Object value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        Entry deepCopy() {
            Entry result = new Entry(key, value, next);
            for (Entry p = result; p.next != null; p = p.next) {
                p.next = new Entry(p.next.key, p.next.value, p.next.next);
            }
            return result;
        }
    }

    public Object get(int idx) {
        return buckets[idx].value;
    }

    public void put(Object key, Object value) {
        boolean flag = false;
        for (Entry bucket : buckets) {
            if (bucket != null && bucket.key == key) {
                bucket.value = value;
                flag = true;
            }
        }
        if (!flag) {
            buckets[size++] = new Entry(key, value, null);
        }
    }

    @Override
    public HashTable clone() {
        try {
            HashTable result = (HashTable) super.clone();
            result.buckets = new Entry[buckets.length];
            for (int i = 0; i < buckets.length; i++) {
                if (buckets[i] != null) {
                    result.buckets[i] = buckets[i].deepCopy();
                }
            }
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
