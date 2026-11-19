# ShoppingServletJPA

Bài tập Lập trình Web – **Servlet / JSP + JPA (Hibernate)**, SQL Server.

- **Bài 01** (09/11/2026): Login (Session + Cookie "Nhớ tôi"), Register – JPA (entity `User`).
- **Bài 02** (12/11/2026): CRUD Category bằng JPA (entity `Category`), upload ảnh, tìm kiếm.
- **Bài 03** (16/11/2026), gồm:
  1. Kích hoạt tài khoản bằng **OTP gửi qua email** khi đăng ký.
  2. Đăng nhập với **mật khẩu mã hóa BCrypt**, đăng xuất.
  3. **Quên mật khẩu**: gửi mã OTP qua email, đặt lại mật khẩu.
  4. Bảng **Videos** quan hệ 1-n với `users` (1 user có nhiều video): CRUD video, 10 video mới nhất ở `/home`,
     video của user phân trang 6/trang ở `/video`, xem chi tiết + phát video, upload bằng **Servlet Multipart**.

## 1. Cấu hình môi trường

| Thành phần | Yêu cầu |
|---|---|
| JDK | 17 trở lên |
| Tomcat | 10.1.x hoặc 11 (Jakarta EE) |
| SQL Server | TCP/IP port **1433**, bật SQL Server Authentication (user `sa`) |
| IDE | Eclipse IDE for Enterprise Java and Web Developers |

1. Chạy `sql/create-database.sql` trong SSMS (tạo database rỗng `ShoppingServiceJPA`).
2. Sửa `jakarta.persistence.jdbc.password` trong `src/main/resources/META-INF/persistence.xml`.
   Bảng `users`, `categories`, `Videos` do Hibernate tự tạo/cập nhật (`hbm2ddl.auto=update`),
   `AppInitListener` tự nạp dữ liệu mẫu.
3. **Cấu hình gửi email OTP** (xem mục 2).
4. Import Maven project, Run on Server (Tomcat 10.1/11): `http://localhost:8080/ShoppingServletJPA/`.

> Bản cũ đã chạy trước đó: không cần xóa dữ liệu. Các cột mới (active, OTP, user_id, VideoUrl, CreatedDate)
> được Hibernate tự thêm; tài khoản cũ lưu mật khẩu thô sẽ **tự được băm BCrypt ở lần đăng nhập đầu tiên**.

## 2. Cấu hình email (OTP)

1. Sao chép `src/main/resources/mail.properties.example` thành `src/main/resources/mail.properties`.
2. Với Gmail: bật Xác minh 2 bước, tạo **Mật khẩu ứng dụng (App password)** 16 ký tự, rồi điền:
   ```
   mail.username=ten_ban@gmail.com
   mail.password=mat_khau_ung_dung_16_ky_tu
   ```
3. File `mail.properties` đã nằm trong `.gitignore` nên **không bị đẩy lên GitHub**.
4. Nếu chưa tạo `mail.properties`, ứng dụng chạy **chế độ DEV**: không gửi mail thật mà in mã OTP ra tab Console của Eclipse
   (dòng `[MAIL - CHẾ ĐỘ DEV] ... Mã OTP : 123456`) để vẫn test được luồng.

## 3. Tài khoản mẫu

| Username | Password | Role | Sau khi đăng nhập |
|---|---|---|---|
| admin | admin123 | 1 | `/admin/home` |
| manager | manager123 | 2 | `/manager/home` |
| user | user123 | 5 | `/home` (có 7 video mẫu) |

## 4. Các URL

| URL | Chức năng |
|---|---|
| `/register` → `/verify-account?u=` | Đăng ký, nhập OTP kích hoạt (có nút Gửi lại mã) |
| `/login`, `/logout` | Đăng nhập (BCrypt, Session + Cookie "Nhớ tôi"), đăng xuất |
| `/forgot-password` → `/reset-password` | Quên mật khẩu: nhập email, nhập OTP + mật khẩu mới |
| `/home` | 10 video mới nhất |
| `/video?page=` | Video của tôi, 6 video / trang |
| `/video/add`, `/video/insert` | Thêm video (upload file video + poster hoặc nhập link) |
| `/video/edit?id=`, `/video/update` | Sửa video |
| `/video/delete?id=` | Xóa video (xóa luôn file đã upload) |
| `/video/detail?id=` | Chi tiết + phát video (file mp4/webm/ogg, link mp4 hoặc YouTube) |
| `/admin/categories`... | CRUD Category (admin) |
| `/image?fname=`, `/media?fname=` | Tải ảnh/video đã upload (thư mục `<user.home>/upload`, có hỗ trợ tua video) |

