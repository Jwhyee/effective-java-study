package ka.chapter4.item17.complex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class ComplexTest {

    @Nested
    class FinalClass {

        @Test
        @DisplayName("처음 생성된 객체와 연산 후의 객체가 동일하지 않다.")
        void equalsTest() {
            Complex origin = new Complex(1.0, 12.0);
            Complex target = new Complex(1.0, 1.0);
            Complex plus = origin.plus(target);

            assertSoftly(softAssertions -> {
                assertThat(origin).isNotEqualTo(plus);
                assertThat(origin.realPart()).isNotEqualTo(plus.realPart());
            });
        }
    }


}
