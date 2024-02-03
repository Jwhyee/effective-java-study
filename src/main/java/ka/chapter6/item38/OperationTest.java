package ka.chapter6.item38;

import java.util.Collection;
import java.util.EnumSet;

public class OperationTest {
    public static void main(String[] args) {
        double x = 5;
        double y = 10;
        test(ExtendedOperation.class, x, y);
//        Operation basicOp = BasicOperation.valueOf(args[0]);
//        double result = basicOp.apply(10, 20);
//        System.out.println(result);
    }

    private static void test(Collection<? extends Operation> opSet,
                             double x, double y) {
        for (Operation op : opSet) {
            System.out.printf("%f %s %f = %f\n",
                    x, op, y, op.apply(x, y));
        }
    }

    private static <T extends Enum<T> & Operation> void test(
            Class<T> opEnumType, double x, double y
    ) {
        for (Operation op : opEnumType.getEnumConstants()) {
            System.out.printf("%f %s %f = %f\n",
                    x, op, y, op.apply(x, y));
        }
    }
}
