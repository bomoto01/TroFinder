package com.trofinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    /*
     * Tìm theo tên.
     */
    Optional<Amenity> findByName(String name);

    /*
     * Kiểm tra tồn tại.
     */
    boolean existsByName(String name);

    /*
     * Tìm gần đúng.
     */
    List<Amenity> findByNameContainingIgnoreCase(String keyword);
    /**
     * Lấy danh sách tiện ích theo danh sách ID.
     */
    List<Amenity> findByIdIn(List<Long> ids);
}