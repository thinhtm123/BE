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

17. 📖 **[16. SQL Concepts & Classification (Khái Niệm & Phân Loại SQL)](./16_sql_concepts.md)**
    - Bản chất ngôn ngữ khai báo (Declarative Language - "What" vs "How").
    - 5 Phân nhánh SQL cốt lõi: DQL (`SELECT`), DML (`INSERT/UPDATE/DELETE`), DDL (`CREATE/ALTER/DROP`), DCL (`GRANT/REVOKE`), TCL (`COMMIT/ROLLBACK`).
    - So sánh kinh điển: DDL vs DML và `TRUNCATE` vs `DELETE`.

18. 🧠 **[18. Subqueries & SQL Tips/Tricks (Mẹo Dễ Nhớ Cho Backend)](./18_subqueries_and_sql_tricks.md)**
    - Mental Models thần chú: Phân biệt triệt để `WHERE` vs `GROUP BY ... HAVING`.
    - Xóa dữ liệu an toàn với `RETURNING *` (tránh xóa nhầm toàn bảng).
    - 4 Dạng Subquery cốt lõi: Lọc động (`> AVG`), `IN / NOT IN`, Correlated Subquery, và tối ưu hiệu năng với `EXISTS`.
    - CTE (`WITH` clause) — Kỹ thuật biến câu SQL dài ngoằng trở nên sáng sủa, dễ bảo trì.
    - `LATERAL JOIN` — Vòng lặp `for-each` trong SQL giải quyết bài toán Top N per group.

19. 🔢 **[19. Set Operations (Các Phép Toán Tập Hợp)](./19_set_operations.md)**
    - So sánh trực quan `JOIN` (nối ngang theo cột) vs `Set Operations` (nối dọc theo dòng).
    - 3 Phép toán cốt lõi: `UNION / UNION ALL` (Hợp), `INTERSECT` (Giao), `EXCEPT` (Hiệu).
    - Bí quyết hiệu năng: Khi nào nên ưu tiên `UNION ALL` thay vì `UNION`.

20. ⚙️ **[20. Configuring PostgreSQL (Bộ 3 File Cấu Hình Sống Còn)](./20_configuring_postgresql.md)**
    - Phân vai "Bộ 3 Quyền Lực": `postgresql.conf` (Động cơ/RAM), `pg_hba.conf` (Bảo vệ gác cổng), `pg_ident.conf` (Ánh xạ user).
    - Các thông số tối ưu RAM cốt lõi: `shared_buffers` (25% RAM), `work_mem` (tối ưu SORT/JOIN).
    - Cơ chế bảo mật HBA (IP whitelist, phương thức `trust` vs `scram-sha-256`) và lệnh `SELECT pg_reload_conf();` không gián đoạn hệ thống.

21. 📊 **[21. Reporting & Logging Statistics (Giám Sát & Thống Kê)](./21_logging_and_statistics.md)**
    - Kỹ thuật bắt Slow Queries (`log_min_duration_statement = 200`).
    - Bảng xếp hạng câu query ngốn tài nguyên nhất qua extension `pg_stat_statements`.
    - Bộ 3 View chẩn đoán sức khỏe: `pg_stat_activity` (bắt tiến trình treo), `pg_stat_user_tables` (phát hiện bảng thiếu Index), và tính tỉ lệ đọc từ RAM (`cache_hit_ratio >= 99%`).

22. 🧩 **[22. Adding Extensions in PostgreSQL (Mở Rộng Sức Mạnh Database)](./22_adding_extensions.md)**
    - Bản chất Extension: Hệ thống "Plugin / Thư viện" mở rộng kiểu dữ liệu, hàm và Index.
    - Bộ lệnh quản trị: `CREATE EXTENSION IF NOT EXISTS`, `\dx`, `DROP EXTENSION`.
    - Top 5 Extensions kinh điển: `uuid-ossp` (sinh ID bảo mật), `pgcrypto` (mã hóa mật khẩu), `pg_trgm` (tìm kiếm mờ/gợi ý chính tả), `PostGIS` (bản đồ định vị), và `pgvector` (AI RAG embeddings).

23. 🛡️ **[23. PostgreSQL Security Concepts (Bảo Mật Đa Tầng)](./23_postgresql_security.md)**
    - Mô hình 5 tầng bảo mật: Network (`pg_hba.conf`, SSL), Authentication (`scram-sha-256`), Authorization (`GRANT/REVOKE` quyền tối thiểu), Row-Level Security (`RLS`), và Encryption (`pgcrypto`).
    - Tính năng RLS: Cơ chế cách ly dữ liệu từng khách hàng (Multi-tenancy) ngay từ tầng CSDL.
    - Checklist an ninh bắt buộc trước khi deploy Production.

24. 🔑 **[24. Object Privileges in PostgreSQL (Phân Quyền Đối Tượng)](./24_object_privileges.md)**
    - Ma trận quyền hạn chi tiết trên: `TABLE`, `VIEW`, `SEQUENCE`, `FUNCTION`, `SCHEMA`.
    - Cú pháp `GRANT` (cấp quyền) và `REVOKE` (thu hồi quyền).
    - "Cái bẫy" quyền sequence khi INSERT cột `SERIAL` (`GRANT USAGE ON ALL SEQUENCES`).
    - Tự động cấp quyền cho các bảng mới tạo trong tương lai qua `ALTER DEFAULT PRIVILEGES`.

