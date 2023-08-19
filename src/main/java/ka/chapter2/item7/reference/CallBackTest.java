package ka.chapter2.item7.reference;

import org.junit.jupiter.api.Test;

public class CallBackTest {
    @Test
    void callBackTest() {
        Client client = new Client();

        // 콜백 등록
        client.registerCallback(message -> System.out.println("Received: " + message));

        // 콜백을 약한 참조로 저장하여 메모리 누수 방지
        client.performTask();

        // 가비지 컬렉션 시도
        System.gc();

        // 콜백이 수거되어 호출되지 않음
        client.performTask();
    }
}
