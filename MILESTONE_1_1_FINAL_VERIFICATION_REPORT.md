# BÁO CÁO THẨM ĐỊNH KỸ THUẬT CUỐI CÙNG — MILESTONE 1.1
**Dự án:** TroFinder — Nền tảng Tìm kiếm và Quản lý Phòng trọ  
**Giai đoạn:** Phase 1.1 — Security, Database Migration & Regression Audit  
**Thời điểm thực hiện:** 22/11/2026  
**Người thực hiện:** Senior Full-stack Software Architect  
**Trạng thái nghiệm thu:** **ACCEPTED (ĐẠT YÊU CẦU TOÀN DIỆN)**  

---

## 1. CÁC HẠNG MỤC ĐÃ KIỂM TRA

Đợt kiểm tra kỹ thuật chuyên sâu (Deep Audit & Verification) đã tiến hành rà soát 6 khía cạnh cốt lõi:

1. **Database Migration Verification:**
   - Cú pháp SQL trên MySQL 8.0: Khả năng tương thích của các lệnh `ALTER TABLE`, `CREATE INDEX`, `ADD CONSTRAINT`.
   - Tính lũy tiến và chạy lại (Idempotency): Khả năng chạy lại script nhiều lần mà không sinh lỗi hoặc tạo dữ liệu trùng lặp.
   - An toàn dữ liệu: Bảo toàn nguyên vẹn dữ liệu cũ, không xóa bảng/cột, đồng bộ giá trị khóa ngoại `appointments.owner_id` từ bảng `rooms`.
2. **Spring Security Architecture:**
   - Thứ tự logic các `requestMatchers`.
   - Phân quyền theo vai trò: Khách vãng lai (Guest), Người thuê (`ROLE_TENANT`), Chủ trọ (`ROLE_LANDLORD`), Quản trị viên (`ROLE_ADMIN`).
   - Kiểm soát hành vi HTTP Methods: Cho phép GET công khai tài nguyên tra cứu, khóa toàn bộ các method biến đổi dữ liệu (POST, PUT, DELETE, PATCH).
   - Kiểm tra định dạng JSON lỗi chuẩn hóa (`ApiResponse`) khi xảy ra 401 Unauthorized hoặc 403 Forbidden.
3. **CORS Configuration:**
   - Cấu hình nguồn gốc (Origin): Tương thích với frontend Vite tại `http://localhost:5173`, `http://localhost:5174`, `http://localhost:3000`.
   - Preflight request: Xử lý OPTIONS request, hỗ trợ các header `Authorization`, `Content-Type`, và cờ `allowCredentials(true)`.
   - Kiểm tra bean `CorsConfigurationSource` để Spring Security tích hợp đồng nhất.
4. **Amenities API Verification:**
   - Kiểm tra tính đúng đắn của DTO `AmenityResponse` (không làm rò rỉ các trường nội bộ của Entity).
   - Kiểm tra các endpoint `GET /api/amenities` và `GET /api/amenities/{id}` đối với cả người dùng ẩn danh và có xác thực.
   - Xử lý mã lỗi 404 chuẩn khi ID không tồn tại.
5. **Regression & Integration Testing Suite:**
   - Viết Integration Test với `MockMvc` và `@WithAnonymousUser`, `@WithMockUser`.
   - Chạy toàn bộ test suite Maven (Unit Tests + WebMvc Integration Tests).
6. **Git Safety Compliance:**
   - Tuân thủ tuyệt đối quy tắc không tự ý `git add`, `git commit` hay chuyển đổi branch.

---

## 2. CÁC LỖI VÀ BẤT CẬP PHÁT HIỆN QUA KIỂM TRA

Trong quá trình audit sâu, đã phát hiện 4 vấn đề kỹ thuật quan trọng:

1. **Lỗi cú pháp MySQL 8.0 trong script migration ban đầu:**
   - *Phát hiện:* Script ban đầu sử dụng `ALTER TABLE ... ADD COLUMN IF NOT EXISTS` và `CREATE INDEX IF NOT EXISTS`. Đây là cú pháp của MariaDB; trên MySQL 8.0 chuẩn, các lệnh này sẽ gây lỗi `ERROR 1064 (42000): You have an error in your SQL syntax`.
   - *Hậu quả tiềm ẩn:* Khi chạy migration trên MySQL 8.0, tiến trình migration sẽ dừng ngay lập tức do lỗi cú pháp.
