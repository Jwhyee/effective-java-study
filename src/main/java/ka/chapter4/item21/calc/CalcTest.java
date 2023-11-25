package ka.chapter4.item21.calc;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

public class CalcTest {

    @Test
    void calcTest() {
        EngineeringCalc ec = new EngineeringCalc();
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(ec.minus(10, 20)).isEqualTo(-10);
            softAssertions.assertThat(ec.sum(10, 20)).isEqualTo(30);
            softAssertions.assertThat(ec.multiple(10, 20)).isEqualTo(200);
        });
    }
}
