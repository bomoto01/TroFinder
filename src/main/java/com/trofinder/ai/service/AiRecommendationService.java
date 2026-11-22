package com.trofinder.ai.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.trofinder.dto.request.RoomSearchRequest;
import com.trofinder.dto.response.RoomSummaryResponse;
import com.trofinder.entity.Favorite;
import com.trofinder.enums.RoomStatus;
import com.trofinder.repository.FavoriteRepository;
import com.trofinder.service.interfaces.RoomService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {

    private final RoomService roomService;
    private final FavoriteRepository favoriteRepository;

    public List<RoomSummaryResponse> getRecommendations(Long userId, int limit) {
        RoomSearchRequest request = new RoomSearchRequest();
        request.setStatus(RoomStatus.AVAILABLE);

        if (userId != null) {
            List<Favorite> favorites = favoriteRepository.findByUserId(userId);
            if (!favorites.isEmpty()) {
                // Heuristic: recommend based on user's favorite room types and locations
                Favorite lastFav = favorites.get(0);
                if (lastFav.getRoom() != null) {
                    request.setRoomType(lastFav.getRoom().getRoomType());
                    if (lastFav.getRoom().getWard() != null && lastFav.getRoom().getWard().getDistrict() != null) {
                        request.setDistrictId(lastFav.getRoom().getWard().getDistrict().getId());
                    }
                }
            }
        }

        // Return top featured & verified rooms
        request.setSortBy("views");
        int pageSize = limit > 0 ? limit : 6;
        return roomService.searchSummary(request, PageRequest.of(0, pageSize)).getContent();
    }
}
