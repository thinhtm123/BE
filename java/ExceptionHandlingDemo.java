import java.io.FileReader;
import java.io.IOException;

/**
 * DEMO VỀ XỬ LÝ NGOẠI LỆ (EXCEPTION HANDLING) TRONG JAVA
 * 
 * Cây phân cấp (Hierarchy):
 *                     Throwable
 *                     /       \
 *              Exception       Error (OutOfMemoryError, StackOverflowError...)
 *               /      \
 *      Checked Ex       Unchecked Ex (RuntimeException)
 *   (IOException...)   (NullPointerException, ArithmeticException...)
 */
public class ExceptionHandlingDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. DEMO UNCHECKED EXCEPTION (RUNTIME EXCEPTION) ===");
        demoUncheckedException();

        System.out.println("\n=== 2. DEMO CHECKED EXCEPTION & TRY-CATCH-FINALLY ===");
        demoCheckedException();

        System.out.println("\n=== 3. DEMO TRY-WITH-RESOURCES (JAVA 7+) ===");
        demoTryWithResources();

        System.out.println("\n=== 4. DEMO CUSTOM EXCEPTION & THROW / THROWS ===");
        try {
            findUserById(-1L);
        } catch (InvalidUserIdException e) {
            System.out.println("Bắt được Custom Exception: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // 1. UNCHECKED EXCEPTION (RuntimeException)
    // ----------------------------------------------------
    // Không bắt buộc phải bọc try-catch hoặc khai báo throws.
    // Xảy ra lúc ứng dụng ĐANG CHẠY (Runtime) do lỗi logic lập trình.
    public static void demoUncheckedException() {
        try {
            int result = 10 / 0; // Lỗi ArithmeticException (Chia cho 0)
            System.out.println("Kết quả: " + result);
        } catch (ArithmeticException e) {
            System.out.println("Đã xử lý bẫy: Không thể chia một số cho 0!");
        } catch (Exception e) {
            // Catch tổng quát (luôn đặt ở cuối các catch cụ thể)
            System.out.println("Bắt lỗi chung: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // 2. CHECKED EXCEPTION & TRY-CATCH-FINALLY
    // ----------------------------------------------------
    // Java Compiler ÉP BẮT BUỘC phải xử lý lúc viết code (Compile-time).
    // Nếu không bọc try-catch hoặc khai báo 'throws', code SẼ KHÔNG BIÊN DỊCH ĐƯỢC.
    public static void demoCheckedException() {
        FileReader reader = null;
        try {
            System.out.println("Đang mở file...");
            // FileReader ném ra IOException (Checked Exception) nếu file không tồn tại
            reader = new FileReader("file_khong_ton_tai.txt");
            reader.read();
        } catch (IOException e) {
            System.out.println("Bắt lỗi Checked Exception: Không tìm thấy file hoặc lỗi đọc file!");
        } finally {
            // Khối 'finally' LUÔN LUÔN CHẠY (dù có ngoại lệ hay không)
            // Thường dùng để dọn dẹp tài nguyên (đóng file, ngắt connection CSDL)
            System.out.println("Khối 'finally': Đóng tài nguyên reader (nếu có)...");
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Lỗi khi đóng reader: " + e.getMessage());
                }
            }
        }
    }

    // ----------------------------------------------------
    // 3. TRY-WITH-RESOURCES (Từ Java 7+)
    // ----------------------------------------------------
    // Giúp TỰ ĐỘNG ĐÓNG TÀI NGUYÊN (file, connection, socket...) mà không cần khối finally rườm rà.
    // Áp dụng cho các class triển khai interface AutoCloseable.
    public static void demoTryWithResources() {
        // Tài nguyên khai báo trong () của try sẽ tự động đóng sau khi xong khối try
        try (FileReader reader = new FileReader("test.txt")) {
            System.out.println("Đọc file...");
        } catch (IOException e) {
            System.out.println("Tự động dọn dẹp tài nguyên! Lỗi caught: " + e.getMessage());
        }
    }

    // ----------------------------------------------------
    // 4. CUSTOM EXCEPTION & THROW / THROWS
    // ----------------------------------------------------
    // 'throws': Khai báo ở chữ ký hàm rằng hàm này CÓ THỂ ném ra ngoại lệ.
    // 'throw': Lệnh chủ động ném ra 1 đối tượng Exception.
    public static void findUserById(Long id) throws InvalidUserIdException {
        if (id == null || id <= 0) {
            throw new InvalidUserIdException("ID người dùng không hợp lệ: " + id);
        }
        System.out.println("Tìm thấy user với ID: " + id);
    }
}

// Custom Unchecked/Checked Exception
class InvalidUserIdException extends Exception {
    public InvalidUserIdException(String message) {
        super(message);
    }
}
