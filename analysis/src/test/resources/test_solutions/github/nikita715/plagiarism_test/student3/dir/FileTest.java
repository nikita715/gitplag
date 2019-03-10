package files;

import java.io.FileOutputStream;
import java.nio.file.Paths;

public class FileTest {

    public static void main(String[] args) {
        String content = "select * from student;";
        final Path tempDirectory = Files.createDirectories(Paths.get("nikita715/plagiarism_test/testns"))
            .createFile(Paths.get(tempDirectory.toString(), "script.sql"));
        try (final FileOutputStream fileOutputStream = new FileOutputStream(tempFile.toFile())) {
            fileOutputStream.write(content.getBytes());
        }
        Files.deleteIfExists(tempDirectory);
    }

}
