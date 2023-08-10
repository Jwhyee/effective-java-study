package ka.chapter2.item5.supplier;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.function.Supplier;

public class SupplierTest {
    @Test
    void supplierTest() {
        Supplier<Integer> randomSupplier = () -> {
            int num = 100;
            return new Random().nextInt(num + 1);
        };

        for (int i = 0; i < 3; i++) {
            int random = randomSupplier.get();
            System.out.println("random = " + random);
        }
    }

    @Test
    void randomTest() {
        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            int num = random.nextInt(100);
            System.out.println("num = " + num);
        }
    }
}
