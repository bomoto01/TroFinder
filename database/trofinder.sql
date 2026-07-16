DROP DATABASE IF EXISTS trofinder;

CREATE DATABASE trofinder
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE trofinder;

/*=============================================================
    TABLE: roles
==============================================================*/

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255)
);

/*=============================================================
    TABLE: users
==============================================================*/

CREATE TABLE users (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên đăng nhập',

    full_name VARCHAR(100) NOT NULL COMMENT 'Họ và tên',

    email VARCHAR(100) UNIQUE COMMENT 'Email',

    phone VARCHAR(20) UNIQUE COMMENT 'Số điện thoại',

    password VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã mã hóa',

    avatar VARCHAR(255) DEFAULT NULL COMMENT 'Ảnh đại diện',

    gender ENUM('MALE','FEMALE','OTHER')
        DEFAULT 'OTHER',

    date_of_birth DATE,

    provider ENUM('LOCAL','GOOGLE','FACEBOOK')
        DEFAULT 'LOCAL',

    email_verified BOOLEAN DEFAULT FALSE,

    phone_verified BOOLEAN DEFAULT FALSE,

    status ENUM(
        'PENDING',
        'ACTIVE',
        'BLOCKED'
    )
    DEFAULT 'PENDING',

    -- ⭐ Thêm
    reputation_score INT DEFAULT 100
        COMMENT 'Điểm uy tín',

    no_show_count INT DEFAULT 0
        COMMENT 'Số lần đặt lịch nhưng không đến',
	last_active_at TIMESTAMP NULL COMMENT 'Hoạt động gần nhất',
    role_id BIGINT NOT NULL COMMENT 'Vai trò',

    CONSTRAINT fk_user_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP

);

/*=============================================================
    TABLE: provinces
==============================================================*/

