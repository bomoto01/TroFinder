package com.trofinder.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.trofinder.dto.request.RoomCreateRequest;
import com.trofinder.dto.response.MapMarkerResponse;
import com.trofinder.dto.response.RoomResponse;
import com.trofinder.dto.response.RoomSummaryResponse;
import com.trofinder.entity.Amenity;
import com.trofinder.entity.Room;
import com.trofinder.entity.RoomImage;
import com.trofinder.entity.RoomVideo;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoomMapper {

    /*
     * ==========================================================
     * Request -> Entity
     * ==========================================================
     */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "ward", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "approvalStatus", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "favoriteCount", ignore = true)
    @Mapping(target = "contactCount", ignore = true)
    @Mapping(target = "appointmentCount", ignore = true)
    @Mapping(target = "shareCount", ignore = true)
    @Mapping(target = "featured", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "priorityScore", ignore = true)
    @Mapping(target = "availableQuantity", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "boostedAt", ignore = true)
    @Mapping(target = "lastContactAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Room toEntity(RoomCreateRequest request);

    /*
     * ==========================================================
     * Entity -> Detailed Response
     * ==========================================================
     */

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "ownerPhone", source = "owner.phone")
    @Mapping(target = "ownerAvatar", source = "owner.avatar")
    @Mapping(target = "provinceId", source = "ward.district.province.id")
    @Mapping(target = "provinceName", source = "ward.district.province.name")
    @Mapping(target = "districtId", source = "ward.district.id")
    @Mapping(target = "districtName", source = "ward.district.name")
    @Mapping(target = "wardId", source = "ward.id")
    @Mapping(target = "wardName", source = "ward.name")
    @Mapping(target = "imageUrls", expression = "java(mapImageUrls(room.getImages()))")
    @Mapping(target = "videoUrls", expression = "java(mapVideoUrls(room.getVideos()))")
    @Mapping(target = "amenities", expression = "java(mapAmenities(room.getAmenities()))")
    RoomResponse toResponse(Room room);

    /*
     * ==========================================================
     * Entity -> Summary Response
     * ==========================================================
     */

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "ownerAvatar", source = "owner.avatar")
    @Mapping(target = "provinceId", source = "ward.district.province.id")
    @Mapping(target = "provinceName", source = "ward.district.province.name")
    @Mapping(target = "districtId", source = "ward.district.id")
    @Mapping(target = "districtName", source = "ward.district.name")
    @Mapping(target = "wardId", source = "ward.id")
    @Mapping(target = "wardName", source = "ward.name")
    @Mapping(target = "thumbnail", expression = "java(extractThumbnail(room.getImages()))")
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "reviewCount", ignore = true)
    RoomSummaryResponse toSummaryResponse(Room room);

    /*
     * ==========================================================
     * Entity -> Map Marker Response
     * ==========================================================
     */

    @Mapping(target = "thumbnail", expression = "java(extractThumbnail(room.getImages()))")
    MapMarkerResponse toMapMarkerResponse(Room room);

    /*
     * ==========================================================
     * Helper Methods
     * ==========================================================
     */

    default List<String> mapImageUrls(List<RoomImage> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .map(RoomImage::getImageUrl)
                .toList();
    }

    default List<String> mapVideoUrls(List<RoomVideo> videos) {
        if (videos == null || videos.isEmpty()) {
            return List.of();
        }
        return videos.stream()
                .map(RoomVideo::getVideoUrl)
                .toList();
    }

    default Set<String> mapAmenities(Set<Amenity> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return Set.of();
        }
        return amenities.stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
    }

    default String extractThumbnail(List<RoomImage> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getThumbnail()))
                .map(RoomImage::getImageUrl)
                .findFirst()
                .orElseGet(() -> images.get(0).getImageUrl());
    }
}
