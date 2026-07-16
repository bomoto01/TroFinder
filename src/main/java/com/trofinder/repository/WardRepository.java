package com.trofinder.repository;

// ======================
// Import Java
// ======================
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.Ward;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {

    /*
     * Tìm theo tên phường.
     */
    Optional<Ward> findByName(String name);

    /*
     * Tìm theo mã.
     */
    Optional<Ward> findByCode(String code);

    /*
     * Lấy danh sách phường theo quận.
     */
    List<Ward> findByDistrictId(Long districtId);

    List<Ward> findByDistrictIdOrderByNameAsc(Long districtId);
    /*
     * Kiểm tra tồn tại.
     */
    boolean existsByName(String name);
    /*
     * Kiểm tra tồn tại theo mã.
     */
    boolean existsByCode(String code);

}