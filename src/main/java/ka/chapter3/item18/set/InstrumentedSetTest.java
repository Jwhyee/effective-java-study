package ka.chapter3.item18.set;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InstrumentedSetTest {
    @Test
    void addAllTest() {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(List.of("가", "나", "다"));

        assertThat(s.getAddCount()).isEqualTo(3);
    }
}