Quy tắc OTP: 6 chữ số, hiệu lực 5 phút, sai tối đa 5 lần, gửi lại sau 60 giây, OTP cũng được lưu dạng băm.

## 5. Kịch bản test (quay video)

1. **Đăng ký** tài khoản mới → nhận mail OTP → nhập OTP → kích hoạt → đăng nhập. Thử đăng nhập khi *chưa* kích hoạt (bị chặn).
2. Kiểm tra SSMS: `SELECT username, password, active FROM users;` → cột password là chuỗi bắt đầu `$2a$`.
3. **Quên mật khẩu** → nhập email → nhận OTP → đặt mật khẩu mới → đăng nhập bằng mật khẩu mới. Đăng xuất.
4. Đăng nhập → `/video` → **Thêm video** (upload mp4 + poster) → xem ở `/home` và `/video`.
5. Thêm đủ 7+ video để thấy **phân trang 6/trang**. Bấm tiêu đề video để **xem chi tiết & phát**.
6. **Sửa** (đổi poster, đổi tiêu đề), **Xóa** video. Thử mở `/video/edit` của video người khác → 403.

## 6. Cấu trúc

```
vn.iotstar.config      JpaConfig (EntityManagerFactory dùng chung), Test
vn.iotstar.entity      User, Category, Video   (User 1 - n Video, Category 1 - n Video)
vn.iotstar.dao(.impl)  UserDao, ICategoryDao, IVideoDao  (JPA: EntityManager, JPQL)
vn.iotstar.service     UserService (OTP, BCrypt), ICategoryService, IVideoService
vn.iotstar.controller  Login, Register, VerifyAccount, ForgotPassword, ResetPassword, Home, Video, Category...
vn.iotstar.util        PasswordUtil (BCrypt), MailUtil (SMTP), UploadUtil (Part.write), Constant
vn.iotstar.filter      AuthFilter (/admin, /manager, /video)
```

## 7. Đưa lên GitHub

```
git add .
git commit -m "Bai 03: OTP email, BCrypt, quen mat khau, CRUD Videos"
git push
```
Trước khi push: đổi mật khẩu `sa` trong `persistence.xml` về giá trị giả, và chắc chắn `mail.properties` KHÔNG nằm trong `git status`.

---
## Bài tập 03: Sitemesh 3 + Bootstrap, Validation, Profile

**1. Sitemesh 3** (`SiteMeshConfigFilter`): `/admin/*`, `/manager/*` dùng `views/decorators/admin.jsp` (sidebar), còn lại dùng `views/decorators/web.jsp` (navbar + footer). Các JSP chỉ giữ `<title>` và nội dung. Giao diện Bootstrap 5.3 + Bootstrap Icons (CDN), CSS/JS riêng ở `webapp/static`.

**2. Validation**: server-side trong `util/ValidationUtil` (login, register, forgot/reset password, thêm/sửa video, thêm/sửa danh mục, profile); client-side bằng HTML5 + class `needs-validation` (`static/js/site.js`). Lỗi hiện dưới từng ô nhờ tag `WEB-INF/tags/field.tag`.

**3. Profile** (`/profile`): sửa họ tên, SĐT, email, ảnh. Đổi email thì OTP được gửi tới **email mới**, nhập đúng OTP mới lưu (`/profile/verify`). Ảnh upload multipart lên Cloudinary.

Cấu hình Cloudinary: copy `src/main/resources/cloudinary.properties.example` thành `cloudinary.properties` rồi điền `cloud_name`, `api_key`, `api_secret` (file thật đã nằm trong `.gitignore`).
Sau khi pull: Maven → Update Project (Force Update) để tải `sitemesh` và `cloudinary-http45`.
