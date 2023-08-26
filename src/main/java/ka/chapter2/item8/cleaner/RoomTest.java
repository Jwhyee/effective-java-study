package ka.chapter2.item8.cleaner;

import org.junit.jupiter.api.Test;

public class RoomTest {
    @Test
    void adultTest() {
        try (Room myRoom = new Room(7)) {
            System.out.println("청소 시작");
        }
        // try가 끝나면 객체를 소멸시킴
        // 때문에 Room 내부에 있는 close()메소드 실행
    }

    @Test
    void teenagerTest() {
        new Room(99);
        System.out.println("청소 시작");
    }
}
