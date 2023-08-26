package ka.chapter2.item8.finalizer.attack;

import ka.chapter2.item8.finalizer.Post;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttackTest {
    @Test
    void serializeTest() throws Exception {
        // 직렬화할 파일 경로
        String filePath = "attack_test.ser";

        // 객체 생성
        Child original = new Child();

        // 객체를 파일에 직렬화
        serializeToFile(original, filePath);

        // 파일로부터 객체 역직렬화
        Child deserialized = deserializeFromFile(filePath);

        // 테스트 실패!
        assertTrue(original == deserialized);
    }

    // 객체를 파일에 직렬화하는 메소드
    private static void serializeToFile(Object object, String filePath) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(object);
            System.out.println("직렬화 : " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일로부터 객체 역직렬화하는 메소드
    private static Child deserializeFromFile(String filePath) {
        int id = 1;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            Child deserialized = (Child) inputStream.readObject();
            if (id == 1) {
                throw new RuntimeException("예외 발생!");
            }
            System.out.println("역직렬화 : " + filePath);
            return deserialized;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
