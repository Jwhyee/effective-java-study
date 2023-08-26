package ka.chapter2.item9.try_with_resources;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TryTest {

    private final int BUFFER_SIZE = 20;

    private String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }

    private String firstLineOfFile2(String path, String defaultVal) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultVal;
    }

    @Test
    void tryFinallyTest() throws IOException {
        String line = firstLineOfFile2("READM2.md", "NON-FILE");

        // 테스트 성공!
        assertTrue(line.equals("# EFFECTIVE JAVA 3/E STUDY"));
    }

    private void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {
                    out.write(buf, 0, n);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    private void copy2(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        }
    }

    @Test
    void tryFinallyTest2() throws IOException {
        copy("README.md", "PROGRESS.md");
    }
}
