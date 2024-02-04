package ka.chapter6.item38;

public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double a, double b) { return Math.pow(a, b); }
    },
    REMINDER("%") {
        public double apply(double a, double b) { return a % b; }
    };

    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
