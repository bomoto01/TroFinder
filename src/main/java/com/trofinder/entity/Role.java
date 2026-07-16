package com.trofinder.entity; // Package chứa các Entity ánh xạ tới bảng trong database

// Import các annotation của JPA
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/*
 * @Entity
 * Đánh dấu đây là một Entity.
 * Hibernate sẽ hiểu class này tương ứng với một bảng trong MySQL.
 */
@Entity

/*
 * Chỉ định Entity này ánh xạ tới bảng "roles"
 */
@Table(name = "roles")

/*
 * Lombok sẽ tự tạo getter cho tất cả thuộc tính
 * Ví dụ:
 * getId()
 * getName()
 * getDescription()
 */
@Getter

/*
 * Lombok sẽ tự tạo setter
 * Ví dụ:
 * setId()
 * setName()
 * setDescription()
 */
@Setter

public class Role {

    /*
     * @Id
     * Khóa chính của bảng.
     */
    @Id

    /*
     * @GeneratedValue
     * ID sẽ tự tăng.
     * Tương ứng AUTO_INCREMENT trong MySQL.
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * @Column
     * Ánh xạ với cột trong database.
     *
     * nullable = false
     * => Không được để NULL.
     *
     * unique = true
     * => Không được trùng.
     *
     * length = 50
     * => Độ dài tối đa 50 ký tự.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /*
     * Mô tả của Role.
     * Có thể để trống.
     *
     * length = 255
     * => Tối đa 255 ký tự.
     */
    @Column(length = 255)
    private String description;

}