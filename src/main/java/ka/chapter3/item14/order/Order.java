package ka.chapter3.item14.order;

import java.util.Comparator;
import java.util.Objects;

import static java.util.Comparator.*;

public class Order implements Comparable<Order> {

    int num;
    int fee;

    public Order(int num, int fee) {
        this.num = num;
        this.fee = fee;
    }

    private static final Comparator<Order> COMPARATOR =
            comparing((Order o) -> o.num)
                    .thenComparingInt(o -> o.fee);

    private static final Comparator<Order> hashCodeOrder = Comparator.comparingInt(o -> o.hashCode());

    @Override
    public int compareTo(Order o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return num == order.num && fee == order.fee;
    }

    @Override
    public String toString() {
        return "Order{" +
                "num=" + num +
                ", fee=" + fee +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, fee);
    }
}
