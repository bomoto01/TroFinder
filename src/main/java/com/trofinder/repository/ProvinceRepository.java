package com.trofinder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trofinder.entity.Province;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long>{

    /*
     * Tìm theo tên tỉnh.
     */
    Optional<Province> findByName(String name);

    /*
     * Tìm theo mã.
     */
    Optional<Province> findByCode(String code);

    /*
     * Kiểm tra tồn tại.
     */
    boolean existsByName(String name);

    boolean existsByCode(String code);

    List<Province> findAllByOrderByNameAsc();
}