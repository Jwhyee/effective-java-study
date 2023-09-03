package ka.chapter3.item10.symmetry;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTest {
    @Test
    void strEqMsgTest() {
        Message msg = new Message("Hello");
        String str = "hello";

        assertTrue(msg.equals(str));
    }

    @Test
    void msgEqStrTest() {
        Message msg = new Message("Hello");
        String str = "hello";

        assertTrue(str.equals(msg));
    }

    @Test
    void msgCollectionTest() {
        List<Message> msgList = new ArrayList<>();
        Message msg = new Message("Hello");
        msgList.add(msg);

        String str = "hello";
        assertTrue(msgList.contains(str));
    }
}
