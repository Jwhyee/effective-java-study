package ka.chapter2.item7.cache;

import ka.chapter2.item7.post.PostEntity;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.WeakHashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheTest {
    @Test
    void cacheTest() {
        // 크기가 3인 LRU 캐시 생성
        LRUCache<String, PostEntity> cache = new LRUCache<>(3);

        // 1, 2, 3 추가 -> {one=1, two=2, three=3}
        cache.put("one", new PostEntity(1, "공지1", "내용1"));
        cache.put("two", new PostEntity(2, "공지2", "내용2"));
        cache.put("three", new PostEntity(3, "공지3", "내용3"));

        // 1 사용 -> {two=2, three=3, one=1}
        cache.get("one");

        // 4 추가 -> {three=3, one=1, four=4}
        cache.put("four", new PostEntity(4, "공지4", "내용4"));
        cache.remove("one");
        // 테스트 성공!
        assertTrue(cache.toString().equals("{three=PostEntity{id=3}, one=PostEntity{id=1}, four=PostEntity{id=4}}"));
    }

    @Test
    void weakHashMapCacheTest() {
        // WeakHashMap을 캐시로 사용
        Map<String, String> cache = new WeakHashMap<>();

        // 데이터를 생성하여 캐시에 추가
        String key1 = "key1";
        String value1 = "Value1";
        cache.put(key1, value1);

        // 캐시에서 데이터를 가져와 사용
        if (cache.containsKey(key1)) {
            String cachedValue = cache.get(key1);
            System.out.println("Cached Value: " + cachedValue);
        }

        // null을 이용한 참조 해제
        key1 = null;

        // 가비지 컬렉션 시도
        System.gc();

        // 일반적으로 WeakHashMap은 가비지 컬렉션 후에 참조가 사라진 항목을 자동으로 제거
        assertTrue(!cache.containsKey(key1));
    }
}
