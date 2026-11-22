package com.trofinder.dto.response;

import java.math.BigDecimal;

import com.trofinder.enums.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapMarkerResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private BigDecimal area;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private String thumbnail;
    private RoomType roomType;
    private Boolean verified;
    private Boolean featured;
}
