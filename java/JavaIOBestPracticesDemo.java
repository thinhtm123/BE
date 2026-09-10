import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * TỔNG HỢP LƯU Ý VÀ CÁCH DÙNG JAVA I/O TRONG THỰC TẾ DỰ ÁN BACKEND
 * 
 * --------------------------------------------------------------------------
 * 📌 CÁCH DÙNG HIỆN ĐẠI NÀY (JAVA 11+):
 * 1. Đọc/Ghi file nhỏ/vừa: Dùng java.nio.file.Files (readString, writeString).
 * 2. Đọc file lớn (> 100MB): Dùng Files.lines() kết hợp Stream API.
 * 3. Luôn chỉ định UTF-8: StandardCharsets.UTF-8.
 * 4. Luôn bọc Try-with-resources để tự động đóng Stream.
 * --------------------------------------------------------------------------
 */
public class JavaIOBestPracticesDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. CÁCH ĐỌC & GHI FILE CHUẨN HIỆN ĐẠI (JAVA 11+) ===");
        demoModernReadWrite();

        System.out.println("\n=== 2. CÁCH ĐỌC FILE LỚN TRÁNH LỖI OUT OF MEMORY (OOM) ===");
        demoReadLargeFile();

        System.out.println("\n=== 3. XỬ LÝ BẢO MẬT: CHỐNG LỖI PATH TRAVERSAL (HACKER ĐỌC FILE TỪ XA) ===");
        demoPathTraversalSecurity("../../etc/passwd"); // Hacker cố tình truyền chuỗi độc hại
        demoPathTraversalSecurity("document.pdf");    // File hợp lệ
    }

    /**
     * 1. ĐỌC & GHI FILE TEXT VỚI BẢNG MÃ UTF-8
     */
    public static void demoModernReadWrite() {
        Path filePath = Paths.get("sample_demo.txt");

        try {
            // GHI FILE: Luôn ép bảng mã UTF-8 để không bị lỗi font trên Windows
            String dataToWrite = "Xin chào Java I/O! Đây là dữ liệu Tiếng Việt chuẩn UTF-8.";
            Files.writeString(filePath, dataToWrite, StandardCharsets.UTF-8);
            System.out.println("-> Đã ghi file thành công: " + filePath.toAbsolutePath());

            // ĐỌC FILE: Đọc toàn bộ nội dung chỉ với 1 dòng code
            String content = Files.readString(filePath, StandardCharsets.UTF-8);
            System.out.println("-> Nội dung đọc được: " + content);

        } catch (IOException e) {
            System.err.println("Lỗi I/O khi thao tác file: " + e.getMessage());
        } finally {
            // Dọn dẹp file test sau khi demo
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }
    }

    /**
     * 2. ĐỌC FILE LỚN DẠNG STREAM (TIẾT KIỆM RAM)
     */
    public static void demoReadLargeFile() {
        Path tempLogFile = Paths.get("app_large_test.log");

        try {
            // Tạo file log giả định
            List<String> logData = List.of(
                "INFO - User 101 logged in",
                "ERROR - Database connection timeout at 10:00",
                "INFO - User 102 logged out",
                "ERROR - Payment gateway failed"
            );
            Files.write(tempLogFile, logData, StandardCharsets.UTF-8);

            // ĐỌC DẠNG STREAM (Chỉ nạp từng dòng vào RAM, file 5GB cũng chạy mượt!)
            // BẮT BUỘC dùng Try-with-resources để giải phóng tài nguyên sau khi stream xong
            try (Stream<String> lines = Files.lines(tempLogFile, StandardCharsets.UTF-8)) {
                System.out.println("-> Tìm thấy các dòng chứa 'ERROR':");
                lines.filter(line -> line.contains("ERROR"))
                     .forEach(line -> System.out.println("   [LOG BẮT ĐƯỢC]: " + line));
            }

        } catch (IOException e) {
            System.err.println("Lỗi đọc file log: " + e.getMessage());
        } finally {
            try {
                Files.deleteIfExists(tempLogFile);
            } catch (IOException ignored) {}
        }
    }

    /**
     * 3. AN TOÀN BẢO MẬT: PHÒNG CHỐNG HACK PATH TRAVERSAL
     */
    public static void demoPathTraversalSecurity(String userProvidedFilename) {
        Path uploadDirectory = Paths.get("uploads").toAbsolutePath();

        // Chuẩn hóa đường dẫn bằng resolve() và normalize()
        Path targetPath = uploadDirectory.resolve(userProvidedFilename).normalize();

        System.out.println("File client yêu cầu: " + userProvidedFilename);
        System.out.println("Đường dẫn sau khi chuẩn hóa: " + targetPath);

        // KIỂM TRA BẢO MẬT: Target path có bắt đầu bằng thư mục uploads không?
        if (!targetPath.startsWith(uploadDirectory)) {
            System.out.println("🛑 CẢNH BÁO BẢO MẬT: Phát hiện hành vi Hack Path Traversal! Từ chối xử lý.");
        } else {
            System.out.println("🟢 AN TOÀN: File hợp lệ nằm trong thư mục uploads.");
        }
    }
}
