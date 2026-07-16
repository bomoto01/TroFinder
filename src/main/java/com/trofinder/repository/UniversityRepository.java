package com.trofinder.repository;

// ======================
// Import Java
// ======================
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.University;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {

    /*
     * Tìm theo tên trường.
     */
    Optional<University> findByName(String name);

    /*
     * Tìm theo tên viết tắt.
     */
    Optional<University> findByShortName(String shortName);

    /*
     * Danh sách trường theo tỉnh.
     */
    List<University> findByProvinceId(Long provinceId);

    /*
     * Danh sách trường theo quận.
     */
    List<University> findByDistrictId(Long districtId);

    /*
     * Danh sách trường theo phường.
     */
    List<University> findByWardId(Long wardId);

    /*
     * Tìm tên gần đúng.
     */
    List<University> findByNameContainingIgnoreCase(String keyword);

    /*
     * Kiểm tra tồn tại.
     */
    boolean existsByName(String name);

}