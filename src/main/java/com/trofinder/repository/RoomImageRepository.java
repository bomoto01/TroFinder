package com.trofinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.RoomImage;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage, Long>{

    /*
     * Lấy toàn bộ ảnh của một phòng
     */
    List<RoomImage> findByRoomIdOrderByDisplayOrder(Long roomId);

    /*
     * Lấy ảnh thumbnail
     */
    RoomImage findByRoomIdAndThumbnailTrue(Long roomId);

}