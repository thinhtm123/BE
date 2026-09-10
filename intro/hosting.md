# 🖥️ Web Hosting

> 💡 **What is Hosting?**  
> **Hosting** provides server space and resources for storing and delivering websites over the internet. Types include shared hosting, VPS, dedicated hosting, and cloud hosting with scalable resources. Services include infrastructure, domain registration, security, and technical support for reliable website availability.

---

## 1. Bản Chất Của Web Hosting

- Để mọi người trên thế giới có thể truy cập vào website của bạn bất kỳ lúc nào, các file mã nguồn (HTML, CSS, JavaScript, Backend Code, Database, Ảnh, Video) phải được đặt trên một **máy chủ (Server)** có kết nối Internet liên tục 24/7 với địa chỉ IP công khai.
- **Web Hosting** chính là dịch vụ cho thuê không gian lưu trữ và tài nguyên tính toán (CPU, RAM, Ổ cứng, Băng thông) trên các máy chủ này.

---

## 2. Ẩn Dụ Thực Tế: Domain vs Hosting vs Website

```text
 🏠 Địa chỉ nhà  ──> Domain Name  (VD: "Số 123 Đường Công Nghệ" / example.com)
 🏡 Mảnh đất/Nhà ──> Web Hosting  (Nơi chứa gạch, đá, không gian sinh sống / Máy chủ lưu trữ)
 🛋️ Nội thất     ──> Website Data (Code, database, hình ảnh, bài viết hiển thị)
 🗺️ Biển chỉ dẫn ──> DNS System   (Chỉ đường từ Địa chỉ nhà tới đúng Mảnh đất thực tế)
```

---

## 3. Các Loại Web Hosting Phổ Biến

| Loại Hosting | Cơ Chế Hoạt Động | Ưu Điểm | Nhược Điểm | Phù Hợp Cho |
| :--- | :--- | :--- | :--- | :--- |
| **Shared Hosting** | Hàng trăm website nằm chung 1 máy chủ vật lý, dùng chung CPU/RAM/ổ cứng. | Giá rất rẻ, dễ dùng, có sẵn bảng điều khiển (cPanel). | Hiệu năng kém, nếu 1 web bị quá tải hoặc dính mã độc sẽ kéo theo các web khác. | Blog cá nhân, website sinh viên, web giới thiệu nhỏ. |
| **VPS (Virtual Private Server)** | Dùng công nghệ ảo hóa chia 1 máy chủ vật lý thành nhiều máy chủ ảo độc lập với RAM, CPU riêng. | Toàn quyền quản trị Root, tài nguyên đảm bảo, ổn định hơn Shared Hosting. | Cần kiến thức quản trị Linux/Server cơ bản, chi phí trung bình. | Website thương mại điện tử nhỏ/vừa, Backend APIs, ứng dụng doanh nghiệp. |
| **Dedicated Server** | Thuê trọn vẹn nguyên 1 máy chủ vật lý riêng biệt tại Datacenter. | Hiệu năng cực mạnh, bảo mật tối đa, toàn quyền kiểm soát phần cứng. | Chi phí rất cao, đòi hỏi đội ngũ Sysadmin/DevOps chuyên nghiệp để bảo trì. | Hệ thống ngân hàng, cổng thanh toán, nền tảng có lưu lượng truy cập khổng lồ. |
| **Cloud Hosting** | Website chạy trên một cụm (cluster) mạng lưới máy chủ đám mây ảo hóa liên kết với nhau. | **Độ sẵn sàng cao (High Availability)**, tự động co giãn (Auto-scaling), chỉ trả tiền theo dung lượng dùng thực tế. | Chi phí có thể biến động khó lường nếu traffic tăng đột biến. | Startup, ứng dụng SaaS, hệ thống Microservices hiện đại (AWS, GCP, Azure). |

---

## 4. Các Dịch Vụ Đi Kèm Web Hosting

Một gói dịch vụ Web Hosting tiêu chuẩn thường bao gồm:
1. **Lưu trữ & Băng thông (Storage & Bandwidth):** Dung lượng ổ cứng SSD/NVMe và lượng dữ liệu truyền tải tối đa trong tháng.
2. **Cơ sở dữ liệu (Database):** Cung cấp sẵn MySQL, PostgreSQL, MariaDB để lưu trữ dữ liệu nghiệp vụ.
3. **Bảo mật (Security):** Chứng chỉ SSL miễn phí (Let's Encrypt), Web Application Firewall (WAF), quét mã độc, chống tấn công DDoS.
4. **Email Hosting:** Tạo hòm thư theo tên miền riêng (ví dụ: `contact@yourcompany.com`).
5. **Sao lưu dữ liệu (Automated Backup):** Tự động backup định kỳ hàng ngày/hàng tuần để phục hồi khi có sự cố.

---

## 5. Tóm Tắt Nhanh (Key Takeaway)

- **Hosting:** Ngôi nhà lưu trữ code và database, giữ website luôn trực tuyến 24/7.
- **Tiêu chí chọn:** Bắt đầu bằng **Shared Hosting/VPS** cho dự án nhỏ, tiến lên **Cloud Hosting/Kubernetes** khi hệ thống cần mở rộng quy mô (Scale).
