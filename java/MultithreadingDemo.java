import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DEMO SỰ KHÁC BIỆT GIỮA ĐƠN LUỒNG (SEQUENTIAL) VÀ ĐA LUỒNG (MULTITHREADED)
 * Kịch bản: Giả lập lấy dữ liệu từ 4 Microservices cho Trang chi tiết sản phẩm Shopee.
 */
public class MultithreadingDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("BẮT ĐẦU DEMO: SO SÁNH HIỆU NĂNG ĐƠN LUỒNG VS ĐA LUỒNG");
        System.out.println("==================================================\n");

        // 1. Chạy thử nghiệm Đơn luồng
        runSequentialDemo();

        System.out.println("\n--------------------------------------------------\n");

        // 2. Chạy thử nghiệm Đa luồng (Parallel)
        runMultithreadedDemo();
    }

    /**
     * PHẦN 1: CHẠY ĐƠN LUỒNG (SEQUENTIAL)
     * Các task chạy lần lượt từ trên xuống dưới, task sau phải chờ task trước xong.
     */
    public static void runSequentialDemo() {
        System.out.println(">>> [1] CHẠY ĐƠN LUỒNG (SEQUENTIAL) <<<");
        long startTime = System.currentTimeMillis();

        String productInfo = fetchProductInfo();   // ~100ms
        String reviews = fetchReviews();           // ~150ms
        String coupons = fetchCoupons();           // ~120ms
        String inventory = fetchInventoryStatus(); // ~80ms

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("-> Kết quả tổng hợp:");
        System.out.println("   + " + productInfo);
        System.out.println("   + " + reviews);
        System.out.println("   + " + coupons);
        System.out.println("   + " + inventory);
        System.out.println("⏱️  TỔNG THỜI GIAN CHẠY ĐƠN LUỒNG: " + totalTime + " ms");
    }

    /**
     * PHẦN 2: CHẠY ĐA LUỒNG (MULTITHREADED / PARALLEL)
     * Sử dụng CompletableFuture và Thread Pool để gọi cả 4 API cùng lúc.
     */
    public static void runMultithreadedDemo() {
        System.out.println(">>> [2] CHẠY ĐA LUỒNG (PARALLEL VỚI COMPLETABLEFUTURE) <<<");
        long startTime = System.currentTimeMillis();

        // Tạo Thread Pool có 4 worker threads
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Kích hoạt 4 task bất đồng bộ trên các luồng riêng biệt
        CompletableFuture<String> futureProduct = CompletableFuture.supplyAsync(MultithreadingDemo::fetchProductInfo, executor);
        CompletableFuture<String> futureReviews = CompletableFuture.supplyAsync(MultithreadingDemo::fetchReviews, executor);
        CompletableFuture<String> futureCoupons = CompletableFuture.supplyAsync(MultithreadingDemo::fetchCoupons, executor);
        CompletableFuture<String> futureInventory = CompletableFuture.supplyAsync(MultithreadingDemo::fetchInventoryStatus, executor);

        // Đợi tất cả 4 task hoàn thành (CompletableFuture.allOf)
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
                futureProduct, futureReviews, futureCoupons, futureInventory
        );

        // Chờ kết quả và tổng hợp dữ liệu
        allTasks.join(); // Chờ đến khi tất cả các luồng hoàn thành

        try {
            String productInfo = futureProduct.get();
            String reviews = futureReviews.get();
            String coupons = futureCoupons.get();
            String inventory = futureInventory.get();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            System.out.println("-> Kết quả tổng hợp:");
            System.out.println("   + " + productInfo);
            System.out.println("   + " + reviews);
            System.out.println("   + " + coupons);
            System.out.println("   + " + inventory);
            System.out.println("⚡ TỔNG THỜI GIAN CHẠY ĐA LUỒNG: " + totalTime + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Đóng Thread Pool
        }
    }

    // =========================================================================
    // CÁC HÀM GIẢ LẬP GỌI MICROSERVICE (MỖI HÀM CÓ ĐỘ TRỄ KHÁC NHAU)
    // =========================================================================

    private static String fetchProductInfo() {
        simulateDelay(100);
        System.out.println("   [" + Thread.currentThread().getName() + "] -> Lấy xong Thông tin Sản phẩm (100ms)");
        return "Sản phẩm: Laptop Dell XPS 15";
    }

    private static String fetchReviews() {
        simulateDelay(150);
        System.out.println("   [" + Thread.currentThread().getName() + "] -> Lấy xong Đánh giá / Reviews (150ms)");
        return "Đánh giá: 4.9/5 sao (120 lượt đánh giá)";
    }

    private static String fetchCoupons() {
        simulateDelay(120);
        System.out.println("   [" + Thread.currentThread().getName() + "] -> Lấy xong Mã giảm giá / Coupons (120ms)");
        return "Voucher: Giam50k, FreeshipMax";
    }

    private static String fetchInventoryStatus() {
        simulateDelay(80);
        System.out.println("   [" + Thread.currentThread().getName() + "] -> Lấy xong Tồn kho / Inventory (80ms)");
        return "Tồn kho: Còn 15 máy";
    }

    private static void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
