package com.trofinder.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.MapMarkerResponse;
import com.trofinder.service.interfaces.RoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final RoomService roomService;

    /**
     * Lấy markers trên bản đồ theo bounding box
     */
    @GetMapping("/markers")
    public ResponseEntity<ApiResponse<List<MapMarkerResponse>>> getMarkers(
            @RequestParam(required = false) BigDecimal minLat,
            @RequestParam(required = false) BigDecimal maxLat,
            @RequestParam(required = false) BigDecimal minLng,
            @RequestParam(required = false) BigDecimal maxLng) {

        List<MapMarkerResponse> markers = roomService.getMapMarkers(minLat, maxLat, minLng, maxLng);

        return ResponseEntity.ok(
                ApiResponse.<List<MapMarkerResponse>>builder()
                        .success(true)
                        .message("Lấy dữ liệu bản đồ thành công")
                        .data(markers)
                        .build());
    }

    /**
     * Tìm phòng lân cận tọa độ trong bán kính
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<MapMarkerResponse>>> getNearby(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {

        List<MapMarkerResponse> nearby = roomService.getNearbyRooms(lat, lng, radiusKm);

        return ResponseEntity.ok(
                ApiResponse.<List<MapMarkerResponse>>builder()
                        .success(true)
                        .message("Tìm phòng lân cận thành công")
                        .data(nearby)
                        .build());
    }
}
