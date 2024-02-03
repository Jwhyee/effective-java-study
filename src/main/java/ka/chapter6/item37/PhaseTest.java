package ka.chapter6.item37;

public class PhaseTest {
    public static void main(String[] args) {
        System.out.println(Phase.Transition.from(Phase.LIQUID, Phase.GAS));
        Phase.Transition.printMap();
    }
}
