package com.trofinder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.response.ApiResponse;
import com.trofinder.dto.response.AmenityResponse;
import com.trofinder.service.interfaces.AmenityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AmenityResponse>>> getAll() {
        List<AmenityResponse> list = amenityService.getAll();
        return ResponseEntity.ok(
                ApiResponse.<List<AmenityResponse>>builder()
                        .success(true)
                        .message("Lấy danh sách tiện ích thành công")
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AmenityResponse>> getById(@PathVariable Long id) {
        AmenityResponse response = amenityService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<AmenityResponse>builder()
                        .success(true)
                        .message("Lấy thông tin tiện ích thành công")
                        .data(response)
                        .build()
        );
    }
}
