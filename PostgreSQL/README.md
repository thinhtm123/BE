# 🐘 PostgreSQL Knowledge Base & Roadmap

Thư mục `PostgreSQL/` chứa các tài liệu tổng hợp, ghi chép và hướng dẫn thực hành về hệ quản trị cơ sở dữ liệu quan hệ mã nguồn mở **PostgreSQL** dành cho lập trình viên Backend.

---

## 📚 Danh Mục Tài Liệu

1. 🐘 **[PostgreSQL Overview & Core Fundamentals](./postgresql_overview.md)**
   - Bản chất Object-Relational Database (ORDBMS).
   - 4 Trụ cột cốt lõi: Tính toàn vẹn ACID, Kiểu dữ liệu hiện đại (JSONB, Arrays, UUID), Quản lý đồng thời MVCC, Hệ thống Index (B-Tree, GIN, GiST, BRIN).
   - Bảng so sánh kinh điển: **PostgreSQL vs MySQL**.
   - Cheatsheet các lệnh `psql` thông dụng & Cú pháp tạo bảng chuẩn Backend.

2. 📊 **[01. Rows & Columns in PostgreSQL (Hàng & Cột)](./01_rows_and_columns.md)**
   - Khái niệm nền tảng về Hàng (Records/Tuples) và Cột (Attributes/Fields).
   - Chi tiết về Cột: Kiểu dữ liệu, các ràng buộc (`PRIMARY KEY`, `NOT NULL`, `UNIQUE`, `DEFAULT`, `CHECK`, `REFERENCES`), câu lệnh DDL quản lý cột.
   - Chi tiết về Hàng: Định danh duy nhất, các cột hệ thống ẩn (`ctid`, `xmin`, `xmax`), câu lệnh DML (`INSERT`, `SELECT`, `UPDATE`, `DELETE`, `RETURNING`, `UPSERT`).
   - Ma trận so sánh Rows vs Columns & Best practices thiết kế bảng chuẩn cho Backend Developer.

3. 📁 **[02. Schemas in PostgreSQL (Không Gian Tên & Cấu Trúc)](./02_schemas.md)**
   - Bản chất của Schema: Không gian tên (Namespace) và mô hình phân cấp tổ chức Database.
   - Schema mặc định `public` và thay đổi bảo mật từ PostgreSQL 15+.
   - Cơ chế phân giải đường dẫn tìm kiếm `search_path`.
   - Các trường hợp ứng dụng thực tế: Phân chia Module, kiến trúc Multi-tenancy (mỗi khách một schema), phân quyền bảo mật (RBAC).
   - Các câu lệnh DDL/DCL quản lý schema và phím tắt `psql` (`\dn`, `\dn+`).

4. 🗄️ **[03. Databases in PostgreSQL (Cơ Sở Dữ Liệu)](./03_databases.md)**
   - Bản chất của Database trong kiến trúc Server Cluster & Tính chất cô lập dữ liệu tuyệt đối (Strict Isolation).
   - Các database hệ thống mặc định (`postgres`, `template1`, `template0`).
   - Bảng so sánh kinh điển: **Database vs Schema** (khi nào nên tách Database, khi nào nên tách Schema).
   - Các câu lệnh SQL quản trị (`CREATE DATABASE`, `ALTER`, `DROP DATABASE ... WITH (FORCE)`).
   - Thao tác nhanh với CLI `psql` (`\l`, `\c`) và các công cụ Terminal (`createdb`, `dropdb`, `pg_dump`, `pg_restore`).

5. 🧩 **[04. Object Model in PostgreSQL (Mô Hình Đối Tượng)](./04_object_model.md)**
   - Khái niệm Object-Relational Database (ORDBMS): Sự giao thoa giữa SQL truyền thống và Tư duy Hướng đối tượng (OOP).
   - 3 Trụ cột cốt lõi: Kiểu dữ liệu tự định nghĩa (Composite Types & ENUMs), Kế thừa bảng (`INHERITS` & từ khóa `ONLY`), Đa hình & Nạp chồng hàm (Function Overloading & Polymorphic Types).
   - Đánh giá thực chiến: Ưu điểm, hạn chế và lời khuyên thiết kế CSDL cho Backend Developer.

6. 🔗 **[05. Relational Model (Mô Hình Dữ Liệu Quan Hệ)](./05_relational_model.md)**
   - Nền tảng lý thuyết của Edgar F. Codd (1970): Bảng (Relations), Hàng (Tuples), Cột (Attributes).
   - Bảng đối chiếu thuật ngữ Toán học vs SQL thực tế.
   - 3 Quy tắc ràng buộc toàn vẹn: Toàn vẹn thực thể (Primary Key), Toàn vẹn tham chiếu (Foreign Key), Toàn vẹn miền giá trị (Data Types, Check).
   - Các loại quan hệ cơ bản: 1 - 1, 1 - N, và N - N (Bảng trung gian Junction Table).
   - Mối liên hệ giữa Đại số quan hệ và câu lệnh SQL (`SELECT`, `WHERE`, `JOIN`, `UNION`, `INTERSECT`, `EXCEPT`).