CREATE TABLE provinces (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE,

    code VARCHAR(20) UNIQUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

/*=============================================================
    TABLE: districts
==============================================================*/

CREATE TABLE districts (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    province_id BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,

    code VARCHAR(20),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_district_province
        FOREIGN KEY (province_id)
        REFERENCES provinces(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: wards
==============================================================*/

CREATE TABLE wards (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    district_id BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,

    code VARCHAR(20),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ward_district
        FOREIGN KEY (district_id)
        REFERENCES districts(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: universities
==============================================================*/

CREATE TABLE universities (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    short_name VARCHAR(100),

    province_id BIGINT NOT NULL,

    district_id BIGINT NOT NULL,

    ward_id BIGINT,

    address VARCHAR(255),

    latitude DECIMAL(10,8),

    longitude DECIMAL(11,8),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_university_province
        FOREIGN KEY (province_id)
        REFERENCES provinces(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_university_district
        FOREIGN KEY (district_id)
        REFERENCES districts(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_university_ward
        FOREIGN KEY (ward_id)
        REFERENCES wards(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: rooms
==============================================================*/

CREATE TABLE rooms (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    owner_id BIGINT NOT NULL COMMENT 'Chủ phòng',

    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề',

    description TEXT COMMENT 'Mô tả',

    price DECIMAL(12,2) NOT NULL COMMENT 'Giá thuê',

    area DECIMAL(6,2) NOT NULL COMMENT 'Diện tích (m2)',

    room_type ENUM(
        'BOARDING_HOUSE',
        'APARTMENT',
        'MINI_APARTMENT',
        'WHOLE_HOUSE',
        'HOMESTAY'
    ) NOT NULL,

    deposit DECIMAL(12,2) DEFAULT 0 COMMENT 'Tiền cọc',

    electric_price DECIMAL(10,2) DEFAULT 0 COMMENT 'Giá điện',

    water_price DECIMAL(10,2) DEFAULT 0 COMMENT 'Giá nước',

    address VARCHAR(255) NOT NULL,
    additional_amenities TEXT COMMENT 'Tiện ích khác do người dùng nhập',

    ward_id BIGINT NOT NULL,

    latitude DECIMAL(10,8),

    longitude DECIMAL(11,8),

    status ENUM(
        'AVAILABLE',
        'RENTED',
        'HIDDEN'
    ) DEFAULT 'AVAILABLE',

    approval_status ENUM(
        'PENDING',
        'APPROVED',
        'REJECTED'
    ) DEFAULT 'PENDING',

    view_count BIGINT DEFAULT 0,
    
    deleted BOOLEAN DEFAULT FALSE COMMENT 'Xóa mềm',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_room_owner
        FOREIGN KEY(owner_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_room_ward
        FOREIGN KEY(ward_id)
        REFERENCES wards(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: amenities
==============================================================*/

CREATE TABLE amenities (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên tiện ích',

    icon VARCHAR(255) COMMENT 'Đường dẫn icon',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

/*=============================================================
    TABLE: room_images
==============================================================*/

CREATE TABLE room_images (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    room_id BIGINT NOT NULL,

    image_url VARCHAR(255) NOT NULL,

    display_order INT DEFAULT 1 COMMENT 'Thứ tự hiển thị',

    is_thumbnail BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_room_image_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE

);

/*=============================================================
    TABLE: room_videos
==============================================================*/

CREATE TABLE room_videos (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    room_id BIGINT NOT NULL,

    video_url VARCHAR(255) NOT NULL,

    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_room_video
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: room_amenities
==============================================================*/

CREATE TABLE room_amenities (

    room_id BIGINT NOT NULL,

    amenity_id BIGINT NOT NULL,

    PRIMARY KEY(room_id, amenity_id),

    CONSTRAINT fk_room_amenity_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_room_amenity_amenity
        FOREIGN KEY(amenity_id)
        REFERENCES amenities(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: favorites
==============================================================*/

CREATE TABLE favorites (

    user_id BIGINT NOT NULL,

    room_id BIGINT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY(user_id, room_id),

    CONSTRAINT fk_favorite_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favorite_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE

);

/*=============================================================
    TABLE: appointments
==============================================================*/

CREATE TABLE appointments (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    room_id BIGINT NOT NULL,

    tenant_id BIGINT NOT NULL,

    appointment_date DATE NOT NULL,

    appointment_time TIME NOT NULL,

    status ENUM(
        'PENDING',
        'CONFIRMED',
        'CANCELLED',
        'COMPLETED'
    ) DEFAULT 'PENDING',

    note VARCHAR(500),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_appointment_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_appointment_tenant
        FOREIGN KEY(tenant_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
	CONSTRAINT uk_appointment
		UNIQUE(
			room_id,
			tenant_id,
			appointment_date,
			appointment_time
		)

);

/*=============================================================
    TABLE: conversations
==============================================================*/

CREATE TABLE conversations (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    room_id BIGINT NOT NULL,

    tenant_id BIGINT NOT NULL,

    landlord_id BIGINT NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_conversation_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_conversation_tenant
        FOREIGN KEY(tenant_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_conversation_landlord
        FOREIGN KEY(landlord_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
	CONSTRAINT uk_conversation
		UNIQUE(room_id, tenant_id, landlord_id)

);

/*=============================================================
    TABLE: messages
==============================================================*/

CREATE TABLE messages (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    conversation_id BIGINT NOT NULL,

    sender_id BIGINT NOT NULL,

    content TEXT,

    image_url VARCHAR(255),

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_message_conversation
        FOREIGN KEY(conversation_id)
        REFERENCES conversations(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_message_sender
        FOREIGN KEY(sender_id)
        REFERENCES users(id)
        ON DELETE CASCADE

);

/*=============================================================
    TABLE: reviews
==============================================================*/

CREATE TABLE reviews (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    room_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL,

    rating TINYINT NOT NULL CHECK(rating BETWEEN 1 AND 5),

    comment TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_review_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
	CONSTRAINT uk_review
		UNIQUE(room_id,user_id)

);

/*=============================================================
    TABLE: reports
==============================================================*/

CREATE TABLE reports (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    room_id BIGINT NOT NULL,

    reporter_id BIGINT NOT NULL,

    reason ENUM(
        'SCAM',
        'WRONG_PRICE',
        'WRONG_INFORMATION',
        'SPAM',
        'OTHER'
    ) NOT NULL,

    description TEXT NOT NULL,

    admin_note TEXT,

    status ENUM(
        'PENDING',
        'REVIEWING',
        'APPROVED',
        'REJECTED',
        'RESOLVED'
    ) DEFAULT 'PENDING',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
	resolved_at TIMESTAMP NULL COMMENT 'Thời điểm xử lý xong',
    CONSTRAINT fk_report_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_report_user
        FOREIGN KEY(reporter_id)
        REFERENCES users(id)
        ON DELETE CASCADE

);

/*=============================================================
    TABLE: report_histories
==============================================================*/

CREATE TABLE report_histories (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    report_id BIGINT NOT NULL,

    admin_id BIGINT NOT NULL,

    action VARCHAR(100) NOT NULL,

    note TEXT,

    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_report_history_report
        FOREIGN KEY(report_id)
        REFERENCES reports(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_report_history_admin
        FOREIGN KEY(admin_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: notifications
==============================================================*/

CREATE TABLE notifications (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    title VARCHAR(255) NOT NULL,

    content TEXT NOT NULL,
    type ENUM(
    'SYSTEM',
    'NEW_ROOM',
    'NEW_MESSAGE',
    'NEW_REVIEW',
    'REPORT'
	) DEFAULT 'SYSTEM',

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: verification_tokens
==============================================================*/

CREATE TABLE verification_tokens (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    token VARCHAR(255) NOT NULL UNIQUE,

    type ENUM(
        'EMAIL_VERIFY',
        'RESET_PASSWORD'
    ) NOT NULL,

    expired_at DATETIME NOT NULL,

    used BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_verification_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: room_views
==============================================================*/

CREATE TABLE room_views (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    room_id BIGINT NOT NULL,

    user_id BIGINT NULL,

    ip_address VARCHAR(45),

    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_room_view_room
        FOREIGN KEY(room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_room_view_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE

);

/*=============================================================
    TABLE: refresh_tokens
==============================================================*/

CREATE TABLE refresh_tokens (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    token VARCHAR(500) NOT NULL UNIQUE,

    expired_at DATETIME NOT NULL,

    revoked BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

/*=============================================================
    TỐI ƯU HÓA INDEX (Chạy đoạn này sau khi đã tạo các bảng)
==============================================================*/

-- 1. Bảng rooms: Tối ưu cho việc lọc danh sách phòng trên trang chủ/tìm kiếm
-- Index kết hợp giúp lọc nhanh theo trạng thái và thời gian/giá
CREATE INDEX idx_room_status_approval_created ON rooms(approval_status, status, created_at);
CREATE INDEX idx_room_ward_price ON rooms(ward_id, price);
CREATE INDEX idx_room_owner_status ON rooms(owner_id, status);

-- 2. Bảng messages: Tối ưu cho việc load danh sách tin nhắn theo hội thoại
CREATE INDEX idx_message_conv_created ON messages(conversation_id, created_at);

-- 3. Bảng appointments: Tối ưu cho việc xem lịch hẹn theo thời gian
CREATE INDEX idx_appointment_room_date ON appointments(room_id, appointment_date);

-- 4. Bảng reports: Tối ưu cho trang quản trị lọc báo cáo
CREATE INDEX idx_report_status_created ON reports(status, created_at);

-- 5. Bảng notifications: Tối ưu cho việc load thông báo chưa đọc của user
CREATE INDEX idx_notification_user_read ON notifications(user_id, is_read);

-- 6. Bảng room_views: Tối ưu cho việc thống kê lượt xem phòng
CREATE INDEX idx_room_view_room_time ON room_views(room_id, viewed_at);

/*=============================================================
   LƯU Ý: 
   - Những index đơn lẻ như (role_id), (owner_id) trước đó 
     nếu là khóa ngoại (Foreign Key) thì MySQL thường đã tự đánh index.
   - Đoạn script trên tập trung vào các trường hợp "WHERE" kết hợp "ORDER BY".
==============================================================*/
