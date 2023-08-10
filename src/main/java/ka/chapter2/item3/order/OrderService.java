package ka.chapter2.item3.order;

import java.util.LinkedList;
import java.util.List;

public class OrderService {
    public static final OrderService INSTANCE = new OrderService();
    private final OrderRepository repository = OrderRepository.INSTANCE;
    private OrderService(){}

    public void makeOrder(String nickname, int price) {
        repository.save(new Order(nickname, price));
    }

    public List<Order> getOrderList() {
        return repository.findAllOrder();
    }

}
