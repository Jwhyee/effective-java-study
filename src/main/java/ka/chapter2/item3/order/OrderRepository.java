package ka.chapter2.item3.order;

import java.util.LinkedList;
import java.util.List;

public class OrderRepository {
    public static final OrderRepository INSTANCE = new OrderRepository();
    private final List<Order> orderList;
    private OrderRepository(){
        orderList = new LinkedList<>();
    }

    public void save(Order order) {
        orderList.add(order);
    }

    public List<Order> findAllOrder() {
        return orderList;
    }

}
