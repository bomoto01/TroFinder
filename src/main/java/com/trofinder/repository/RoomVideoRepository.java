package com.trofinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.RoomVideo;

@Repository
public interface RoomVideoRepository extends JpaRepository<RoomVideo, Long> {

    /*
     * Lấy tất cả video của phòng
     */
    List<RoomVideo> findByRoomId(Long roomId);

}