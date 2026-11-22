package com.trofinder.service.interfaces;

import java.util.List;

import com.trofinder.dto.response.AmenityResponse;

public interface AmenityService {

    List<AmenityResponse> getAll();

    AmenityResponse getById(Long id);
}