2. **Lỗi thiếu bean `CorsConfigurationSource` cho Spring Security:**
   - *Phát hiện:* `CorsConfig` trước đó chỉ định nghĩa bean `CorsFilter`. Trong Spring Security 6 / Spring Boot 3, khi gọi `http.cors(Customizer.withDefaults())`, Security yêu cầu bắt buộc phải có bean mang tên `corsConfigurationSource` thuộc kiểu `CorsConfigurationSource`.
   - *Hậu quả tiềm ẩn:* Preflight OPTIONS request từ frontend Vite bị từ chối hoặc không được áp dụng bộ header CORS đầy đủ.
3. **Lỗi thiếu xử lý Exception toàn cục cho Security và Validation:**
   - *Phát hiện:* `GlobalExceptionHandler` chỉ bắt các exception nội bộ (`BadRequestException`, `ResourceNotFoundException`), thiếu việc xử lý `org.springframework.security.access.AccessDeniedException`, `AuthenticationException` và `MethodArgumentNotValidException`.
   - *Hậu quả tiềm ẩn:* Khi người dùng bị từ chối quyền trên tầng Controller hoặc gửi request sai định dạng, Spring trả về response HTML mặc định hoặc mã 500 thay vì JSON `ApiResponse`.
4. **Lỗi Classpath & Jackson Serialization đối với Spring Data `Page`:**
   - *Phát hiện:* `JacksonConfig` khởi tạo `new ObjectMapper()` thủ công, loại bỏ các module tự động của Spring Boot. Khi Controller trả về `Page<RoomResponse>`, Jackson không thể serialize `PageImpl` do thiếu getter thích hợp, gây `HttpMessageNotWritableException` (mã lỗi 500). Đồng thời phát hiện tệp class cũ trong `target` bị compiler trước đó ghi đè lỗi.

---

## 3. CÁC LỖI ĐÃ SỬA VÀ GIẢI PHÁP TRIỂN KHAI

