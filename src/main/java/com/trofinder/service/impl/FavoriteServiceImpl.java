package com.trofinder.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.response.FavoriteResponse;
import com.trofinder.entity.Favorite;
import com.trofinder.entity.FavoriteId;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.exception.BadRequestException;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.mapper.FavoriteMapper;
import com.trofinder.repository.FavoriteRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.interfaces.CurrentUserService;
import com.trofinder.service.interfaces.FavoriteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final RoomRepository roomRepository;

    private final FavoriteMapper favoriteMapper;

    private final CurrentUserService currentUserService;

    // ==========================================================
    // CREATE
    // ==========================================================

    @Override
    @Transactional
    public void addFavorite(Long roomId) {

        User user = getCurrentUser();

        Room room = getRoom(roomId);

        if (favoriteRepository.existsByUserAndRoom(user, room)) {

            throw new BadRequestException(
                    "Phòng đã nằm trong danh sách yêu thích.");
        }

        Favorite favorite = buildFavorite(user, room);

        favoriteRepository.save(favorite);

        room.setFavoriteCount(room.getFavoriteCount() + 1);

        roomRepository.save(room);
    }

    // ==========================================================
    // DELETE
    // ==========================================================

    @Override
    @Transactional
    public void removeFavorite(Long roomId) {

        User user = getCurrentUser();

        Room room = getRoom(roomId);

        favoriteRepository.findByUserAndRoom(user, room)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Phòng chưa được yêu thích."));

        favoriteRepository.deleteByUserAndRoom(user, room);

        if (room.getFavoriteCount() > 0) {

            room.setFavoriteCount(room.getFavoriteCount() - 1);

            roomRepository.save(room);
        }
    }

    // ==========================================================
    // READ
    // ==========================================================

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getMyFavorites() {

        User user = getCurrentUser();

        return favoriteRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(favoriteMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long roomId) {

        User user = getCurrentUser();

        Room room = getRoom(roomId);

        return favoriteRepository.existsByUserAndRoom(user, room);
    }

    // ==========================================================
    // PRIVATE METHODS
    // ==========================================================

    /**
     * Lấy người dùng hiện tại
     */
    private User getCurrentUser() {
        return currentUserService.getCurrentUser();
    }

    /**
     * Lấy phòng theo ID
     */
    private Room getRoom(Long roomId) {

        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy phòng."));
    }

    /**
     * Tạo Favorite
     */
    private Favorite buildFavorite(
            User user,
            Room room) {

        Favorite favorite = new Favorite();

        FavoriteId favoriteId = new FavoriteId();

        favoriteId.setUserId(user.getId());
        favoriteId.setRoomId(room.getId());

        favorite.setId(favoriteId);

        favorite.setUser(user);
        favorite.setRoom(room);

        return favorite;
    }
}