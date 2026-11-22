package com.trofinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    /*
     * Tìm theo tên.
     */
    Optional<District> findByName(String name);

    List<District> findByNameContainingIgnoreCase(String name);

    /*
     * Tìm theo mã.
     */
    Optional<District> findByCode(String code);

    /*
     * Lấy danh sách quận theo tỉnh.
     */
    List<District> findByProvinceId(Long provinceId);

    List<District> findByProvinceIdOrderByNameAsc(Long provinceId);
    /*
     * Kiểm tra tồn tại.
     */
    boolean existsByName(String name);
    /*
     * Kiểm tra tồn tại theo mã.
     */
    boolean existsByCode(String code);
}