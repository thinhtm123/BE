/**
 * DEMO TRỰC QUAN VỀ JMM (JAVA MEMORY MODEL) & TỪ KHÓA VOLATILE
 * Vấn đề minh họa: Memory Visibility (Tính nhìn thấy của dữ liệu giữa các CPU Cache)
 */
public class JmmVisibilityDemo {

    // 🔴 KHÔNG CÓ VOLATILE: Giá trị bị lưu cache riêng trên CPU Core của từng Thread!
    private static boolean flagWithoutVolatile = false;

    // 🟢 CÓ VOLATILE: Ép các CPU Core đọc/ghi trực tiếp xuống RAM chính!
    private static volatile boolean flagWithVolatile = false;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==================================================");
        System.out.println("DEMO 1: KHÔNG DÙNG VOLATILE (XẢY RA LỖI JMM VISIBILITY)");
        System.out.println("==================================================\n");
        demoWithoutVolatile();

        System.out.println("\n--------------------------------------------------\n");

        System.out.println("==================================================");
        System.out.println("DEMO 2: CÓ DÙNG VOLATILE (ĐỒNG BỘ MEMORY BẮT BỘ TRUY CẬP RAM)");
        System.out.println("==================================================\n");
        demoWithVolatile();
    }

    /**
     * THỬ NGHIỆM 1: KHÔNG DÙNG VOLATILE
     * Worker Thread sẽ bị KẸT VĨNH VIỄN trong vòng lặp vô tận vì CPU Core của nó
     * chỉ đọc biến flag từ CPU Cache cũ của mình mà KHÔNG BIẾT Main Thread đã đổi flag = true ở RAM!
     */
    public static void demoWithoutVolatile() throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("   [Worker Thread] Đang chạy vòng lặp kiểm tra flag...");
            long count = 0;
            // Vòng lặp chạy liên tục kiểm tra biến
            while (!flagWithoutVolatile) {
                count++;
                // Lưu ý: Tuyệt đối KHÔNG gọi Thread.sleep() hay System.out.println() ở đây,
                // vì các hàm đó chứa khối synchronized ngầm sẽ vô tình ép CPU flush cache!
            }
            System.out.println("   [Worker Thread] Đã thoát vòng lặp! (count = " + count + ")");
        });

        worker.start();

        // Main Thread nghỉ 100ms rồi đổi flag thành true ở RAM
        Thread.sleep(100);
        System.out.println("   [Main Thread] Đã đổi flagWithoutVolatile = true!");
        flagWithoutVolatile = true;

        // Chờ Worker Thread trong 1.5 giây
        worker.join(1500);

        if (worker.isAlive()) {
            System.out.println("🚨 KẾT QUẢ: Worker Thread BỊ KẸT VĨNH VIỄN!");
            System.out.println("   👉 Giải thích JMM: CPU Core của Worker chỉ nhìn thấy giá trị 'false' ở CPU Cache của nó.");
            worker.interrupt(); // Ngắt luồng bị kẹt để tiếp tục chương trìxnh
        }
    }

    /**
     * THỬ NGHIỆM 2: CÓ DÙNG VOLATILE
     * Từ khóa volatile làm vô hiệu hóa CPU Cache cho biến này.
     * Worker Thread thoát lập tức khi Main Thread vừa đổi flag = true!
     */
    public static void demoWithVolatile() throws InterruptedException {
        Thread worker = new Thread(() -> {
            System.out.println("   [Worker Thread] Đang chạy vòng lặp kiểm tra flag...");
            long count = 0;
            while (!flagWithVolatile) {
                count++;
            }
            System.out.println("   [Worker Thread] Đã nhìn thấy flagWithVolatile = true và THOÁT THÀNH CÔNG! (count = " + count + ")");
        });

        worker.start();

        Thread.sleep(100);
        System.out.println("   [Main Thread] Đã đổi flagWithVolatile = true!");
        flagWithVolatile = true;

        worker.join(1500);

        if (!worker.isAlive()) {
            System.out.println("✅ KẾT QUẢ: Worker Thread THOÁT LẬP TỨC nhờ quy tắc Volatile của JMM!");
        }
    }
}
