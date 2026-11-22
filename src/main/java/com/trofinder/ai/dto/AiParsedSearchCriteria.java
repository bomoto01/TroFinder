package com.trofinder.ai.dto;

import java.math.BigDecimal;
import java.util.List;

import com.trofinder.enums.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiParsedSearchCriteria {
    private String provinceName;
    private String districtName;
    private String wardName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minArea;
    private BigDecimal maxArea;
    private RoomType roomType;
    private List<String> amenityKeywords;
    private String extractedKeywords;
}
