package ka.chapter3.item10.consistency;

public class Member {

    private String nickname;

    public Member(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "Member( nickname : " + nickname + " )";
    }
}
