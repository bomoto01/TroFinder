package com.trofinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trofinder.entity.Favorite;
import com.trofinder.entity.FavoriteId;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    /**
     * Kiểm tra phòng đã được yêu thích chưa.
     */
    boolean existsByUserAndRoom(User user, Room room);

    /**
     * Lấy danh sách phòng yêu thích của người dùng.
     */
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);

    List<Favorite> findByUserId(Long userId);

    /**
     * Tìm Favorite theo User và Room.
     */
    Optional<Favorite> findByUserAndRoom(User user, Room room);

    /**
     * Xóa khỏi danh sách yêu thích.
     */
    void deleteByUserAndRoom(User user, Room room);

    /**
     * Đếm số lượt yêu thích của một phòng.
     */
    long countByRoom(Room room);

}