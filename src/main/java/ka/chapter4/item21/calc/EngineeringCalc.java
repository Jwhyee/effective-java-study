package ka.chapter4.item21.calc;

public class EngineeringCalc implements Calculator {
    @Override
    public int sum(int num1, int num2) {
        return num1 + num2;
    }

    @Override
    public int minus(int num1, int num2) {
        return num1 - num2;
    }
}