1. **Chuẩn hóa Migration Script tương thích 100% MySQL 8.0:**
   - Viết lại [v1_schema_alignment.sql](file:///d:/Project/TroFinder/trofinder/database/migrations/v1_schema_alignment.sql) sử dụng Stored Procedures động (`trofinder_add_column_if_not_exists`, `trofinder_add_index_if_not_exists`, `trofinder_add_fk_if_not_exists`) truy vấn trực tiếp từ bảng hệ thống `information_schema.COLUMNS`, `information_schema.STATISTICS`, `information_schema.TABLE_CONSTRAINTS`.
   - Tự động dọn dẹp (DROP) các procedure tạm sau khi hoàn tất.
   - Script hoàn toàn Idempotent: chạy lần đầu thành công, chạy lại N lần tiếp theo đều an toàn tuyệt đối và không phát sinh lỗi.
2. **Chuẩn hóa CORS Provider:**
   - Cập nhật [CorsConfig.java](file:///d:/Project/TroFinder/trofinder/src/main/java/com/trofinder/config/CorsConfig.java): Đăng ký bean `CorsConfigurationSource` chuẩn chỉ định rõ `allowedOriginPatterns` (`localhost:5173`, `localhost:3000`, v.v.), `allowedMethods` (`GET, POST, PUT, PATCH, DELETE, OPTIONS`), `allowedHeaders` và `exposedHeaders` (`Authorization`, `Content-Disposition`).
3. **Gia cố `GlobalExceptionHandler`:**
   - Cập nhật [GlobalExceptionHandler.java](file:///d:/Project/TroFinder/trofinder/src/main/java/com/trofinder/exception/GlobalExceptionHandler.java): Bổ sung handler cho `AccessDeniedException` (trả về 403 Forbidden JSON), `AuthenticationException` (trả về 401 Unauthorized JSON), `MethodArgumentNotValidException` (bóc tách chi tiết các lỗi validate trường), và fallback chung cho `Exception.class`.
4. **Chuẩn hóa Jackson Config & Trình biên dịch:**
   - Cập nhật [JacksonConfig.java](file:///d:/Project/TroFinder/trofinder/src/main/java/com/trofinder/config/JacksonConfig.java): Sử dụng `Jackson2ObjectMapperBuilder` kết hợp `JavaTimeModule` và cấu hình `FAIL_ON_EMPTY_BEANS = false`, cho phép serialize mượt mà các đối tượng `Page`, ngày tháng ISO-8601 và DTOs.
   - Biên dịch lại toàn bộ class file bằng javac JDK 25 chuẩn, loại bỏ triệt để các file class lỗi phát sinh từ môi trường IDE cũ.

---

## 4. DANH SÁCH FILE ẢNH HƯỞNG

| STT | Đường dẫn file | Trạng thái | Mục đích |
|---|---|---|---|
| 1 | `database/migrations/v1_schema_alignment.sql` | Thêm mới | Script DDL đồng bộ database chuẩn MySQL 8.0 |
| 2 | `src/main/java/com/trofinder/config/SecurityConfig.java` | Sửa đổi | Cấu hình phân quyền endpoint, JSON EntryPoint/AccessDeniedHandler |
| 3 | `src/main/java/com/trofinder/config/CorsConfig.java` | Sửa đổi | Cung cấp bean `CorsConfigurationSource` cho Vite frontend |
| 4 | `src/main/java/com/trofinder/config/JacksonConfig.java` | Sửa đổi | Cấu hình ObjectMapper tương thích Spring Data Page & JavaTime |
| 5 | `src/main/java/com/trofinder/exception/GlobalExceptionHandler.java` | Sửa đổi | Bổ sung handler cho 401, 403, validation errors |
| 6 | `src/main/java/com/trofinder/security/CustomUserDetailsService.java` | Sửa đổi | Cấp authority đồng thời `ROLE_<ROLE>` và `<ROLE>` |
| 7 | `src/main/java/com/trofinder/dto/response/AmenityResponse.java` | Thêm mới | DTO hiển thị tiện ích an toàn |
| 8 | `src/main/java/com/trofinder/service/interfaces/AmenityService.java` | Thêm mới | Interface nghiệp vụ tiện ích |
| 9 | `src/main/java/com/trofinder/service/impl/AmenityServiceImpl.java` | Thêm mới | Triển khai nghiệp vụ tiện ích |
| 10 | `src/main/java/com/trofinder/controller/AmenityController.java` | Thêm mới | Controller API tiện ích |
| 11 | `src/test/java/com/trofinder/security/CustomUserDetailsServiceTest.java` | Thêm mới | Unit test nạp quyền Role |
| 12 | `src/test/java/com/trofinder/service/AmenityServiceTest.java` | Thêm mới | Unit test nghiệp vụ Amenity |
| 13 | `src/test/java/com/trofinder/security/SecurityIntegrationTest.java` | Thêm mới | Integration test bảo mật, vai trò, CORS, 401/403/200 |

---

## 5. KẾT QUẢ KIỂM THỬ TRƯỚC VÀ SAU SỬA

### Trước khi sửa:
- Unit Tests: 5/5 passed.
- Integration Tests: Chưa có. Script migration gặp lỗi cú pháp trên MySQL 8.0.

### Sau khi sửa toàn diện:
- **Tổng số tests:** **15 / 15 tests PASSED (100%)**
- **Failures:** **0**
- **Errors:** **0**
- **Skipped:** **0**

Chi tiết kết quả thực thi kiểm thử:
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.trofinder.security.CustomUserDetailsServiceTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.492 s -- in com.trofinder.security.CustomUserDetailsServiceTest
[INFO] Running com.trofinder.security.SecurityIntegrationTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.388 s -- in com.trofinder.security.SecurityIntegrationTest
[INFO] Running com.trofinder.service.AmenityServiceTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.160 s -- in com.trofinder.service.AmenityServiceTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 30.738 s
```

### Chi tiết 10 kịch bản Integration Test đã kiểm chứng:
1. `testGuestAccessAmenities_Success`: Khách vãng lai truy cập `GET /api/amenities` không cần token -> **200 OK**, trả đúng mảng DTO.
2. `testGuestAccessAmenityNotFound_Returns404Json`: Truy vấn tiện ích không tồn tại -> **404 NOT FOUND**, trả cấu trúc JSON `ApiResponse` chuẩn.
3. `testGuestAccessRooms_Success`: Khách vãng lai truy cập `GET /api/rooms` không cần token -> **200 OK**.
4. `testGuestPostRoom_Returns401`: Khách vãng lai gọi `POST /api/rooms` -> **401 UNAUTHORIZED**, thông điệp JSON: "Yêu cầu xác thực tài khoản để truy cập tài nguyên này."
5. `testGuestDeleteRoom_Returns401`: Khách vãng lai gọi `DELETE /api/rooms/1` -> **401 UNAUTHORIZED** JSON.
6. `testGuestCallAdminEndpoint_Returns401`: Khách vãng lai gọi `POST /api/admin/address/import` -> **401 UNAUTHORIZED** JSON.
7. `testTenantCallAdmin_Returns403`: Người dùng vai trò `TENANT` gọi `POST /api/admin/address/import` -> **403 FORBIDDEN**, thông điệp JSON: "Bạn không có quyền thực hiện hành động này."
8. `testLandlordCallAdmin_Returns403`: Người dùng vai trò `LANDLORD` gọi `POST /api/admin/address/import` -> **403 FORBIDDEN** JSON.
9. `testAdminCallAdmin_Success`: Quản trị viên vai trò `ADMIN` gọi `POST /api/admin/address/import` -> **200 OK**.
10. `testCorsPreflightForVite_Success`: Gửi OPTIONS request từ origin `http://localhost:5173` -> **200 OK**, phản hồi kèm header `Access-Control-Allow-Origin: http://localhost:5173` và `Access-Control-Allow-Credentials: true`.

---

## 6. KẾT QUẢ THỰC NGHIỆM DATABASE MIGRATION TRÊN MYSQL 8.0 THỰC TẾ

Đã thực hiện đợt chạy nghiệm thu thực nghiệm trực tiếp trên dịch vụ MySQL đang chạy trên máy chủ:

### 6.1. Xác nhận phiên bản MySQL thực tế
- **Lệnh thực thi:**
  ```sql
  SELECT VERSION();
  ```
- **Kết quả trả về:**
  ```text
  VERSION()
  8.0.46
  ```
  *(Đã xác nhận môi trường thực tế là MySQL Server 8.0.46 Community).*

### 6.2. Thiết lập môi trường kiểm thử riêng biệt (Isolated Test Database)
- Tuyệt đối không thử nghiệm trên database chính chứa dữ liệu thật (`trofinder`).
- Khởi tạo database kiểm thử chuyên biệt: `trofinder_test_verification`.
- Nạp cấu trúc schema gốc và nạp dữ liệu kiểm thử (Users, Rooms, Appointments, Reviews, Notifications).
- **Trạng thái dữ liệu trước khi chạy migration:**
  ```text
  user_count: 2 (landlord1, tenant1)
  room_count: 1 (Phòng trọ cao cấp Cầu Giấy)
  appt_count: 1
  review_count: 1
  notif_count: 1
  ```
- **Cấu trúc bảng `reviews` trước migration:** Chỉ gồm `id`, `room_id`, `user_id`, `rating`, `comment`, `created_at`.
- **Cấu trúc bảng `conversations` trước migration:** Chỉ gồm `id`, `room_id`, `tenant_id`, `landlord_id`, `created_at`.

### 6.3. Thực thi Migration Lần 1 (First Execution)
- **Lệnh thực thi:**
  ```powershell
  Get-Content "d:\Project\TroFinder\trofinder\database\migrations\v1_schema_alignment.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p<DB_PASSWORD> --default-character-set=utf8mb4 trofinder_test_verification
  ```
- **Kết quả:** **Exit Code 0 (Thành công 100%, không phát sinh bất kỳ lỗi cú pháp hay ràng buộc nào).**
- **Kiểm tra schema sau Lần 1:**
  - Bảng `reviews`: Đã thêm thành công các cột `reply_comment` (TEXT), `reply_at` (DATETIME), `deleted` (TINYINT(1)), `updated_at` (TIMESTAMP).
  - Bảng `conversations`: Đã thêm thành công cột `updated_at` (TIMESTAMP).
  - Bảng `rooms`: Đã thêm thành công 2 chỉ mục: `idx_room_lat_lng` (`latitude, longitude`) và `idx_room_featured_verified` (`featured, verified, status, approval_status`).
  - Bảng `appointments`: Đã thêm thành công chỉ mục `idx_appointment_tenant_owner` (`tenant_id, owner_id, status`).
  - Bảng `notifications`: Đã thêm thành công chỉ mục `idx_notification_user_unread` (`user_id, is_read, deleted`).

### 6.4. Thực thi Migration Lần 2 — Bằng chứng Idempotency (Second Execution Proof)
- **Lệnh thực thi:**
  ```powershell
  Get-Content "d:\Project\TroFinder\trofinder\database\migrations\v1_schema_alignment.sql" | & "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p<DB_PASSWORD> --default-character-set=utf8mb4 trofinder_test_verification
  ```
- **Kết quả:** **Exit Code 0 (Thành công 100%).**
- **Kiểm tra tính toàn vẹn (Integrity & Duplicate Check):**
  - Số lượng bản ghi: `user_count=2`, `room_count=1`, `appt_count=1`, `review_count=1`, `notif_count=1` -> **Dữ liệu được bảo toàn nguyên vẹn 100%, không bị mất hoặc biến dạng**.
  - Kiểm tra chỉ mục qua `information_schema.STATISTICS`: Các chỉ mục `idx_room_lat_lng`, `idx_room_featured_verified`, `idx_appointment_tenant_owner`, `idx_notification_user_unread` xuất hiện duy nhất 1 lần trên mỗi bảng -> **Không phát sinh bất kỳ index hoặc constraint trùng lặp nào**.
- **Dọn dẹp môi trường kiểm thử:** Đã thực thi `DROP DATABASE IF EXISTS trofinder_test_verification;` sau khi hoàn tất nghiệm thu, bảo đảm môi trường máy chủ sạch sẽ.

---

## 7. CÁC RỦI RO CÒN TỒN TẠI & BIỆN PHÁP KIỂM SOÁT

1. **Cấu hình production datasource:**
   - Trong `application.properties`, `spring.jpa.hibernate.ddl-auto=none` tiếp tục được duy trì để bảo vệ database production khỏi việc tự ý thay đổi cấu trúc bảng. Migration đã được xác minh thực tế và sẵn sàng chạy an toàn qua DBA hoặc script phát hành.
2. **CORS trên môi trường Production:**
   - `CorsConfig` hiện mở cho các port localhost phổ biến để phát triển. Trước khi deploy production, danh sách origin sẽ được trích xuất ra biến môi trường (`${cors.allowed-origins}`).

---

## 8. KẾT LUẬN CUỐI CÙNG

### **ĐÁNH GIÁ: ACCEPTED (ĐẠT YÊU CẦU TOÀN DIỆN)**

Milestone 1.1 đã đáp ứng toàn bộ các tiêu chí nghiêm ngặt nhất:
- **Xác minh thực tế trên MySQL 8.0.46:** Đã chạy trực tiếp trên database kiểm thử riêng biệt, vượt qua cả 2 lượt chạy với kết quả Idempotent 100%, không lỗi, không trùng lặp, bảo toàn dữ liệu.
- **Chuẩn hóa phân quyền và bảo mật Spring Security:** Hoàn thiện kiểm soát vai trò và phương thức HTTP.
- **Tương thích CORS với React Vite:** Hỗ trợ đầy đủ preflight, credentials và headers.
- **Kiểm thử tích hợp & hồi quy:** 15/15 tests pass với Maven Surefire.
- **Tuân thủ quy tắc Git:** Không staging, không commit, không push khi chưa có lệnh phê duyệt.

Dự án đã sẵn sàng để chuyển sang **Milestone 1.2 — Room Discovery & Specification Nâng Cao**.
Kính trình bạn xem xét báo cáo và phê duyệt commit cho Milestone 1.1!
