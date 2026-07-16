package com.trofinder.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.trofinder.dto.request.RoomCreateRequest;
import com.trofinder.dto.response.RoomResponse;
import com.trofinder.entity.Amenity;
import com.trofinder.entity.Room;
import com.trofinder.entity.RoomImage;
import com.trofinder.entity.RoomVideo;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoomMapper {

    /**
     * RoomCreateRequest -> Room
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
    @Mapping(target = "deleted", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    Room toEntity(RoomCreateRequest request);

    /**
     * Room -> RoomResponse
     */
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "ownerPhone", source = "owner.phone")

    @Mapping(target = "wardId", source = "ward.id")
    @Mapping(target = "wardName", source = "ward.name")

    @Mapping(target = "districtId", source = "ward.district.id")
    @Mapping(target = "districtName", source = "ward.district.name")

    @Mapping(target = "provinceId", source = "ward.district.province.id")
    @Mapping(target = "provinceName", source = "ward.district.province.name")

    @Mapping(target = "additionalAmenities", source = "additionalAmenities")

    @Mapping(target = "imageUrls", expression = "java(mapImageUrls(room.getImages()))")
    @Mapping(target = "videoUrls", expression = "java(mapVideoUrls(room.getVideos()))")
    @Mapping(target = "amenities", expression = "java(mapAmenities(room.getAmenities()))")

    RoomResponse toResponse(Room room);

    default List<String> mapImageUrls(List<RoomImage> images) {
        if (images == null) {
            return List.of();
        }

        return images.stream()
                .map(RoomImage::getImageUrl)
                .toList();
    }

    default List<String> mapVideoUrls(List<RoomVideo> videos) {
        if (videos == null) {
            return List.of();
        }

        return videos.stream()
                .map(RoomVideo::getVideoUrl)
                .toList();
    }

    default Set<String> mapAmenities(Set<Amenity> amenities) {
        if (amenities == null) {
            return Set.of();
        }

        return amenities.stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
    }

}