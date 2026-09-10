// 1. KHAI BÁO PACKAGE (Không gian tên, quản lý cấu trúc thư mục)

// 2. KHAI BÁO IMPORT (Nạp thư viện có sẵn trong JDK hoặc thư viện bên ngoài)
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 3. KHAI BÁO CLASS (Mọi code Java phải nằm trong một class)
// Tên Class trùng khớp hoàn toàn với tên file (BasicSyntaxDemo.java)
public class BasicSyntaxDemo {

    // HẰNG SỐ (Constant) - Dùng 'final', static (cho toàn bộ class) và viết hoa
    // UPPER_SNAKE_CASE
    // JS: const MAX_USERS = 100;
    public static final int MAX_USERS = 100;

    // 4. ENTRY POINT (Phương thức main - nơi chương trình bắt đầu chạy)
    public static void main(String[] args) {
        System.out.println("=== MINH HỌA CÚ PHÁP CƠ BẢN JAVA ===");

        // ----------------------------------------------------
        // A. BIẾN & KIỂU DỮ LIỆU NGUYÊN THỦY (Primitives)
        // ----------------------------------------------------
        // Phân biệt rõ kiểu số nguyên, số thực, luận lý, ký tự
        int userCount = 42; // Số nguyên (32-bit)
        double score = 9.5; // Số thực (64-bit)
        boolean isLogged = true; // Luận lý
        char rating = 'A'; // Ký tự đơn (bọc trong nháy đơn '')

        // Sử dụng 'var' từ Java 10+ (Local Variable Type Inference)
        // JS: let greeting = "Hello";
        var greeting = "Xin chào!"; // Tự suy luận kiểu String

        System.out.println("Greeting: " + greeting);
        System.out.println("User count: " + userCount);

        // ----------------------------------------------------
        // B. BIỂU THỨC & CÂU LỆNH (Expressions & Statements)
        // ----------------------------------------------------
        // Cần kết thúc bằng dấu chấm phẩy ; bắt buộc
        int doubleCount = userCount * 2; // Biểu thức nhân
        if (doubleCount > MAX_USERS) {
            System.out.println("Đã vượt quá số lượng user tối đa cho phép!");
        } else {
            System.out.println("Số lượng user vẫn nằm trong giới hạn.");
        }

        // ----------------------------------------------------
        // C. CẤU TRÚC DỮ LIỆU (Mảng & Collections)
        // ----------------------------------------------------

        // 1. Mảng tĩnh (Cố định số lượng phần tử khi tạo)
        // JS: const numbers = [10, 20, 30] (nhưng JS thay đổi được size)
        int[] fixedNumbers = { 10, 20, 30 };
        System.out.println("Phần tử đầu tiên của mảng tĩnh: " + fixedNumbers[0]);
        System.out.println("Chiều dài mảng tĩnh: " + fixedNumbers.length);

        // Loop qua mảng tĩnh (giống for-of trong JS)
        // JS: for (let num of fixedNumbers)
        for (int num : fixedNumbers) {
            System.out.print(num + " ");
        }
        System.out.println(); // Xuống dòng

        // 2. ArrayList (Mảng động - Co giãn kích thước linh hoạt như mảng JS)
        // Lưu ý: ArrayList chỉ chứa đối tượng (Reference Types), không chứa Primitive
        // trực tiếp.
        // Tự động chuyển int thành Integer (Autoboxing).
        ArrayList<String> languages = new ArrayList<>();
        languages.add("JavaScript"); // Thêm phần tử
        languages.add("Java");
        languages.add("TypeScript");

        System.out.println("Mảng động: " + languages);
        System.out.println("Ngôn ngữ thứ 2: " + languages.get(1)); // Dùng get(index) thay vì [index]

        // 3. HashMap (Cấu trúc Key-Value, giống Object/Map trong JS)
        // Khai báo Map chứa Key là String, Value là Integer
        // JS: const userAges = { "Thinh": 25, "An": 22 }
        Map<String, Integer> userAges = new HashMap<>();
        userAges.put("Thinh", 25);
        userAges.put("An", 22);

        System.out.println("Tuổi của Thinh: " + userAges.get("Thinh"));

        // Loop qua HashMap
        for (Map.Entry<String, Integer> entry : userAges.entrySet()) {
            System.out.printf("Key: %s, Value: %d\n", entry.getKey(), entry.getValue());
        }

        // ----------------------------------------------------
        // D. GỌI MỘT ĐỐI TƯỢNG ĐƠN GIẢN (OOP Basic)
        // ----------------------------------------------------
        Person person = new Person("Nguyen Van A", 28);
        person.sayHello();
    }
}

// 5. MỘT CLASS PHỤ TRONG FILE (Không dùng từ khóa public)
class Person {
    // Thuộc tính đóng gói (Encapsulation)
    private String name;
    private int age;

    // Hàm khởi tạo (Constructor)
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Phương thức
    public void sayHello() {
        System.out.printf("Xin chào, tôi là %s, %d tuổi.\n", this.name, this.age);
    }
}
