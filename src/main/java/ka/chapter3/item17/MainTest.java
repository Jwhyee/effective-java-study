package ka.chapter3.item17;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

import static org.assertj.core.api.Assertions.assertThat;

public class MainTest {

    @Test
    void bigIntegerTest() {
        BigInteger moby = new BigInteger("1000000");

        moby = moby.flipBit(0);

        System.out.println(moby);
    }

    @Test
    void bitSetTest() {
        BitSet moby = new BitSet(1000000);

        moby.flip(0);

        System.out.println(moby);
    }

    @Test
    void bigIntegerReflectionTest() {
        try {
            BigInteger bi = new BigInteger("-1000000");
            int signum = bi.signum();
            Field field = bi.getClass().getDeclaredField("mag");
            field.setAccessible(true);

            System.out.println(field);
            System.out.println(signum);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
