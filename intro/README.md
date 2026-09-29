# 🚀 Kiến Thức Nền Tảng (Computer Science & Networking Fundamentals)

Thư mục `intro/` chứa các tài liệu tổng hợp và nghiên cứu chuyên sâu về các kiến thức cốt lõi (Computer Science, Mạng máy tính, Hệ điều hành, Giao thức truyền thông) làm nền tảng cho việc phát triển hệ thống Backend hiệu năng cao và bảo mật.

---

## 📚 Danh Mục Tài Liệu

1. 🌐 **[How Does the Internet Work? (Internet Hoạt Động Như Thế Nào?)](./how_the_internet_works.md)**
   - Tổng quan toàn diện về hạ tầng mạng, gói tin, router, mô hình OSI/TCP-IP, vòng đời của một Web Request.

2. 🏷️ **[Domain Name (Tên Miền)](./domain_name.md)**
   - Định nghĩa, cấu trúc thứ bậc (TLD, SLD, Subdomain), các đơn vị quản lý (ICANN, Registry, Registrar).

3. 🖥️ **[Web Hosting (Lưu Trữ Web)](./hosting.md)**
   - Khái niệm, so sánh Shared Hosting, VPS, Dedicated Server, Cloud Hosting, ẩn dụ mối liên hệ với Domain & Website.

4. 📡 **[DNS - Domain Name System (Hệ Thống Phân Giải Tên Miền)](./dns.md)**
   - Mô hình phân cấp 4 tầng máy chủ (Resolver, Root, TLD, Authoritative), các bản ghi cốt lõi (A, AAAA, CNAME, MX, TXT), TTL & Cache.

5. 🧭 **[Web Browsers (Trình Duyệt Web)](./browsers.md)**
   - Kiến trúc trình duyệt, các Rendering/JavaScript Engines (Blink, V8, Gecko, WebKit), Critical Rendering Path, cơ chế bảo mật (Sandbox, SOP, HTTPS).

6. ⚡ **[HTTP & HTTPS (Giao Thức Truyền Tải Siêu Văn Bản)](./http.md)**
   - Mô hình Request-Response, định dạng thông điệp, bản chất Stateless, các HTTP Methods/Status codes và so sánh HTTP vs HTTPS.

7. 🔌 **[APIs - Application Programming Interface (Giao Diện Lập Trình Ứng Dụng)](./api_fundamentals.md)**
   - Ẩn dụ kinh điển "Người bồi bàn nhà hàng" kết nối Client (Khách) và Backend/DB (Nhà bếp).
   - 3 Trụ cột cốt lõi: Endpoints (URL), HTTP Methods (GET, POST, PUT, PATCH, DELETE tương ứng CRUD), và Data Formats (JSON thống trị vs XML).
   - Tổng quan các kiến trúc: RESTful, GraphQL, gRPC, WebSocket.

8. 🌐 **[REST & RESTful API (Kiến Trúc Chuẩn Cho Web API)](./rest_api.md)**
   - 6 Ràng buộc kiến trúc (Constraints) của REST: Client-Server, Stateless, Cacheable, Uniform Interface, Layered System.
   - Bản chất "Stateless": Server không lưu session, mở rộng ngang (Horizontal Scaling) mượt mà.
   - Quy chuẩn đặt tên URI (Danh từ số nhiều, cấm dùng động từ) & Ma trận HTTP Status Codes (200, 201, 204, 400, 401, 403, 404, 500).

9. 📋 **[JSON:API Specification (Đặc Tả Định Dạng Chuẩn Cho REST API)](./json_api_specification.md)**
   - Phân biệt dùng JSON tùy tiện (Ad-hoc) vs Bản đặc tả chuẩn công nghiệp JSON:API (`jsonapi.org`).
   - Cấu trúc khung xương: `data` (`type`, `id`, `attributes`), `relationships`, `included` (chống N+1 API gọi nhiều lần), và `meta`.
   - Chuẩn Content-Type: `application/vnd.api+json`.

10. ⚔️ **[So Sánh Kiến Trúc API: REST vs SOAP vs gRPC vs GraphQL](./api_architectures_comparison.md)**
    - Phân chia "lãnh địa" thực tế: Không có cái nào ít dùng, chỉ là dùng đúng chỗ!
    - SOAP: Hệ thống Ngân hàng, Core Banking, Tài chính quốc tế (XML/WSDL).
    - gRPC: Vua tốc độ kết nối nội bộ giữa các Microservices (HTTP/2 + Protobuf nhị phân).
    - GraphQL: Facebook phát minh giúp Frontend/Mobile tự do chọn trường dữ liệu (chống Over-fetching & Under-fetching).
    - Bảng so sánh toàn diện 4 kiến trúc.

11. 📜 **[OpenAPI Specification (OAS) & Swagger](./openapi_specification.md)**
    - Bản vẽ thiết kế kỹ thuật (Contract) chuẩn hóa cho REST API bằng YAML/JSON.
    - Phân biệt bản chất: OpenAPI (Quy chuẩn/Tiêu chuẩn) vs Swagger (Bộ công cụ/UI).
    - Hai trường phái thực tế: Code-First (viết code sinh docs) vs Design-First (chốt spec trước, generate code sau).
    - Hệ sinh thái tự động hóa: Swagger UI, Client SDK generation, Mock Server (Prism).

---

*Tài liệu tham khảo chính: [cs.fyi - How does the Internet Work?](https://cs.fyi/guide/how-does-internet-work)*




