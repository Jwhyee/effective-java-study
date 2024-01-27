package ka.chapter6.item35;

public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUATET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUATET(8),
    NONET(9), DECTET(10), TRIPLE_QUATET(12);

    private final int numberOfMusicians;
    Ensemble(int size) {this.numberOfMusicians = size;}

    public int numberOfMusicians() { return numberOfMusicians;}
}
