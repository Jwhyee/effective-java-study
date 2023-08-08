package ka.chapter2.item3.order;

import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    @Test
    void orderTest1() {
        OrderService instance = OrderService.INSTANCE;
        instance.makeOrder("user1", 10000);
        instance.makeOrder("user2", 30000);

        System.out.println(instance.getOrderList());

    }
}
