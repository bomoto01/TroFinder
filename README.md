# TroFinder

TroFinder là hệ thống tìm kiếm phòng trọ được phát triển bằng Spring Boot.

## Công nghệ sử dụng

- Java 21
- Spring Boot 3
- Spring Security
- JWT Authentication
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- Lombok
- MapStruct

---

## Chức năng đã hoàn thành

### Authentication

- Đăng ký
- Đăng nhập
- JWT
- Phân quyền Role

### Address

- Import dữ liệu tỉnh/thành
- Danh sách Tỉnh
- Danh sách Quận/Huyện
- Danh sách Phường/Xã

### Room

- Đăng phòng
- Xem chi tiết phòng
- Cập nhật phòng
- Xóa mềm
- Tìm kiếm phòng
- Filter theo:
  - Giá
  - Diện tích
  - Loại phòng
  - Địa chỉ

---

## Database

MySQL

File SQL nằm trong thư mục:

database/

---

## Chạy project

Clone project

```bash
git clone https://github.com/bomoto01/TroFinder.git
```

Di chuyển vào project

```bash
cd TroFinder
```

Chạy

```bash
mvn spring-boot:run
```

Hoặc chạy trực tiếp trong IntelliJ IDEA / VS Code.

---

## Cấu trúc project

```
com.trofinder
│
├── config
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
├── service
├── specification
└── util
```

---

## Roadmap

- [x] Authentication
- [x] Address
- [x] Room CRUD
- [x] Room Search
- [ ] Upload ảnh
- [ ] Upload video
- [ ] Favorite
- [ ] Chat
- [ ] Search theo bản đồ
- [ ] Notification
- [ ] Docker
- [ ] Deploy

---

## Tác giả

Bùi Minh Tâm
