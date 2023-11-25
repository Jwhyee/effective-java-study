package ka.chapter4.item15.search;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializableTest {
    @Test
    void serializeTest() {
        // 직렬화할 파일 경로
        String filePath = "src/main/java/ka/chapter3/item15/search/node.ser";

        // 객체 생성
        Node original = new Node(2, 1);
        System.out.println("Original Node = " + original);

        // 객체를 파일에 직렬화
        serializeToFile(original, filePath);

        // 파일로부터 객체 역직렬화
        Node deserialized = deserializeFromFile(filePath);

        // 역직렬화된 객체 사용
        System.out.println("Deserialized Node = " + deserialized);

        assertTrue(original.x == deserialized.x);
    }

    // 객체를 파일에 직렬화하는 메소드
    private static void serializeToFile(Object object, String filePath) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(object);
            System.out.println("Object serialized to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일로부터 객체 역직렬화하는 메소드
    private static Node deserializeFromFile(String filePath) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            Node deserialized = (Node) inputStream.readObject();
            System.out.println("Object deserialized from " + filePath);
            return deserialized;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
