package ka.chapter3.item14.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTest {
    @Test
    void firstConditionTest() {
        // 첫 번째 객체가 두 번째 객체보다 작으면, 두 번째가 첫 번째 보다 커야한다.
        Order o1 = new Order(1, 15000);
        Order o2 = new Order(2, 30000);
        assertTrue(o1.compareTo(o2) == -1);

        // 첫 번째가 두 번째보다 크면, 두 번째는 첫 번째보다 작아야한다.
        o1 = new Order(2, 30000);
        o2 = new Order(1, 15000);
        assertTrue(o1.compareTo(o2) == 1);

        // 첫 번째가 두 번째와 같다면, 두 번째는 첫 번째와 같아야한다.
        o1 = new Order(1, 15000);
        o2 = new Order(1, 15000);
        assertTrue(o1.compareTo(o2) == 0);
    }

    @Test
    void secondConditionTest() {
        // 첫 번째가 두 번째보다 크고, 두 번째가 세 번째보다 크면, 첫 번째는 세 번째보다 커야 한다.
        Order o1 = new Order(3, 45000);
        Order o2 = new Order(2, 30000);
        Order o3 = new Order(1, 15000);

        assertTrue(o1.compareTo(o2) == 1);
        assertTrue(o2.compareTo(o3) == 1);
        assertTrue(o1.compareTo(o3) == 1);
    }

    @Test
    void thirdConditionTest() {
        // 크기가 같은 객체들끼리는 어떤 객체와 비교하더라도 항상 같아야한다.
        Order o1 = new Order(1, 15000);
        Order o2 = new Order(1, 15000);
        Order o3 = new Order(1, 15000);

        assertTrue(o1.compareTo(o2) == 0);
        assertTrue(o2.compareTo(o3) == 0);
        assertTrue(o1.compareTo(o3) == 0);
    }

    @Test
    void collectionSortTest() {
        Set<Order> orderList = new TreeSet<>();

        for (int i = 1; i <= 5; i++) {
            orderList.add(new Order(i, 15000));
        }

        orderList.add(new Order(6, 1500));

        orderList.stream()
                .forEach(System.out::println);
    }

    @Test
    void bigDecimalTest() {
        Set<BigDecimal> bigDecimalSet = new HashSet<>();
        bigDecimalSet.add(new BigDecimal("1.0"));
        bigDecimalSet.add(new BigDecimal("1.00"));
        assertTrue(bigDecimalSet.size() == 2);

        bigDecimalSet = new TreeSet<>();
        bigDecimalSet.add(new BigDecimal("1.0"));
        bigDecimalSet.add(new BigDecimal("1.00"));
        assertTrue(bigDecimalSet.size() == 1);
    }
}
