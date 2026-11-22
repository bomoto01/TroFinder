-- ============================================================================
-- TroFinder Database Schema Migration: v1_schema_alignment.sql
-- Mục đích: Đồng bộ schema cơ sở dữ liệu với JPA Entity và các module Phase 1
-- Đảm bảo tương thích hoàn toàn chuẩn MySQL 8.0 (idempotent, an toàn dữ liệu)
-- ============================================================================

-- USE trofinder; -- Run against the active database connection

DELIMITER $$

-- Thủ tục kiểm tra và thêm cột nếu chưa tồn tại (tương thích MySQL 8.0)
DROP PROCEDURE IF EXISTS trofinder_add_column_if_not_exists $$
CREATE PROCEDURE trofinder_add_column_if_not_exists(
    IN p_table VARCHAR(64),
    IN p_column VARCHAR(64),
    IN p_definition VARCHAR(255)
)
BEGIN
    DECLARE col_count INT;
    SELECT COUNT(*) INTO col_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table
      AND COLUMN_NAME = p_column;

    IF col_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', p_table, '` ADD COLUMN `', p_column, '` ', p_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

-- Thủ tục kiểm tra và thêm chỉ mục nếu chưa tồn tại
DROP PROCEDURE IF EXISTS trofinder_add_index_if_not_exists $$
CREATE PROCEDURE trofinder_add_index_if_not_exists(
    IN p_table VARCHAR(64),
    IN p_index VARCHAR(64),
    IN p_columns VARCHAR(255)
)
BEGIN
    DECLARE idx_count INT;
    SELECT COUNT(*) INTO idx_count
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table
      AND INDEX_NAME = p_index;

    IF idx_count = 0 THEN
        SET @sql = CONCAT('CREATE INDEX `', p_index, '` ON `', p_table, '` (', p_columns, ')');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

-- Thủ tục kiểm tra và thêm khóa ngoại nếu chưa tồn tại
DROP PROCEDURE IF EXISTS trofinder_add_fk_if_not_exists $$
CREATE PROCEDURE trofinder_add_fk_if_not_exists(
    IN p_table VARCHAR(64),
    IN p_constraint VARCHAR(64),
    IN p_fk_sql VARCHAR(500)
)
BEGIN
    DECLARE fk_count INT;
    SELECT COUNT(*) INTO fk_count
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table
      AND CONSTRAINT_NAME = p_constraint;

    IF fk_count = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', p_table, '` ADD CONSTRAINT `', p_constraint, '` ', p_fk_sql);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END $$

DELIMITER ;

-- ============================================================================
-- 1. BẢNG amenities
-- ============================================================================
CALL trofinder_add_column_if_not_exists('amenities', 'created_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP');

-- ============================================================================
-- 2. BẢNG appointments
-- ============================================================================
CALL trofinder_add_column_if_not_exists('appointments', 'owner_id', 'BIGINT NULL AFTER room_id');
CALL trofinder_add_column_if_not_exists('appointments', 'confirmed_at', 'DATETIME NULL AFTER status');
CALL trofinder_add_column_if_not_exists('appointments', 'cancelled_at', 'DATETIME NULL AFTER confirmed_at');
CALL trofinder_add_column_if_not_exists('appointments', 'completed_at', 'DATETIME NULL AFTER cancelled_at');
CALL trofinder_add_column_if_not_exists('appointments', 'deleted', 'BOOLEAN NOT NULL DEFAULT FALSE AFTER note');
CALL trofinder_add_column_if_not_exists('appointments', 'updated_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at');

-- Đồng bộ owner_id từ bảng rooms cho các lịch hẹn cũ
UPDATE appointments a
JOIN rooms r ON a.room_id = r.id
SET a.owner_id = r.owner_id
WHERE a.owner_id IS NULL;

-- Ràng buộc khóa ngoại cho owner_id
CALL trofinder_add_fk_if_not_exists(
    'appointments',
    'fk_appointment_owner',
    'FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE'
);

-- ============================================================================
-- 3. BẢNG notifications
-- ============================================================================
ALTER TABLE notifications MODIFY COLUMN type VARCHAR(50) NOT NULL DEFAULT 'SYSTEM';
CALL trofinder_add_column_if_not_exists('notifications', 'reference_id', 'BIGINT NULL AFTER type');
CALL trofinder_add_column_if_not_exists('notifications', 'deleted', 'BOOLEAN NOT NULL DEFAULT FALSE AFTER is_read');
CALL trofinder_add_column_if_not_exists('notifications', 'updated_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at');

-- ============================================================================
-- 4. BẢNG reviews
-- ============================================================================
CALL trofinder_add_column_if_not_exists('reviews', 'reply_comment', 'TEXT NULL AFTER comment');
CALL trofinder_add_column_if_not_exists('reviews', 'reply_at', 'DATETIME NULL AFTER reply_comment');
CALL trofinder_add_column_if_not_exists('reviews', 'deleted', 'BOOLEAN NOT NULL DEFAULT FALSE AFTER reply_at');
CALL trofinder_add_column_if_not_exists('reviews', 'updated_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at');

-- ============================================================================
-- 5. BẢNG conversations
-- ============================================================================
CALL trofinder_add_column_if_not_exists('conversations', 'updated_at', 'TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER created_at');

-- ============================================================================
-- 6. TỐI ƯU HÓA INDEXES
-- ============================================================================
CALL trofinder_add_index_if_not_exists('rooms', 'idx_room_lat_lng', 'latitude, longitude');
CALL trofinder_add_index_if_not_exists('rooms', 'idx_room_featured_verified', 'featured, verified, status, approval_status');
CALL trofinder_add_index_if_not_exists('appointments', 'idx_appointment_tenant_owner', 'tenant_id, owner_id, status');
CALL trofinder_add_index_if_not_exists('notifications', 'idx_notification_user_unread', 'user_id, is_read, deleted');

-- ============================================================================
-- DỌN DẸP THỦ TỤC TẠM
-- ============================================================================
DROP PROCEDURE IF EXISTS trofinder_add_column_if_not_exists;
DROP PROCEDURE IF EXISTS trofinder_add_index_if_not_exists;
DROP PROCEDURE IF EXISTS trofinder_add_fk_if_not_exists;
