package com.trofinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trofinder.dto.response.AmenityResponse;
import com.trofinder.entity.Amenity;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.repository.AmenityRepository;
import com.trofinder.service.impl.AmenityServiceImpl;

@ExtendWith(MockitoExtension.class)
class AmenityServiceTest {

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    private Amenity wifi;
    private Amenity airConditioner;

    @BeforeEach
    void setUp() {
        wifi = Amenity.builder()
                .id(1L)
                .name("Wifi")
                .icon("wifi")
                .build();

        airConditioner = Amenity.builder()
                .id(2L)
                .name("Điều hòa")
                .icon("air-conditioner")
                .build();
    }

    @Test
    @DisplayName("Get all amenities")
    void testGetAllAmenities() {
        when(amenityRepository.findAll()).thenReturn(List.of(wifi, airConditioner));

        List<AmenityResponse> result = amenityService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Wifi", result.get(0).getName());
        assertEquals("Điều hòa", result.get(1).getName());
        verify(amenityRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get amenity by valid id")
    void testGetById_Success() {
        when(amenityRepository.findById(1L)).thenReturn(Optional.of(wifi));

        AmenityResponse result = amenityService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Wifi", result.getName());
    }

    @Test
    @DisplayName("Get amenity by non-existent id throws ResourceNotFoundException")
    void testGetById_NotFound() {
        when(amenityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                amenityService.getById(999L)
        );
    }
}
