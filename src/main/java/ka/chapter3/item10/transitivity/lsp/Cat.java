package ka.chapter3.item10.transitivity.lsp;

public class Cat extends Animal {

    String move(int distance, boolean flying) {
        if (flying) {
            return distance + "만큼 날아서 이동했습니다.";
        }
        return distance + "만큼 뛰어서 이동했습니다.";
    }

}