7. 🏛️ **[06. High Level Database Concepts (Các Khái Niệm Cấp Cao)](./06_high_level_concepts.md)**
   - ACID (Atomicity, Consistency, Isolation, Durability) — 4 tính chất giao dịch bắt buộc.
   - Normalization — Chuẩn hóa dữ liệu 1NF, 2NF, 3NF để loại bỏ trùng lặp.
   - Indexes — Tăng tốc truy vấn, đánh đổi giữa tốc độ đọc và chi phí ghi.
   - Transactions & Concurrency Control (MVCC, Isolation Levels).
   - Quy trình Database Design: ERD → Logical → Physical Schema.

8. 🛡️ **[07. ACID Properties in PostgreSQL (Bộ 4 Tính Chất Vàng)](./07_acid.md)**
   - Chi tiết 4 tính chất: Atomicity, Consistency, Isolation, Durability.
   - Cách PostgreSQL hiện thực từng tính chất (pg_xact, Constraints, MVCC, WAL + fsync).
   - Bảng so sánh 4 cấp độ cô lập (Isolation Levels) và các hiện tượng bất thường (Dirty Read, Phantom Read...).

9. 📦 **[08. Transactions in PostgreSQL (Giao Dịch Cơ Sở Dữ Liệu)](./08_transactions.md)**
   - Đơn vị công việc logic (Logical Unit of Work) & Vòng đời giao dịch (Active $\to$ Committed / Aborted).
   - Lệnh điều khiển: `BEGIN`, `COMMIT`, `ROLLBACK`, và quản lý điểm lưu cục bộ với `SAVEPOINT`.
   - Autocommit vs Explicit Transaction; Cảnh báo lỗi Long-running Transactions và Deadlocks.

10. ⚡ **[09. Multi-Version Concurrency Control - MVCC (Đa Phiên Bản)](./09_mvcc.md)**
    - Triết lý: "Đọc không bao giờ chặn Ghi, Ghi không bao giờ chặn Đọc".
    - Kiến trúc Tuple với các cột ẩn: `xmin`, `xmax`, `ctid` và cơ chế hoạt động của `INSERT`/`UPDATE`/`DELETE`.
    - Cơ chế Snapshot Isolation; Vấn đề Dead Tuples, Table Bloat và giải pháp `VACUUM` / `AUTOVACUUM`.
    - Hiện tượng Transaction ID Wraparound và cơ chế Freeze.

11. 📝 **[10. Write-Ahead Log - WAL (Nhật Ký Ghi Trước)](./10_write_ahead_log.md)**
    - Nguyên lý ARIES: Ghi log tuần tự (Sequential I/O) trước khi ghi đĩa dữ liệu (Random I/O).
    - Vai trò của Checkpoint và cơ chế Crash Recovery (REDO log).
    - Các ứng dụng nâng cao: Streaming Replication (Master-Replica), Point-In-Time Recovery (PITR), Change Data Capture (CDC).

12. 🔍 **[11. Query Processing Engine (Vòng Đời Xử Lý Truy Vấn)](./11_query_processing.md)**
    - 4 Giai đoạn xử lý truy vấn: Parser $\to$ Analyzer & Rewriter $\to$ Planner/Optimizer $\to$ Executor.
    - Bộ tối ưu dựa trên chi phí (Cost-based Optimizer) & Các thuật toán Scan (`Seq Scan`, `Index Scan`) và Join (`Nested Loop`, `Hash Join`, `Merge Join`).
    - Hướng dẫn thực hành đo đạc với `EXPLAIN (ANALYZE, BUFFERS)` và lời khuyên tối ưu cho Backend Developer.

13. 🔌 **[12. Connect Using `psql` CLI (Thao Tác Dòng Lệnh Cốt Lõi)](./12_connect_using_psql.md)**
    - Cú pháp kết nối Local, Docker, Remote và chuẩn Connection String URI.
    - Bộ lệnh Meta-Commands thiết yếu: `\q`, `\l`, `\c`, `\dt`, `\d`, `\dn`, `\du`.
    - Pro-tips cho Backend Dev: Đo lường truy vấn với `\timing`, định dạng hiển thị `\x auto`, thực thi file script `-f` và lệnh nhanh `-c`.

14. 🐳 **[13. Using Docker with PostgreSQL (Container Hóa)](./13_using_docker.md)**
    - Lệnh chạy nhanh `docker run` chuẩn kèm ánh xạ Port & gắn Volume lưu trữ dữ liệu bền vững.
    - Mẫu file `docker-compose.yml` chuẩn cho dự án Backend kèm cấu hình `healthcheck`.
    - Cách kết nối từ máy Host và thao tác trực tiếp bên trong container (`docker exec`).

15. ⚙️ **[14. Managing Postgres (systemd, pg_ctl, pg_ctlcluster)](./14_managing_postgres.md)**
    - Quản trị dịch vụ Linux qua `systemd` (`status`, `start`, `restart`, `reload`).
    - Công cụ gốc `pg_ctl` và 3 chế độ tắt database (`smart`, `fast`, `immediate`).
    - Quản lý đa phiên bản trên Ubuntu/Debian bằng `pg_ctlcluster` & Checklist xử lý sự cố.

16. ☁️ **[15. Deployment in Cloud (Triển Khai Đám Mây & DBaaS)](./15_deployment_in_cloud.md)**
    - So sánh Self-hosted vs Managed DBaaS (AWS RDS, GCP Cloud SQL, Supabase, Neon).
    - Các nguyên tắc an toàn: Connection Pooling (PgBouncer), bắt buộc SSL (`sslmode=require`), bảo mật Private VPC.




