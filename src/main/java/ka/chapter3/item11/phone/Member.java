package ka.chapter3.item11.phone;

import java.util.Objects;

public class Member {
    private final String name;

    public Member(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(name, member.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