25. 🔐 **[25. Authentication Models in PostgreSQL (Các Mô Hình Xác Thực)](./25_authentication_models.md)**
    - Bản đồ các phương thức kiểm tra danh tính trong `pg_hba.conf`.
    - Chuẩn mật khẩu bảo mật cao: `scram-sha-256` (chuẩn vàng) vs `md5` (lỗi thời) vs `trust` (chỉ dùng test).
    - Xác thực tập trung doanh nghiệp: `LDAP / Active Directory`, `GSSAPI/Kerberos` (Single Sign-On), `Certificate` (SSL Client Cert), và `PAM`.

26. 👥 **[26. PostgreSQL Roles & Attributes (Quản Lý Định Danh & Thuộc Tính)](./26_postgresql_roles.md)**
    - Hợp nhất User và Group thành thực thể duy nhất: `Login Role` (có cờ `LOGIN`) vs `Group Role` (`NOLOGIN`).
    - Các thuộc tính cấp cao của Role: `SUPERUSER`, `CREATEDB`, `CREATEROLE`, `INHERIT`, `REPLICATION`, và `CONNECTION LIMIT`.
    - Bộ lệnh CRUD hoàn chỉnh: `CREATE ROLE`, `ALTER ROLE`, và quy trình xóa Role an toàn (`REASSIGN OWNED` $\to$ `DROP OWNED` $\to$ `DROP ROLE`).

27. 🔒 **[27. SSL Settings in PostgreSQL (Mã Hóa Đường Truyền SSL/TLS)](./27_ssl_settings.md)**
    - Vì sao SSL là bắt buộc trên Production (chống nghe lén dữ liệu & tấn công Man-in-the-Middle).
    - Cấu hình phía Server: `ssl = on` trong `postgresql.conf` và từ khóa `hostssl` trong `pg_hba.conf`.
    - Bảng giải mã 6 cấp độ `sslmode` cho chuỗi kết nối Backend: `disable`, `allow`, `prefer`, `require`, `verify-ca`, `verify-full`.

28. 🛡️ **[28. Row Level Security - RLS (Bảo Mật Cấp Độ Từng Dòng Dữ Liệu)](./28_row_level_security.md)**
    - Bản chất RLS: Tự động chèn bộ lọc ngầm vào CSDL (bảo vệ chống rò rỉ dữ liệu khi Backend quên viết `WHERE`).
    - Quy trình 3 bước: `ENABLE ROW LEVEL SECURITY` $\to$ `CREATE POLICY` $\to$ Phân biệt `USING` vs `WITH CHECK`.
    - Ứng dụng đỉnh cao trong kiến trúc phần mềm SaaS / Multi-Tenancy (cô lập dữ liệu giữa các công ty khách hàng).

29. 🏰 **[29. Advanced Topics in PostgreSQL Security (Bảo Mật Nâng Cao)](./29_advanced_security_topics.md)**
    - Audit Logging với `pgaudit`: Ghi nhật ký kiểm toán bất biến phục vụ thanh tra pháp lý (ai đã sửa gì, lúc nào).
    - Data Masking & Anonymization: Che giấu số thẻ, CCCD khi clone database sang môi trường Dev/Test theo chuẩn GDPR.
    - TDE (Transparent Data Encryption): Mã hóa ổ đĩa chống trộm cắp vật lý.
    - Phòng thủ SQL Injection ở tầng Backend bằng Parameterized Queries.

30. 🛡️ **[30. SELinux & PostgreSQL (Bảo Mật Cấp Độ Kernel Linux)](./30_selinux_and_postgresql.md)**
    - Bản chất SELinux: Cơ chế kiểm soát truy cập bắt buộc (MAC) ở tầng nhân Linux.
    - Cơ chế Sandbox cô lập: Giới hạn tiến trình PostgreSQL chỉ được truy cập thư mục data và cổng 5432 (dù bị hack cũng không thể thoát ra phá hoại OS).
    - 3 Trạng thái hoạt động (`Enforcing`, `Permissive`, `Disabled`) và cách xử lý lỗi phân quyền thư mục mới (`semanage`, `restorecon`).

31. 🔄 **[31. Replication in PostgreSQL (Sao Chép & Đồng Bộ CSDL)](./31_replication_in_postgresql.md)**
    - Mục tiêu cốt lõi: High Availability (Sẵn sàng cao), Fault Tolerance (Chịu lỗi), và Read Scaling (Chia tải đọc).
    - 2 Cơ chế nhân bản: Physical Streaming Replication (đồng bộ WAL cấp độ nhị phân) vs Logical Replication (mô hình Publish/Subscribe theo từng bảng).
    - Đánh đổi kinh điển: Asynchronous Replication (siêu nhanh) vs Synchronous Replication (Zero Data Loss chuẩn ngân hàng).

32. 💾 **[32. Checkpoints & Background Writer (Cơ Chế Ghi Đĩa & Phục Hồi Dữ Liệu)](./32_checkpoints_and_bgwriter.md)**
    - Bản chất Dirty Pages: Dữ liệu sửa trên RAM Shared Buffers chưa kịp lưu xuống đĩa.
    - Vai trò Checkpoint: Mốc lưu dữ liệu an toàn phục vụ Crash Recovery (giảm thời gian khởi động lại sau cúp điện).
    - Background Writer (`bgwriter`): Tiến trình âm thầm ghi rải rác để làm mịn lưu lượng I/O, tránh nghẽn đột ngột.
    - Bộ 3 tham số tinh chỉnh: `checkpoint_timeout`, `max_wal_size`, và `checkpoint_completion_target = 0.9`.




















