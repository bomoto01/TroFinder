package com.trofinder.ai.dto;

import java.math.BigDecimal;
import java.util.List;

import com.trofinder.dto.response.MapMarkerResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMapResponse {

    private String anchorName;
    private BigDecimal centerLat;
    private BigDecimal centerLng;
    private Integer zoom;
    private Integer totalFound;
    private List<MapMarkerResponse> markers;
    private String aiAnalysis;
    private Double distanceKm;
    private List<String> recommendedAreas;
}
