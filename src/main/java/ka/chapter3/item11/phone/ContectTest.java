package ka.chapter3.item11.phone;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContectTest {

    @Test
    void logicalTest() {
        Map<Contact, String> contactMap = new HashMap<>();

        int numContacts = 1000; // 적절한 값을 설정하세요

        for (int i = 0; i < numContacts; i++) {
            Contact contact = new Contact(82, "010-1234-1234", "준영");
            contactMap.put(contact, "Some Value");
        }

        int numBuckets = contactMap.size();
        int avgItemsPerBucket = numContacts / numBuckets;

        System.out.println("Number of Contacts: " + numContacts);
        System.out.println("Number of Buckets: " + numBuckets);
        System.out.println("Average Items Per Bucket: " + avgItemsPerBucket);
    }

    // HashMap 내부의 버킷 수를 가져오는 메서드
    private static int getNumBuckets(Map<Contact, String> map) {
        int numBuckets = 0;
        try {
            java.lang.reflect.Field tableField = HashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(map);
            if (table != null) {
                numBuckets = table.length;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return numBuckets;
    }

    @Test
    void equalsTest() {
        Map<Contact, String> sendMessageMap = new HashMap<>();

        String msg = "안녕 나 에스파 윈터야 100만원만 보내줘!";

        sendMessageMap.put(new Contact(82, "010-1234-1234", "준영"), msg);

        assertTrue(sendMessageMap.get(new Contact(82, "010-1234-1234", "준영"))
                .equals(msg)
        );
    }
}
