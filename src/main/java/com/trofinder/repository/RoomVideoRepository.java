package com.trofinder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.RoomVideo;

@Repository
public interface RoomVideoRepository extends JpaRepository<RoomVideo, Long> {

    /*
     * Lấy video của một phòng
     */
    Optional<RoomVideo> findByRoomId(Long roomId);

}