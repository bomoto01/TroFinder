package com.trofinder.ai.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.trofinder.ai.dto.AiMapResponse;
import com.trofinder.ai.dto.AiParsedSearchCriteria;
import com.trofinder.dto.request.RoomSearchRequest;
import com.trofinder.dto.response.MapMarkerResponse;
import com.trofinder.dto.response.RoomSummaryResponse;
import com.trofinder.entity.Amenity;
import com.trofinder.entity.District;
import com.trofinder.entity.University;
import com.trofinder.enums.RoomStatus;
import com.trofinder.enums.RoomType;
import com.trofinder.repository.AmenityRepository;
import com.trofinder.repository.DistrictRepository;
import com.trofinder.repository.UniversityRepository;
import com.trofinder.service.interfaces.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiSearchService {

    private final RoomService roomService;
    private final DistrictRepository districtRepository;
    private final AmenityRepository amenityRepository;
    private final UniversityRepository universityRepository;

    public Page<RoomSummaryResponse> searchByNaturalLanguage(String prompt, Pageable pageable) {
        AiParsedSearchCriteria criteria = parseNaturalLanguagePrompt(prompt);

        RoomSearchRequest request = new RoomSearchRequest();
        request.setMinPrice(criteria.getMinPrice());
        request.setMaxPrice(criteria.getMaxPrice());
        request.setMinArea(criteria.getMinArea());
        request.setMaxArea(criteria.getMaxArea());
        request.setRoomType(criteria.getRoomType());
        request.setKeyword(criteria.getExtractedKeywords());
        request.setStatus(RoomStatus.AVAILABLE);

        // Map district if found
        if (criteria.getDistrictName() != null) {
            List<District> districts = districtRepository.findByNameContainingIgnoreCase(criteria.getDistrictName());
            if (!districts.isEmpty()) {
                request.setDistrictId(districts.get(0).getId());
            }
        }

        // Map amenities if found
        if (criteria.getAmenityKeywords() != null && !criteria.getAmenityKeywords().isEmpty()) {
            List<Long> amenityIds = new ArrayList<>();
            for (String kw : criteria.getAmenityKeywords()) {
                List<Amenity> found = amenityRepository.findAll().stream()
                        .filter(a -> a.getName().toLowerCase().contains(kw.toLowerCase()))
                        .toList();
                found.forEach(a -> amenityIds.add(a.getId()));
            }
            if (!amenityIds.isEmpty()) {
                request.setAmenityIds(amenityIds);
            }
        }

        return roomService.searchSummary(request, pageable);
    }

    public AiMapResponse searchMapByNaturalLanguage(String prompt) {
        AiParsedSearchCriteria criteria = parseNaturalLanguagePrompt(prompt);

        BigDecimal centerLat = new BigDecimal("21.0285");
        BigDecimal centerLng = new BigDecimal("105.8542");
        String anchorName = "Hà Nội";
        int zoom = 13;

        // 1. Detect University from DB or keyword
        List<University> allUnis = universityRepository.findAll();
        for (University u : allUnis) {
            String nameLower = u.getName() != null ? u.getName().toLowerCase() : "";
            String shortLower = u.getShortName() != null ? u.getShortName().toLowerCase() : "";
            if ((prompt != null && prompt.toLowerCase().contains(nameLower))
                    || (!shortLower.isEmpty() && prompt != null && prompt.toLowerCase().contains(shortLower))) {
                if (u.getLatitude() != null && u.getLongitude() != null) {
                    centerLat = u.getLatitude();
                    centerLng = u.getLongitude();
                    anchorName = u.getName();
                    zoom = 15;
                    break;
                }
            }
        }

        // 2. Query nearby markers using Haversine formula
        List<MapMarkerResponse> nearby = roomService.getNearbyRooms(centerLat, centerLng, 5.0);

        // 3. Filter by criteria
        List<MapMarkerResponse> filtered = nearby.stream()
                .filter(m -> {
                    if (criteria.getMaxPrice() != null && m.getPrice().compareTo(criteria.getMaxPrice()) > 0) {
                        return false;
                    }
                    if (criteria.getMinPrice() != null && m.getPrice().compareTo(criteria.getMinPrice()) < 0) {
                        return false;
                    }
                    if (criteria.getRoomType() != null && m.getRoomType() != criteria.getRoomType()) {
                        return false;
                    }
                    return true;
                })
                .toList();

        return AiMapResponse.builder()
                .anchorName(anchorName)
                .centerLat(centerLat)
                .centerLng(centerLng)
                .zoom(zoom)
                .totalFound(filtered.size())
                .markers(filtered)
                .build();
    }

    public AiParsedSearchCriteria parseNaturalLanguagePrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return AiParsedSearchCriteria.builder().build();
        }

        String text = prompt.toLowerCase();
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        RoomType roomType = null;
        String districtName = null;
        List<String> amenityKeywords = new ArrayList<>();

        // Detect Price: "dưới X triệu" or "từ X đến Y triệu"
        Pattern rangePattern = Pattern.compile("từ\\s*(\\d+(\\.\\d+)?)\\s*(đến|-)\\s*(\\d+(\\.\\d+)?)\\s*(triệu|tr)");
        Matcher rangeMatcher = rangePattern.matcher(text);
        if (rangeMatcher.find()) {
            minPrice = new BigDecimal(rangeMatcher.group(1)).multiply(new BigDecimal(1000000));
            maxPrice = new BigDecimal(rangeMatcher.group(4)).multiply(new BigDecimal(1000000));
        } else {
            Pattern underPattern = Pattern.compile("(dưới|<|khoảng|tầm)\\s*(\\d+(\\.\\d+)?)\\s*(triệu|tr)");
            Matcher underMatcher = underPattern.matcher(text);
            if (underMatcher.find()) {
                maxPrice = new BigDecimal(underMatcher.group(2)).multiply(new BigDecimal(1000000));
            }
        }

        // Detect RoomType
        if (text.contains("chung cư mini") || text.contains("ccmn")) {
            roomType = RoomType.MINI_APARTMENT;
        } else if (text.contains("chung cư") || text.contains("căn hộ")) {
            roomType = RoomType.APARTMENT;
        } else if (text.contains("nhà nguyên căn")) {
            roomType = RoomType.WHOLE_HOUSE;
        } else if (text.contains("homestay")) {
            roomType = RoomType.HOMESTAY;
        } else if (text.contains("phòng trọ") || text.contains("nhà trọ") || text.contains("phòng")) {
            roomType = RoomType.BOARDING_HOUSE;
        }

        // Detect District
        String[] commonDistricts = {
                "cầu giấy", "đống đa", "ba đình", "hai bà trưng", "thanh xuân",
                "hà đông", "hoàng mai", "nam từ liêm", "bắc từ liêm", "tây hồ",
                "bình thạnh", "gò vấp", "thủ đức", "tân bình", "quận 1", "quận 3", "quận 7", "quận 10"
        };
        for (String d : commonDistricts) {
            if (text.contains(d)) {
                districtName = d;
                break;
            }
        }

        // Detect Amenities
        if (text.contains("điều hòa") || text.contains("máy lạnh")) amenityKeywords.add("điều hòa");
        if (text.contains("nóng lạnh")) amenityKeywords.add("nóng lạnh");
        if (text.contains("máy giặt")) amenityKeywords.add("máy giặt");
        if (text.contains("tủ lạnh")) amenityKeywords.add("tủ lạnh");
        if (text.contains("gác lửng") || text.contains("gác xép")) amenityKeywords.add("gác");
        if (text.contains("ban công")) amenityKeywords.add("ban công");
        if (text.contains("khép kín")) amenityKeywords.add("khép kín");

        return AiParsedSearchCriteria.builder()
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .roomType(roomType)
                .districtName(districtName)
                .amenityKeywords(amenityKeywords)
                .extractedKeywords(prompt)
                .build();
    }
}
