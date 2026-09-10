import java.util.Arrays;

public class DataTypesVariablesDemo {

    // ====================================================
    // I. PHÂN BIỆT 3 LOẠI BIẾN (Local, Instance, Static)
    // ====================================================

    // 1. STATIC VARIABLE (Biến tĩnh)
    // - Khai báo có từ khóa 'static' bên ngoài các phương thức.
    // - Thuộc về Lớp (Class), dùng chung cho toàn bộ các thực thể tạo ra từ Class này.
    // - Vòng đời: Khởi tạo khi Class được load, giải phóng khi chương trình dừng.
    // - JS tương đương: Class static fields.
    public static String companyName = "Google Corp";

    // 2. INSTANCE VARIABLE (Biến thực thể / Biến toàn cục đối tượng)
    // - Khai báo không có từ khóa 'static' bên ngoài các phương thức.
    // - Thuộc về riêng từng đối tượng (Object) cụ thể.
    // - Tự động được gán giá trị mặc định (0, false, null...) nếu ta không khởi tạo.
    // - JS tương đương: Class properties (như this.employeeName).
    public String employeeName;
    public int employeeAge;

    // Constructor (Hàm tạo)
    public DataTypesVariablesDemo(String name, int age) {
        this.employeeName = name;
        this.employeeAge = age;
    }

    public void displayInfo() {
        // 3. LOCAL VARIABLE (Biến cục bộ)
        // - Khai báo bên trong một phương thức, constructor hoặc block {...}.
        // - Chỉ tồn tại trong phạm vi thực thi của phương thức đó.
        // - KHÔNG CÓ giá trị mặc định, bắt buộc phải khởi tạo trước khi gọi nếu không sẽ lỗi biên dịch!
        // - JS tương đương: Khai báo let/const bên trong một function.
        String officeBranch = "Hanoi Office";

        System.out.printf("Nhân viên: %s, Tuổi: %d, Nơi làm việc: %s của %s\n",
                employeeName, employeeAge, officeBranch, companyName);
    }

    public static void main(String[] args) {
        System.out.println("=== 1. THỬ NGHIỆM 3 LOẠI BIẾN ===");
        
        // Tạo 2 thực thể nhân viên khác nhau
        DataTypesVariablesDemo emp1 = new DataTypesVariablesDemo("Thịnh", 25);
        DataTypesVariablesDemo emp2 = new DataTypesVariablesDemo("An", 22);

        emp1.displayInfo();
        emp2.displayInfo();

        // Thay đổi giá trị biến Static (ảnh hưởng toàn bộ Class)
        DataTypesVariablesDemo.companyName = "Alphabet Inc";
        System.out.println("\n--- Sau khi thay đổi tên công ty (Static) ---");
        emp1.displayInfo();
        emp2.displayInfo();


        // ====================================================
        // II. PHÂN BIỆT KIỂU DỮ LIỆU (Primitive vs Non-Primitive)
        // ====================================================
        System.out.println("\n=== 2. KIỂU DỮ LIỆU (DATA TYPES) ===");

        // --- A. PRIMITIVE TYPES (Kiểu dữ liệu nguyên thủy) ---
        // Lưu giá trị thực tế trực tiếp trên bộ nhớ STACK. Hiệu năng cực kỳ nhanh.
        byte smallByte = 127;           // 1 byte  (-128 đến 127)
        short smallShort = 32767;       // 2 bytes (-32,768 đến 32,767)
        int score = 2000000000;         // 4 bytes (Kiểu số nguyên mặc định của Java)
        long bigNumber = 900000000000L; // 8 bytes (Cần ký tự 'L' ở cuối)
        
        float temp = 36.6f;             // 4 bytes (Cần ký tự 'f' ở cuối)
        double pi = 3.1415926535;       // 8 bytes (Kiểu số thực mặc định)

        char rating = 'A';              // 2 bytes (Lưu 1 ký tự Unicode, bọc nháy đơn '')
        boolean isHappy = true;         // Luận lý (true/false)

        System.out.println("[Primitive] int score: " + score);
        System.out.println("[Primitive] double pi: " + pi);
        System.out.println("[Primitive] char rating: " + rating);

        // --- B. NON-PRIMITIVE TYPES / REFERENCE TYPES (Kiểu tham chiếu) ---
        // Lưu địa chỉ trỏ tới đối tượng trên bộ nhớ HEAP.
        // Có thể có các phương thức đính kèm và nhận giá trị 'null'.

        // 1. String (Kiểu chuỗi - là một Class đặc biệt)
        String helloMsg = "Hello World";

        // 2. Arrays (Mảng - có kích thước cố định)
        int[] ages = {25, 30, 35};

        // 3. Classes (Mọi đối tượng tạo từ class)
        DataTypesVariablesDemo demoObject = new DataTypesVariablesDemo("Bình", 30);

        // 4. Enums (Kiểu liệt kê cố định danh sách giá trị)
        Role userRole = Role.ADMIN;

        // 5. Records (Tính năng từ Java 14+ giúp lưu dữ liệu Immutable nhanh chóng, không cần viết boilerplate code)
        // Tương tự Class nhưng chỉ để lưu data (DTO), có sẵn getter, toString, equals...
        Point coordinates = new Point(10, 20);

        System.out.println("[Reference] String: " + helloMsg);
        System.out.println("[Reference] Array: " + Arrays.toString(ages));
        System.out.println("[Reference] Enum: " + userRole);
        System.out.println("[Reference] Record: " + coordinates + " -> X=" + coordinates.x());
    }
}

// Khai báo Enum bên ngoài Class chính
enum Role {
    USER, ADMIN, MANAGER
}

// Khai báo Record bên ngoài Class chính (giống như Object đóng băng trong JS)
record Point(int x, int y) {}
