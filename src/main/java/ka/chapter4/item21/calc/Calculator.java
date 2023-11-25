package ka.chapter4.item21.calc;

public interface Calculator {
    int sum(int num1, int num2);
    int minus(int num1, int num2);

    default int multiple(int num1, int num2) {
        return num1 * num2;
    }
}
