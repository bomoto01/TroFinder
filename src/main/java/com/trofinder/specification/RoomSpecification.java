package com.trofinder.specification;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.trofinder.entity.Room;
import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.RoomType;

public class RoomSpecification {

    private RoomSpecification() {
    }

    /**
     * Chưa bị xóa
     */
    public static Specification<Room> notDeleted() {

        return (root, query, cb) ->
                cb.isFalse(root.get("deleted"));
    }

    /**
     * Đã được duyệt
     */
    public static Specification<Room> approved() {

        return (root, query, cb) ->
                cb.equal(
                        root.get("approvalStatus"),
                        ApprovalStatus.APPROVED);
    }

    /**
     * Theo tỉnh
     */
    public static Specification<Room> hasProvince(Long provinceId) {

        return (root, query, cb) -> {

            if (provinceId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.join("ward")
                            .join("district")
                            .join("province")
                            .get("id"),
                    provinceId);
        };
    }

    /**
     * Theo quận/huyện
     */
    public static Specification<Room> hasDistrict(Long districtId) {

        return (root, query, cb) -> {

            if (districtId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.join("ward")
                            .join("district")
                            .get("id"),
                    districtId);
        };
    }

    /**
     * Theo phường/xã
     */
    public static Specification<Room> hasWard(Long wardId) {

        return (root, query, cb) -> {

            if (wardId == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.join("ward")
                            .get("id"),
                    wardId);
        };
    }

    /**
     * Giá tối thiểu
     */
    public static Specification<Room> minPrice(BigDecimal minPrice) {

        return (root, query, cb) -> {

            if (minPrice == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                    root.get("price"),
                    minPrice);
        };
    }

    /**
     * Giá tối đa
     */
    public static Specification<Room> maxPrice(BigDecimal maxPrice) {

        return (root, query, cb) -> {

            if (maxPrice == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                    root.get("price"),
                    maxPrice);
        };
    }

    /**
     * Diện tích tối thiểu
     */
    public static Specification<Room> minArea(BigDecimal minArea) {

        return (root, query, cb) -> {

            if (minArea == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                    root.get("area"),
                    minArea);
        };
    }

    /**
     * Diện tích tối đa
     */
    public static Specification<Room> maxArea(BigDecimal maxArea) {

        return (root, query, cb) -> {

            if (maxArea == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                    root.get("area"),
                    maxArea);
        };
    }

    /**
     * Loại phòng
     */
    public static Specification<Room> hasRoomType(RoomType roomType) {

        return (root, query, cb) -> {

            if (roomType == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("roomType"),
                    roomType);
        };
    }

    /**
     * Từ khóa
     */
    public static Specification<Room> keyword(String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String value = "%" + keyword.trim().toLowerCase() + "%";

            return cb.or(

                    cb.like(
                            cb.lower(root.get("title")),
                            value),

                    cb.like(
                            cb.lower(root.get("description")),
                            value),

                    cb.like(
                            cb.lower(root.get("address")),
                            value)

            );

        };
    }

}