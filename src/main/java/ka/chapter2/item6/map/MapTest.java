package ka.chapter2.item6.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapTest {

    Map<Integer, String> fruitRepository;

    @BeforeEach
    void mapInit() {
        fruitRepository = new LinkedHashMap<>();
        fruitRepository.put(1, "사과");
        fruitRepository.put(2, "샤인머스켓");
        fruitRepository.put(3, "물복");
    }

    @Test
    void mapTest1() {
        Set<Integer> repoKeySetView1 = fruitRepository.keySet();
        repoKeySetView1.remove(1);
        Set<Integer> repoKeySetView2 = fruitRepository.keySet();
        assertTrue(repoKeySetView1 == repoKeySetView2);
    }
}
