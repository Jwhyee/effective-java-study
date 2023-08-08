package ka.chapter2.item3.order;

public class Order {
    private String nickname;

    private int price;

    public Order(String nickname, int price) {
        this.nickname = nickname;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "nickname='" + nickname + '\'' +
                ", price=" + price +
                '}';
    }
}
