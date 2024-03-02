package ka.chapter7.item44;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CacheTest {
    public static void main(String[] args) {
        Map<String, Integer> map = new LinkedHashMap<>();

        Map<String, Integer> cacheMap = new CacheHashMap<>();

        cacheMap.keySet().stream()
                .map(Integer::parseInt)
                .filter(it -> it == 0)
                .collect(Collectors.toSet());

    }
}

class CacheHashMap<K, V> extends LinkedHashMap<K, V> {
    private static final int MAX_ENTRIES = 100;
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > MAX_ENTRIES;
    }
}

@FunctionalInterface
interface EldestEntryRemovalFunction<K, V> {
    boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
}