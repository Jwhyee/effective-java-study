package ka.chapter3.item12.phone;

import java.math.BigInteger;
import java.time.LocalDate;

public class Phone {
    private int areaCode, prefix, lineNum;

    @Override
    public String toString() {
        return "%03d-%04d-%04d".formatted(areaCode, prefix, lineNum);
    }

    public Phone(String number) {
        String[] split = number.split("-");
        areaCode = Integer.parseInt(split[0]);
        prefix = Integer.parseInt(split[1]);
        lineNum = Integer.parseInt(split[2]);
    }

}
