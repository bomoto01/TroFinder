package com.trofinder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trofinder.dto.response.ApiResponse;
import com.trofinder.entity.University;
import com.trofinder.repository.UniversityRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityRepository universityRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UniversityDto>>> getUniversities(
            @RequestParam(required = false) String keyword) {

        List<University> list;
        if (keyword != null && !keyword.isBlank()) {
            list = universityRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else {
            list = universityRepository.findAll();
        }

        List<UniversityDto> dtos = list.stream()
                .map(u -> UniversityDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .shortName(u.getShortName())
                        .address(u.getAddress())
                        .latitude(u.getLatitude() != null ? u.getLatitude().doubleValue() : null)
                        .longitude(u.getLongitude() != null ? u.getLongitude().doubleValue() : null)
                        .provinceName(u.getProvince() != null ? u.getProvince().getName() : null)
                        .districtName(u.getDistrict() != null ? u.getDistrict().getName() : null)
                        .build())
                .toList();

        return ResponseEntity.ok(
                ApiResponse.<List<UniversityDto>>builder()
                        .success(true)
                        .message("Lấy danh sách trường đại học thành công")
                        .data(dtos)
                        .build());
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UniversityDto {
        private Long id;
        private String name;
        private String shortName;
        private String address;
        private Double latitude;
        private Double longitude;
        private String provinceName;
        private String districtName;
    }
}
