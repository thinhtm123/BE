import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * DEMO VIRTUAL THREADS (JAVA 21+) VS PLATFORM THREADS TRUYỀN THỐNG
 * Sức mạnh: Cho phép tạo và chạy HÀNG CHỤC NGHÌN luồng đồng thời mà không tốn tài nguyên RAM/CPU.
 */
public class VirtualThreadsDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("DEMO SỨC MẠNH VIRTUAL THREADS (JAVA 21+)");
        System.out.println("==================================================\n");

        int taskCount = 10000; // 10.000 tác vụ đồng thời

        // 1. Chạy 10.000 task với Virtual Threads (Java 21)
        runWithVirtualThreads(taskCount);

        System.out.println("\n--------------------------------------------------\n");

        // 2. Chạy 10.000 task với Platform Threads truyền thống (Thread Pool 100 threads)
        runWithPlatformThreadsPool(taskCount);
    }

    /**
     * PHẦN 1: CHẠY VỚI VIRTUAL THREADS (Java 21+)
     * JVM tạo 10.000 Virtual Threads siêu nhẹ.
     * Khi Virtual Thread gặp I/O blocking (Thread.sleep / gọi Database / gọi API),
     * nó tự động nhường Carrier Thread (OS Thread) cho Virtual Thread khác mượn sử dụng!
     */
    public static void runWithVirtualThreads(int taskCount) {
        System.out.println(">>> [1] CHẠY " + taskCount + " TASKS VỚI VIRTUAL THREADS <<<");
        long startTime = System.currentTimeMillis();

        // Executors.newVirtualThreadPerTaskExecutor() xuất hiện chính thức từ Java 21
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, taskCount).forEach(i -> {
                executor.submit(() -> {
                    simulateBlockingIo(); // Chờ I/O 1 giây
                    return i;
                });
            });
        } // Try-with-resources tự động gọi shutdown() và join() chờ cả 10.000 Virtual Threads hoàn thành

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("⚡ TỔNG THỜI GIAN 10.000 VIRTUAL THREADS: " + totalTime + " ms");
    }

    /**
     * PHẦN 2: CHẠY VỚI PLATFORM THREADS TRUYỀN THỐNG (Thread Pool 100 OS Threads)
     * Mỗi OS Thread ngốn ~1MB RAM. Không thể tạo 10.000 OS Threads cùng lúc (sẽ OutOfMemoryError).
     * Do đó phải giới hạn Pool 100 threads ➡️ 10.000 tasks phải xếp hàng chạy 100 đợt!
     */
    public static void runWithPlatformThreadsPool(int taskCount) {
        System.out.println(">>> [2] CHẠY " + taskCount + " TASKS VỚI PLATFORM THREAD POOL (100 THREADS) <<<");
        long startTime = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newFixedThreadPool(100)) {
            IntStream.range(0, taskCount).forEach(i -> {
                executor.submit(() -> {
                    simulateBlockingIo(); // Chờ I/O 1 giây
                    return i;
                });
            });
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("⏱️  TỔNG THỜI GIAN PLATFORM THREAD POOL (100 THREADS): " + totalTime + " ms");
    }

    /**
     * Giả lập một tác vụ chờ I/O ngốn 1 giây (ví dụ: Gọi Database, Đọc file, Gọi API bên thứ 3)
     */
    private static void simulateBlockingIo() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
