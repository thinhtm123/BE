import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class BasicIO {
    public static void main(String[] args) {
        // ==========================================
        // 1. IN DỮ LIỆU RA MÀN HÌNH (System.out.println)
        // ==========================================
        // JS: console.log("Hello World");
        System.out.println("Hello World từ Java!");

        // In không xuống dòng (giống process.stdout.write trong Node.js)
        System.out.print("Dòng này ");
        System.out.print("không xuống dòng.\n"); // Sử dụng \n để xuống dòng thủ công

        // In định dạng (Format) - giống console.log("Họ tên: %s, Tuổi: %d", name, age)
        String user = "Thịnh";
        int experienceYears = 4;
        System.out.printf("Lập trình viên: %s - Kinh nghiệm: %d năm JS\n", user, experienceYears);


        // ==========================================
        // 2. NHẬN DỮ LIỆU TỪ BÀN PHÍM (System.in)
        // ==========================================

        // --- CÁCH A: DÙNG SCANNER ---
        // Tiện lợi nhất cho người mới bắt đầu vì tự động phân tích (parse) các kiểu dữ liệu như int, double...
        System.out.println("\n--- [Cách 1] Sử dụng Scanner ---");
        Scanner scanner = new Scanner(System.in);

        System.out.print("Nhập tên của bạn: ");
        String name = scanner.nextLine(); // Đọc một dòng text (String)

        System.out.print("Nhập tuổi của bạn: ");
        // Tránh lỗi trôi dòng: Nếu dùng scanner.nextInt(), ký tự xuống dòng '\n' vẫn ở trong bộ đệm.
        // Cách tốt nhất và an toàn nhất là đọc String rồi tự parse:
        int age = 0;
        try {
            age = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Tuổi không hợp lệ, mặc định là 0.");
        }

        System.out.println("-> [Scanner] Xin chào " + name + ", bạn " + age + " tuổi.");


        // --- CÁCH B: DÙNG BUFFEREDREADER + INPUTSTREAMREADER ---
        // Nhanh hơn Scanner về tốc độ đọc (buffer lớn hơn).
        // Chỉ đọc String nên nếu muốn lấy số phải tự ép kiểu (parseInt, parseDouble...).
        // Bắt buộc phải handle ngoại lệ (Exception) hoặc ném lỗi đi (throws IOException).
        System.out.println("\n--- [Cách 2] Sử dụng BufferedReader ---");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Nhập địa chỉ của bạn: ");
            String address = reader.readLine(); // Đọc một dòng text

            System.out.print("Nhập số năm kinh nghiệm lập trình: ");
            String expInput = reader.readLine();
            int exp = Integer.parseInt(expInput); // Tự parse thủ công

            System.out.printf("-> [BufferedReader] Địa chỉ: %s, Kinh nghiệm: %d năm.\n", address, exp);
        } catch (IOException e) {
            System.out.println("Có lỗi xảy ra khi đọc dữ liệu: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Vui lòng nhập một số hợp lệ!");
        }

        // Đóng scanner và reader khi không sử dụng nữa để giải phóng tài nguyên.
        // Scanner đóng sẽ đóng luồng System.in gốc luôn.
        scanner.close();
    }
}
