package ka.chapter2.item2;

public class Phone {
    private final String modelName;
    private final String brand;
    private final int price;
    private final int storage;
    private final int maxBattery;

    public static class Builder {
        private final String modelName;
        private final String brand;
        private int price = 0;
        private int storage = 0;
        private int maxBattery = 0;

        public Builder(String modelName, String brand) {
            this.modelName = modelName;
            this.brand = brand;
        }

        public Builder price(int val) {
            price = val;
            return this;
        }

        public Builder storage(int val) {
            storage = val;
            return this;
        }

        public Builder maxBattery(int val) {
            maxBattery = val;
            return this;
        }

        public Phone build() {
            return new Phone(this);
        }
    }

    private Phone(Builder builder) {
        modelName = builder.modelName;
        brand = builder.brand;
        price = builder.price;
        storage = builder.storage;
        maxBattery = builder.maxBattery;
    }
}
