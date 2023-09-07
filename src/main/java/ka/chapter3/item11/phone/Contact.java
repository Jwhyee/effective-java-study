package ka.chapter3.item11.phone;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.Objects;

public class Contact {
    private int hashCode;
    private final int countryCode;
    private final int prefix, middle, suffix;
    private final Member member;

    public Contact(int countryCode, String phoneNumber, String name) {
        this.countryCode = countryCode;
        this.member = new Member(name);

        String[] split = phoneNumber.split("-");
        prefix = Integer.parseInt(split[0]);
        middle = Integer.parseInt(split[1]);
        suffix = Integer.parseInt(split[2]);

    }

//    @Override
//    public int hashCode() {
//        int result = Integer.hashCode(countryCode);
//        result = 31 * result + Integer.hashCode(middle);
//        result = 31 * result + Integer.hashCode(suffix);
//        result = 31 * result + (member != null ? member.hashCode() : 0);
//        return result;
//    }

    @Override
    synchronized public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = Integer.hashCode(countryCode);
            result = 31 * result + Integer.hashCode(middle);
            result = 31 * result + Integer.hashCode(suffix);
            result = 31 * result + (member != null ? member.hashCode() : 0);
        }
        return result;
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Contact c)) return false;
//        return countryCode == c.countryCode
//                && prefix == c.prefix
//                && middle == c.middle
//                && suffix == c.suffix
//                && Objects.equals(member, c.member);
//    }
}
