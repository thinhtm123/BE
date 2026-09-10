import java.math.BigDecimal;

public class GotchasDemo {
    public static void main(String[] args) {
        System.out.println("=== CÁC BẪY PHỔ BIẾN KHI CHUYỂN TỪ JS SANG JAVA ===");

        // ----------------------------------------------------
        // BẪY 1: So sánh Chuỗi (String Comparison)
        // ----------------------------------------------------
        // JS: str1 === str2
        String s1 = new String("hello");
        String s2 = new String("hello");

        // Sử dụng '==' để so sánh (Sai lầm phổ biến)
        // '==' so sánh địa chỉ ô nhớ trên Heap, không so sánh nội dung chuỗi.
        System.out.println("So sánh bằng '==': " + (s1 == s2)); // Kết quả: false

        // Sử dụng '.equals()' để so sánh nội dung (Đúng đắn)
        System.out.println("So sánh bằng '.equals()': " + s1.equals(s2)); // Kết quả: true


        // ----------------------------------------------------
        // BẪY 2: Phép chia số nguyên (Integer Division)
        // ----------------------------------------------------
        // JS: 5 / 2 = 2.5
        // Java: Chia 2 số nguyên sẽ trả về số nguyên (phần thập phân bị cắt bỏ).
        int a = 5;
        int b = 2;
        System.out.println("Phép chia 5 / 2 trong Java: " + (a / b)); // Kết quả: 2

        // Muốn ra số thực, ít nhất 1 toán hạng phải là kiểu thực (double/float) hoặc ép kiểu:
        System.out.println("Đúng: 5.0 / 2 = " + (5.0 / b)); // Kết quả: 2.5
        System.out.println("Đúng: (double)5 / 2 = " + ((double) a / b)); // Kết quả: 2.5


        // ----------------------------------------------------
        // BẪY 3: So sánh Wrapper Class (Integer Cache)
        // ----------------------------------------------------
        // Java cache các đối tượng Integer có giá trị từ -128 đến 127.
        Integer num1 = 127;
        Integer num2 = 127;
        System.out.println("So sánh Integer (127 == 127): " + (num1 == num2)); // Kết quả: true (vì dùng chung cache)

        Integer num3 = 128;
        Integer num4 = 128;
        System.out.println("So sánh Integer (128 == 128): " + (num3 == num4)); // Kết quả: false (vì tạo ra 2 object mới trên Heap)

        // Quy tắc: Luôn dùng '.equals()' cho tất cả Object (kể cả Integer, Double...)
        System.out.println("Đúng: 128 equals 128: " + num3.equals(num4)); // Kết quả: true


        // ----------------------------------------------------
        // BẪY 4: Sai số tính toán Floating-point & Tiền tệ
        // ----------------------------------------------------
        // Tương tự JS: 0.1 + 0.2 = 0.30000000000000004 do lưu trữ nhị phân.
        double d1 = 0.1;
        double d2 = 0.2;
        System.out.println("double 0.1 + 0.2: " + (d1 + d2)); // Kết quả: 0.30000000000000004

        // Giải pháp trong Java cho ngành tài chính, tiền tệ: Dùng BigDecimal
        BigDecimal bd1 = new BigDecimal("0.1"); // Bắt buộc truyền String vào constructor
        BigDecimal bd2 = new BigDecimal("0.2");
        BigDecimal result = bd1.add(bd2);
        System.out.println("BigDecimal 0.1 + 0.2: " + result); // Kết quả chuẩn xác: 0.3


        // ----------------------------------------------------
        // BẪY 5: Tránh NullPointerException (NPE) khi so sánh chuỗi
        // ----------------------------------------------------
        String input = null; // Biến nhận từ API/Client có thể null
        
        try {
            // Cách nguy hiểm: Nếu input là null, gọi hàm sẽ crash chương trình
            // input.equals("ADMIN");
        } catch (NullPointerException e) {
            System.out.println("Bị crash do NullPointerException!");
        }

        // Cách an toàn: Đặt chuỗi literal (chuỗi biết chắc chắn khác null) lên trước
        boolean isAdmin = "ADMIN".equals(input); // Kết quả trả về false an toàn, không bao giờ bị NPE
        System.out.println("So sánh an toàn với chuỗi null: " + isAdmin);
    }
}
