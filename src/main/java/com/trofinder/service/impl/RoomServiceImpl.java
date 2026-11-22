package com.trofinder.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.trofinder.dto.request.RoomCreateRequest;
import com.trofinder.dto.request.RoomSearchRequest;
import com.trofinder.dto.response.MapMarkerResponse;
import com.trofinder.dto.response.RoomResponse;
import com.trofinder.dto.response.RoomSummaryResponse;
import com.trofinder.entity.Amenity;
import com.trofinder.entity.Room;
import com.trofinder.entity.RoomImage;
import com.trofinder.entity.RoomVideo;
import com.trofinder.entity.User;
import com.trofinder.entity.Ward;
import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.exception.BadRequestException;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.mapper.RoomMapper;
import com.trofinder.repository.AmenityRepository;
import com.trofinder.repository.RoomImageRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.repository.RoomVideoRepository;
import com.trofinder.repository.WardRepository;
import com.trofinder.service.interfaces.CurrentUserService;
import com.trofinder.service.interfaces.RoomService;
import com.trofinder.specification.RoomSpecification;
import com.trofinder.storage.FileStorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final FileStorageService fileStorageService;
    private final RoomMapper roomMapper;
    private final WardRepository wardRepository;
    private final AmenityRepository amenityRepository;
    private final CurrentUserService currentUserService;
    private final RoomVideoRepository roomVideoRepository;

    // ==========================================================
    // CREATE
    // ==========================================================

    @Override
    @Transactional
    public RoomResponse create(RoomCreateRequest request) {
        User owner = getCurrentUser();
        Ward ward = getWard(request.getWardId());
        Set<Amenity> amenities = getAmenities(request.getAmenityIds());

        Room room = buildRoom(request, owner, ward, amenities);
        return saveRoom(room);
    }

    // ==========================================================
    // READ
    // ==========================================================

    @Override
    @Transactional
    public RoomResponse getById(Long id) {
        Room room = getRoom(id);
        room.setViewCount(room.getViewCount() + 1);
        roomRepository.save(room);
        return roomMapper.toResponse(room);
    }

    @Override
    public Page<RoomResponse> getAll(Pageable pageable) {
        return roomRepository.findAllByDeletedFalse(pageable)
                .map(roomMapper::toResponse);
    }

    @Override
    public Page<RoomResponse> search(RoomSearchRequest request, Pageable pageable) {
        Specification<Room> specification = buildSearchSpecification(request);
        Pageable sortedPageable = applySort(request, pageable);

        return roomRepository.findAll(specification, sortedPageable)
                .map(roomMapper::toResponse);
    }

    @Override
    public Page<RoomSummaryResponse> searchSummary(RoomSearchRequest request, Pageable pageable) {
        Specification<Room> specification = buildSearchSpecification(request);
        Pageable sortedPageable = applySort(request, pageable);

        return roomRepository.findAll(specification, sortedPageable)
                .map(roomMapper::toSummaryResponse);
    }

    @Override
    public List<MapMarkerResponse> getMapMarkers(BigDecimal minLat, BigDecimal maxLat, BigDecimal minLng, BigDecimal maxLng) {
        if (minLat == null || maxLat == null || minLng == null || maxLng == null) {
            return roomRepository.findAllAvailableWithLocation()
                    .stream()
                    .map(roomMapper::toMapMarkerResponse)
                    .toList();
        }
        return roomRepository.findInBoundingBox(minLat, maxLat, minLng, maxLng)
                .stream()
                .map(roomMapper::toMapMarkerResponse)
                .toList();
    }

    @Override
    public List<MapMarkerResponse> getNearbyRooms(BigDecimal lat, BigDecimal lng, Double radiusKm) {
        if (lat == null || lng == null) {
            return List.of();
        }
        double maxDist = radiusKm != null ? radiusKm : 5.0;
        double centerLat = lat.doubleValue();
        double centerLng = lng.doubleValue();

        return roomRepository.findAllAvailableWithLocation().stream()
                .filter(r -> r.getLatitude() != null && r.getLongitude() != null)
                .filter(r -> {
                    double dist = calculateHaversineDistanceKm(
                            centerLat, centerLng,
                            r.getLatitude().doubleValue(), r.getLongitude().doubleValue());
                    return dist <= maxDist;
                })
                .map(roomMapper::toMapMarkerResponse)
                .toList();
    }

    private double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private Specification<Room> buildSearchSpecification(RoomSearchRequest request) {
        Specification<Room> spec = Specification.where(RoomSpecification.notDeleted())
                .and(RoomSpecification.approved());

        if (request.getStatus() != null) {
            spec = spec.and(RoomSpecification.hasStatus(request.getStatus()));
        } else {
            spec = spec.and(RoomSpecification.hasStatus(RoomStatus.AVAILABLE));
        }

        if (request.getVerified() != null) {
            spec = spec.and(RoomSpecification.isVerified(request.getVerified()));
        }

        if (request.getFeatured() != null) {
            spec = spec.and(RoomSpecification.isFeatured(request.getFeatured()));
        }

        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            spec = spec.and(RoomSpecification.hasAmenities(request.getAmenityIds()));
        }

        spec = spec.and(RoomSpecification.hasProvince(request.getProvinceId()))
                .and(RoomSpecification.hasDistrict(request.getDistrictId()))
                .and(RoomSpecification.hasWard(request.getWardId()))
                .and(RoomSpecification.minPrice(request.getMinPrice()))
                .and(RoomSpecification.maxPrice(request.getMaxPrice()))
                .and(RoomSpecification.minArea(request.getMinArea()))
                .and(RoomSpecification.maxArea(request.getMaxArea()))
                .and(RoomSpecification.hasRoomType(request.getRoomType()))
                .and(RoomSpecification.keyword(request.getKeyword()));

        return spec;
    }

    private Pageable applySort(RoomSearchRequest request, Pageable pageable) {
        if (request.getSortBy() == null || request.getSortBy().isBlank()) {
            return pageable;
        }

        Sort sort;
        switch (request.getSortBy().toLowerCase()) {
            case "price_asc":
                sort = Sort.by(Sort.Direction.ASC, "price");
                break;
            case "price_desc":
                sort = Sort.by(Sort.Direction.DESC, "price");
                break;
            case "area_asc":
                sort = Sort.by(Sort.Direction.ASC, "area");
                break;
            case "area_desc":
                sort = Sort.by(Sort.Direction.DESC, "area");
                break;
            case "views":
                sort = Sort.by(Sort.Direction.DESC, "viewCount");
                break;
            case "favorites":
                sort = Sort.by(Sort.Direction.DESC, "favoriteCount");
                break;
            case "newest":
            default:
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    // ==========================================================
    // UPDATE
    // ==========================================================

    @Override
    @Transactional
    public RoomResponse update(Long id, RoomCreateRequest request) {
        Room room = getRoom(id);
        validateOwner(room);

        Ward ward = getWard(request.getWardId());
        Set<Amenity> amenities = getAmenities(request.getAmenityIds());

        updateRoom(room, request, ward, amenities);
        return saveRoom(room);
    }

    // ==========================================================
    // DELETE
    // ==========================================================

    @Override
    @Transactional
    public void delete(Long id) {
        Room room = getRoom(id);
        validateOwner(room);

        room.setDeleted(true);
        room.setStatus(RoomStatus.HIDDEN);
        roomRepository.save(room);
    }

    // ==========================================================
    // MEDIA OPERATIONS
    // ==========================================================

    @Override
    @Transactional
    public List<String> uploadImages(Long roomId, List<MultipartFile> files) {
        Room room = getRoom(roomId);
        validateOwner(room);

        if (files == null || files.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ít nhất một ảnh.");
        }

        List<RoomImage> currentImages = roomImageRepository.findByRoomIdOrderByDisplayOrder(roomId);
        if (currentImages.size() + files.size() > 20) {
            throw new BadRequestException("Mỗi phòng chỉ được tối đa 20 ảnh.");
        }

        List<String> imageUrls = fileStorageService.storeRoomImages(roomId, files);
        boolean hasThumbnail = currentImages.stream().anyMatch(RoomImage::getThumbnail);
        int displayOrder = currentImages.size() + 1;

        List<RoomImage> roomImages = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            RoomImage roomImage = RoomImage.builder()
                    .room(room)
                    .imageUrl(imageUrl)
                    .displayOrder(displayOrder++)
                    .thumbnail(!hasThumbnail)
                    .build();
            roomImages.add(roomImage);
            hasThumbnail = true;
        }

        roomImageRepository.saveAll(roomImages);
        return imageUrls;
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        RoomImage image = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh."));

        validateOwner(image.getRoom());
        fileStorageService.delete(image.getImageUrl());
        roomImageRepository.delete(image);
    }

    @Override
    @Transactional
    public void setThumbnail(Long imageId) {
        RoomImage selectedImage = roomImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ảnh."));

        validateOwner(selectedImage.getRoom());

        List<RoomImage> images = roomImageRepository.findByRoomId(selectedImage.getRoom().getId());
        for (RoomImage image : images) {
            image.setThumbnail(false);
        }
        selectedImage.setThumbnail(true);
        roomImageRepository.saveAll(images);
    }

    @Override
    @Transactional
    public String uploadVideo(Long roomId, MultipartFile file) {
        Room room = getRoom(roomId);
        validateOwner(room);

        roomVideoRepository.findByRoomId(roomId).ifPresent(video -> {
            fileStorageService.deleteVideo(video.getVideoUrl());
            roomVideoRepository.delete(video);
        });

        String videoUrl = fileStorageService.storeRoomVideo(roomId, file);
        RoomVideo roomVideo = RoomVideo.builder()
                .room(room)
                .videoUrl(videoUrl)
                .build();
        roomVideoRepository.save(roomVideo);

        return videoUrl;
    }

    @Override
    public String getVideo(Long roomId) {
        getRoom(roomId);
        return roomVideoRepository.findByRoomId(roomId)
                .map(RoomVideo::getVideoUrl)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteVideo(Long roomId) {
        Room room = getRoom(roomId);
        validateOwner(room);

        RoomVideo video = roomVideoRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng chưa có video."));

        fileStorageService.deleteVideo(video.getVideoUrl());
        roomVideoRepository.delete(video);
    }

    // ==========================================================
    // PRIVATE HELPER METHODS
    // ==========================================================

    private User getCurrentUser() {
        return currentUserService.getCurrentUser();
    }

    private Room getRoom(Long id) {
        return roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng."));
    }

    private Ward getWard(Long wardId) {
        return wardRepository.findById(wardId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phường/xã."));
    }

    private Set<Amenity> getAmenities(Set<Long> amenityIds) {
        if (amenityIds == null || amenityIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(amenityRepository.findAllById(amenityIds));
    }

    private Room buildRoom(RoomCreateRequest request, User owner, Ward ward, Set<Amenity> amenities) {
        Room room = roomMapper.toEntity(request);
        updateRoomInformation(room, request, owner, ward, amenities);
        applyDefaultValues(room);
        return room;
    }

    private void updateRoomInformation(Room room, RoomCreateRequest request, User owner, Ward ward, Set<Amenity> amenities) {
        room.setOwner(owner);
        room.setWard(ward);
        room.setAmenities(amenities);
        room.setAdditionalAmenities(request.getAdditionalAmenities());
        room.setContactName(request.getContactName());
        room.setContactPhone(request.getContactPhone());
        room.setZaloPhone(request.getZaloPhone());
        room.setZaloQrUrl(request.getZaloQrUrl());
        room.setQuantity(request.getQuantity());
        room.setAllowBooking(request.getAllowBooking());
        room.setAllowCall(request.getAllowCall());
        room.setAllowChat(request.getAllowChat());
    }

    private void updateRoom(Room room, RoomCreateRequest request, Ward ward, Set<Amenity> amenities) {
        room.setContactName(request.getContactName());
        room.setContactPhone(request.getContactPhone());
        room.setZaloPhone(request.getZaloPhone());
        room.setZaloQrUrl(request.getZaloQrUrl());
        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());
        room.setRoomType(request.getRoomType());
        room.setPrice(request.getPrice());
        room.setDeposit(request.getDeposit());
        room.setElectricPrice(request.getElectricPrice());
        room.setWaterPrice(request.getWaterPrice());
        room.setArea(request.getArea());
        room.setQuantity(request.getQuantity());

        if (room.getAvailableQuantity() > request.getQuantity()) {
            room.setAvailableQuantity(request.getQuantity());
        }

        room.setAddress(request.getAddress());
        room.setWard(ward);
        room.setLatitude(request.getLatitude());
        room.setLongitude(request.getLongitude());
        room.setAmenities(amenities);
        room.setAdditionalAmenities(request.getAdditionalAmenities());
        room.setAllowBooking(request.getAllowBooking());
        room.setAllowCall(request.getAllowCall());
        room.setAllowChat(request.getAllowChat());
    }

    private void applyDefaultValues(Room room) {
        room.setStatus(RoomStatus.AVAILABLE);
        room.setApprovalStatus(ApprovalStatus.PENDING);
        room.setAvailableQuantity(room.getQuantity());
        room.setViewCount(0L);
        room.setFavoriteCount(0);
        room.setContactCount(0);
        room.setAppointmentCount(0);
        room.setShareCount(0);
        room.setFeatured(false);
        room.setPriorityScore(0);
        room.setVerified(false);
        room.setPublishedAt(null);
        room.setDeleted(false);
    }

    private void validateOwner(Room room) {
        User currentUser = getCurrentUser();
        if (!room.getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền thao tác phòng này.");
        }
    }

    private RoomResponse saveRoom(Room room) {
        Room saved = roomRepository.save(room);
        return roomMapper.toResponse(saved);
    }
}
