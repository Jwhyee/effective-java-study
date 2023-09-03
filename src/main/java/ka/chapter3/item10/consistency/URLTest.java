package ka.chapter3.item10.consistency;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class URLTest {

    @Test
    void urlTest() {
        String urlStr1 = "https://www.naver.com";
        String urlStr2 = "https://223.130.200.107";

        try {
            URL url1 = new URL(urlStr1);
            URL url2 = new URL(urlStr2);

            InetAddress address1 = InetAddress.getByName(url1.getHost());
            InetAddress address2 = InetAddress.getByName(url2.getHost());

            System.out.println("Host 1 IP: " + address1.getHostAddress());
            System.out.println("Host 2 IP: " + address2.getHostAddress());

            // 테스트 성공!
            assertTrue(url1.equals(url2));

        } catch (MalformedURLException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
