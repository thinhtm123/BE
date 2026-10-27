# ⚡ Chuyên Đề: Caching & Tối Ưu Hóa Hiệu Năng Hệ Thống (Caching Architecture)

Thư mục này tập trung vào các kiến thức toàn diện về **Kỹ thuật Caching (Bộ nhớ đệm)** — "vũ khí tối thượng" giúp hệ thống Backend giảm độ trễ từ hàng trăm mili-giây xuống dưới 1 mili-giây, gánh hàng triệu lượt truy cập đồng thời và giải phóng cơ sở dữ liệu khỏi nguy cơ quá tải.

---

## 📚 Danh Mục Tài Liệu

1. ⚡ **[Caching Fundamentals (Bản Chất, Thuật Ngữ & Toàn Cảnh Caching)](./01_caching_fundamentals.md)**
   - Định nghĩa cốt lõi & Ẩn dụ "Ngăn kéo bàn làm việc vs Kho tầng hầm".
   - Bảng độ trễ phần cứng kinh điển (*Latency Numbers Every Programmer Should Know*).
   - Bộ thuật ngữ bắt buộc: **Cache Hit, Cache Miss, Hit Ratio, TTL, Eviction**.
   - Toàn cảnh các tầng Caching từ Client, CDN, Nginx, App Memory đến Distributed Cache (Redis) và Database Buffer Pool.
   - Câu nói bất hủ của Phil Karlton: *"Chỉ có 2 thứ khó nhất trong ngành Khoa học Máy tính: Hủy bộ đệm (Cache Invalidation) và Đặt tên biến."*

2. 📱 **[Client-Side Caching (Lưu Trữ Đệm Phía Máy Khách)](./02_client_side_caching.md)**
   - Triết lý: Request nhanh nhất là request không bao giờ phải gửi qua mạng (0ms latency).
   - Giải mã Header `Cache-Control`: `max-age`, `immutable`, `public/private`, và hiểu lầm về `no-cache` vs `no-store`.
   - Cơ chế thăm dò lại (**Conditional Requests**): `ETag` (mã băm nội dung), `Last-Modified` và mã phản hồi tiết kiệm băng thông **`HTTP 304 Not Modified`**.
   - Kỹ thuật **Cache Busting (Content Hashing)** giải bài toán hóc búa Cache Invalidation khi deploy code mới.
   - Các API lưu trữ trên thiết bị: Service Worker (PWA offline), IndexedDB (Database NoSQL trên trình duyệt), LocalStorage.

3. 🔴 **[Redis: "Ông Vua" In-Memory Caching & Data Structures](./03_redis_fundamentals.md)**
   - Vì sao Redis nhanh như chớp (100.000+ ops/s): In-Memory, Kiến trúc Single-Threaded Event Loop (I/O Multiplexing), Cấu trúc dữ liệu tối ưu bằng C.
   - 5 Cấu trúc dữ liệu cốt lõi: Strings (Atomic counters), Hashes (Objects), Lists (Queues), Sets (Unique/Intersection), Sorted Sets (ZSet Leaderboard).
   - Tính bền vững (Persistence): RDB (Snapshot định kỳ) vs AOF (Append-Only File) & Hybrid Persistence.
   - Sẵn sàng cao & Mở rộng: Redis Sentinel (Tự động Failover bầu Master) và Redis Cluster (Sharding qua 16.384 Hash Slots).
   - Các thuật toán dọn rác khi RAM bị đầy (**Eviction Policies**: `allkeys-lru`, `volatile-ttl`, `noeviction`).
