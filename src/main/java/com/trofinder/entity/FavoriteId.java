package com.trofinder.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Khóa chính kép của bảng favorites.
 */
@Embeddable

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FavoriteId implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * ==========================================================
     * USER ID
     * ==========================================================
     */
    @Column(name = "user_id")
    private Long userId;

    /*
     * ==========================================================
     * ROOM ID
     * ==========================================================
     */
    @Column(name = "room_id")
    private Long roomId;

}