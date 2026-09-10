import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * DEMO THAO TÁC FILE, FOLDER VÀ GỌI HTTP API TRONG JAVA (JAVA 11+)
 */
public class FilesAndApiDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("PHẦN 1: THAO TÁC FILE VÀ FOLDER (FILES & PATH)");
        System.out.println("==================================================");
        demoFileAndFolderOperations();

        System.out.println("\n==================================================");
        System.out.println("PHẦN 2: GỌI HTTP API (HTTPCLIENT - JAVA 11+)");
        System.out.println("==================================================");
        demoHttpApiCall();
    }

    /**
     * 1. DEMO CÁC THAO TÁC VỚI FILE & THƯ MỤC
     */
    public static void demoFileAndFolderOperations() {
        // Đặt đường dẫn trực tiếp vào thư mục dự án
        Path dirPath = Paths.get("/Users/thinhnguyen/DEVELOP/backend-java/demo_output/documents");
        Path filePath = dirPath.resolve("sample.txt");

        try {
            // A. Tạo thư mục (nếu chưa tồn tại)
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("-> Đã tạo thư mục: " + dirPath.toAbsolutePath());
            }

            // B. Ghi nội dung vào file (Java 11+)
            String contentToWrite = "Dữ liệu thử nghiệm Java Files API.\nNgày tạo: 2026-08-03";
            Files.writeString(filePath, contentToWrite, StandardCharsets.UTF_8);
            System.out.println("-> Đã ghi file thành công: " + filePath.getFileName());

            // C. Đọc nội dung file
            String readContent = Files.readString(filePath, StandardCharsets.UTF_8);
            System.out.println("-> Nội dung đọc từ file:\n" + readContent);

            // D. Liệt kê các file trong thư mục
            System.out.println("-> Danh sách các file trong thư mục '" + dirPath + "':");
            try (Stream<Path> stream = Files.list(dirPath)) {
                stream.forEach(p -> System.out.println("   - " + p.getFileName()));
            }

        } catch (IOException e) {
            System.err.println("Lỗi thao tác File: " + e.getMessage());
        } finally {
            // Xóa file & thư mục test sau khi chạy xong
            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(dirPath);
                Files.deleteIfExists(dirPath.getParent());
                System.out.println("-> Đã dọn dẹp thư mục tạm.");
            } catch (IOException ignored) {}
        }
    }

    /**
     * 2. DEMO GỌI HTTP API (GET & POST) VỚI JAVA 11 HTTPCLIENT
     */
    public static void demoHttpApiCall() {
        // Khởi tạo HttpClient chính chủ của Java 11
        HttpClient client = HttpClient.newHttpClient();

        // ----------------------------------------------------
        // A. GỌI API GET (Lấy dữ liệu)
        // ----------------------------------------------------
        System.out.println("\n[1] Đang gửi HTTP GET request...");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("-> Status Code: " + getResponse.statusCode());
            System.out.println("-> Response Body:\n" + getResponse.body());
        } catch (IOException | InterruptedException e) {
            System.err.println("Lỗi gọi API GET: " + e.getMessage());
        }

        // ----------------------------------------------------
        // B. GỌI API POST (Tạo mới dữ liệu với JSON)
        // ----------------------------------------------------
        System.out.println("\n[2] Đang gửi HTTP POST request...");
        String jsonPayload = """
                {
                    "title": "Học Java API",
                    "body": "Nội dung học tập với HttpClient Java 11",
                    "userId": 1
                }
                """;

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("-> Status Code: " + postResponse.statusCode()); // 201 Created
            System.out.println("-> Response Body:\n" + postResponse.body());
        } catch (IOException | InterruptedException e) {
            System.err.println("Lỗi gọi API POST: " + e.getMessage());
        }
    }
}
