package com.trofinder.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.response.AmenityResponse;
import com.trofinder.entity.Amenity;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.repository.AmenityRepository;
import com.trofinder.service.interfaces.AmenityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;

    @Override
    public List<AmenityResponse> getAll() {
        return amenityRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AmenityResponse getById(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiện ích"));
        return toResponse(amenity);
    }

    private AmenityResponse toResponse(Amenity amenity) {
        return AmenityResponse.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .icon(amenity.getIcon())
                .build();
    }
}
