package com.trofinder.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.trofinder.dto.response.FavoriteResponse;
import com.trofinder.entity.Favorite;
import com.trofinder.entity.Room;
import com.trofinder.entity.RoomImage;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FavoriteMapper {

    /*
     * ==========================================================
     * Favorite -> FavoriteResponse
     * ==========================================================
     */

    @Mapping(target = "favoriteAt", source = "createdAt")

    // Room
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "title", source = "room.title")
    @Mapping(target = "price", source = "room.price")
    @Mapping(target = "area", source = "room.area")
    @Mapping(target = "roomType", source = "room.roomType")
    @Mapping(target = "address", source = "room.address")

    // Owner
    @Mapping(target = "ownerId", source = "room.owner.id")
    @Mapping(target = "ownerName", source = "room.owner.fullName")

    // Statistics
    @Mapping(target = "featured", source = "room.featured")
    @Mapping(target = "verified", source = "room.verified")
    @Mapping(target = "viewCount", source = "room.viewCount")

    // Time
    @Mapping(target = "createdAt", source = "room.createdAt")

    // Thumbnail
    @Mapping(
            target = "thumbnail",
            expression = "java(getThumbnail(favorite.getRoom()))"
    )
    FavoriteResponse toResponse(Favorite favorite);

    /*
     * ==========================================================
     * Helper
     * ==========================================================
     */

    default String getThumbnail(Room room) {

        if (room == null || room.getImages() == null) {
            return null;
        }

        return room.getImages()
                .stream()
                .filter(RoomImage::getThumbnail)
                .map(RoomImage::getImageUrl)
                .findFirst()
                .orElse(null);
    }

}