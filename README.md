# AIMS-Ver2025

## Hướng dẫn chạy code
1. Clone repository về máy local
```bash 
git clone https://github.com/trangntt-for-student/AIMS-Ver2025.git
```
Chuyển sang thư mục của project
```bash
cd AIMS-Ver2025
```

2. Build dự án bằng Maven
```bash
mvn clean install
```
Hoặc có thể bỏ qua bước này nếu sử dụng IDE như IntelliJ IDEA hoặc Eclipse để tự động build project.

3. Tạo file cấu hình application.properties trong thư mục `src/main/resources/`
Copy nội dung của file `applicationExample.properties` và dán vào file `application.properties`. Chỉnh sửa các thông tin cấu hình theo nhu cầu của bạn (nếu cần).
Truy cập trang: https://developer.paypal.com/dashboard/ để lấy các thông tin cần thiết cho cấu hình PayPal.
- Bước 1: Tạo tài khoản sandbox (nếu chưa có).
- Bước 2: Tạo ứng dụng mới trong phần Apps & Credentials để lấy Client ID và Secret.

4. Chạy ứng dụng
Sử dụng Maven để chạy ứng dụng:
```bash
mvn spring-boot:run
```
Hoặc chạy trực tiếp từ IDE bằng cách chạy class `com.hust.soict.aims.App.java`.
