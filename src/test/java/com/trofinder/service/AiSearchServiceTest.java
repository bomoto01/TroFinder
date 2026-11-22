package com.trofinder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trofinder.ai.dto.AiParsedSearchCriteria;
import com.trofinder.ai.service.AiSearchService;
import com.trofinder.enums.RoomType;
import com.trofinder.repository.AmenityRepository;
import com.trofinder.repository.DistrictRepository;
import com.trofinder.service.interfaces.RoomService;

@ExtendWith(MockitoExtension.class)
class AiSearchServiceTest {

    @Mock
    private RoomService roomService;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private com.trofinder.repository.UniversityRepository universityRepository;

    @InjectMocks
    private AiSearchService aiSearchService;

    @Test
    @DisplayName("Phân tích câu hỏi tự nhiên: vị trí, giá, loại phòng và tiện ích")
    void parseNaturalLanguagePrompt_ComplexQuery() {
        String prompt = "Tìm chung cư mini quận Cầu Giấy dưới 4 triệu có điều hòa gác lửng";

        AiParsedSearchCriteria criteria = aiSearchService.parseNaturalLanguagePrompt(prompt);

        assertNotNull(criteria);
        assertEquals("cầu giấy", criteria.getDistrictName());
        assertEquals(RoomType.MINI_APARTMENT, criteria.getRoomType());
        assertEquals(0, new BigDecimal("4000000").compareTo(criteria.getMaxPrice()));
        assertNotNull(criteria.getAmenityKeywords());
        assertEquals(2, criteria.getAmenityKeywords().size());
    }

    @Test
    @DisplayName("Phân tích khoảng giá 'từ X đến Y triệu'")
    void parseNaturalLanguagePrompt_PriceRange() {
        String prompt = "Phòng trọ từ 2 đến 3.5 triệu ở Đống Đa";

        AiParsedSearchCriteria criteria = aiSearchService.parseNaturalLanguagePrompt(prompt);

        assertNotNull(criteria);
        assertEquals(0, new BigDecimal("2000000").compareTo(criteria.getMinPrice()));
        assertEquals(0, new BigDecimal("3500000").compareTo(criteria.getMaxPrice()));
        assertEquals(RoomType.BOARDING_HOUSE, criteria.getRoomType());
        assertEquals("đống đa", criteria.getDistrictName());
    }
}
