package com.trofinder.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import com.trofinder.dto.request.RoomCreateRequest;
import com.trofinder.dto.response.RoomResponse;
import com.trofinder.entity.Amenity;
import com.trofinder.entity.Room;
import com.trofinder.entity.RoomVideo;
import com.trofinder.entity.User;
import com.trofinder.entity.Ward;
import com.trofinder.enums.ApprovalStatus;
import com.trofinder.enums.RoomStatus;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.mapper.RoomMapper;
import com.trofinder.repository.AmenityRepository;
import com.trofinder.repository.RoomImageRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.repository.RoomVideoRepository;
import com.trofinder.repository.WardRepository;
import com.trofinder.service.interfaces.CurrentUserService;
import com.trofinder.service.interfaces.RoomService;
import com.trofinder.dto.request.RoomSearchRequest;
import com.trofinder.specification.RoomSpecification;
import com.trofinder.storage.FileStorageService;
import com.trofinder.entity.RoomImage;

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

        Room room = buildRoom(
                request,
                getCurrentUser(),
                getWard(request.getWardId()),
                getAmenities(request.getAmenityIds()));

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
    public Page<RoomResponse> search(
            RoomSearchRequest request,
            Pageable pageable) {

        Specification<Room> specification =
                Specification.where(RoomSpecification.notDeleted())
                        .and(RoomSpecification.approved())
                        .and(RoomSpecification.hasProvince(request.getProvinceId()))
                        .and(RoomSpecification.hasDistrict(request.getDistrictId()))
                        .and(RoomSpecification.hasWard(request.getWardId()))
                        .and(RoomSpecification.minPrice(request.getMinPrice()))
                        .and(RoomSpecification.maxPrice(request.getMaxPrice()))
                        .and(RoomSpecification.minArea(request.getMinArea()))
                        .and(RoomSpecification.maxArea(request.getMaxArea()))
                        .and(RoomSpecification.hasRoomType(request.getRoomType()))
                        .and(RoomSpecification.keyword(request.getKeyword()));

        return roomRepository
                .findAll(specification, pageable)
                .map(roomMapper::toResponse);

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

        Set<Amenity> amenities =
                getAmenities(request.getAmenityIds());

        updateRoom(
                room,
                request,
                ward,
                amenities);

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

        roomRepository.save(room);
    }

    // ==========================================================
    // PRIVATE METHODS
    // ==========================================================
    @Override
    @Transactional
    public List<String> uploadImages(
            Long roomId,
            List<MultipartFile> files) {

        Room room = getRoom(roomId);

        validateOwner(room);

        List<String> imageUrls =
                fileStorageService.storeRoomImages(roomId, files);

        boolean hasThumbnail =
                roomImageRepository.findByRoomIdAndThumbnailTrue(roomId) != null;

        int displayOrder = roomImageRepository
                .findByRoomIdOrderByDisplayOrder(roomId)
                .size() + 1;

        for (String imageUrl : imageUrls) {

            RoomImage roomImage = RoomImage.builder()
                    .room(room)
                    .imageUrl(imageUrl)
                    .displayOrder(displayOrder++)
                    .thumbnail(!hasThumbnail)
                    .build();

            roomImageRepository.save(roomImage);

            hasThumbnail = true;
        }

        return imageUrls;
    }
    /**
     * User đang đăng nhập
     */
    private User getCurrentUser() {
        return currentUserService.getCurrentUser();
    }

    /**
     * Lấy phòng
     */
    private Room getRoom(Long id) {

        return roomRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy phòng."));
    }

    /**
     * Lấy Ward
     */
    private Ward getWard(Long wardId) {

        return wardRepository.findById(wardId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy phường/xã."));
    }

    /**
     * Lấy danh sách tiện ích
     */
    private Set<Amenity> getAmenities(Set<Long> amenityIds) {

        if (amenityIds == null || amenityIds.isEmpty()) {
            return new HashSet<>();
        }

        return new HashSet<>(amenityRepository.findAllById(amenityIds));
    }

    /**
     * Tạo Room
     */
    private Room buildRoom(
            RoomCreateRequest request,
            User owner,
            Ward ward,
            Set<Amenity> amenities) {

        Room room = roomMapper.toEntity(request);

        updateRoomInformation(
                room,
                request,
                owner,
                ward,
                amenities);

        applyDefaultValues(room);

        return room;
    }

    /**
     * Cập nhật thông tin Room
     */
    private void updateRoomInformation(
            Room room,
            RoomCreateRequest request,
            User owner,
            Ward ward,
            Set<Amenity> amenities) {

        room.setOwner(owner);

        room.setWard(ward);

        room.setAmenities(amenities);

        room.setAdditionalAmenities(request.getAdditionalAmenities());
    }
    /**
     * Cập nhật dữ liệu Room
     */
    private void updateRoom(
            Room room,
            RoomCreateRequest request,
            Ward ward,
            Set<Amenity> amenities) {

        room.setTitle(request.getTitle());
        room.setDescription(request.getDescription());

        room.setPrice(request.getPrice());
        room.setArea(request.getArea());

        room.setRoomType(request.getRoomType());

        room.setDeposit(request.getDeposit());
        room.setElectricPrice(request.getElectricPrice());
        room.setWaterPrice(request.getWaterPrice());

        room.setAddress(request.getAddress());

        room.setWard(ward);

        room.setLatitude(request.getLatitude());
        room.setLongitude(request.getLongitude());

        room.setAmenities(amenities);

        room.setAdditionalAmenities(request.getAdditionalAmenities());

    }
    /**
     * Giá trị mặc định
     */
    private void applyDefaultValues(Room room) {

        room.setStatus(RoomStatus.AVAILABLE);

        room.setApprovalStatus(ApprovalStatus.PENDING);

        room.setViewCount(0L);
        
        room.setDeleted(false);
    }
    /**
     * Kiểm tra người dùng có phải chủ phòng không.
     */
    private void validateOwner(Room room) {

        User currentUser = getCurrentUser();

        if (!room.getOwner().getId().equals(currentUser.getId())) {

            throw new IllegalArgumentException(
                    "Bạn không có quyền thao tác phòng này.");

        }
    }

    /**
     * Lưu Room
     */
    private RoomResponse saveRoom(Room room) {

        Room saved = roomRepository.save(room);

        return roomMapper.toResponse(saved);
    }
    @Override
    @Transactional
    public void deleteImage(Long imageId) {

        RoomImage image = roomImageRepository.findById(imageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy ảnh."));

        validateOwner(image.getRoom());

        fileStorageService.delete(image.getImageUrl());

        roomImageRepository.delete(image);
    }
    @Override
    @Transactional
    public void setThumbnail(Long imageId) {

        RoomImage selectedImage = roomImageRepository.findById(imageId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy ảnh."));

        // Chỉ chủ phòng mới được đổi thumbnail
        validateOwner(selectedImage.getRoom());

        // Lấy toàn bộ ảnh của phòng
        List<RoomImage> images =
                roomImageRepository.findByRoomId(
                        selectedImage.getRoom().getId());

        // Bỏ thumbnail của tất cả ảnh
        for (RoomImage image : images) {
            image.setThumbnail(false);
        }

        // Đặt thumbnail cho ảnh được chọn
        selectedImage.setThumbnail(true);

        roomImageRepository.saveAll(images);
    }
   @Override
@Transactional
public String uploadVideo(
        Long roomId,
        MultipartFile file) {

    Room room = getRoom(roomId);

    validateOwner(room);

    roomVideoRepository.findByRoomId(roomId)
            .ifPresent(video -> {

                fileStorageService.deleteVideo(
                        video.getVideoUrl());

                roomVideoRepository.delete(video);

            });

    String videoUrl =
            fileStorageService.storeRoomVideo(
                    roomId,
                    file);

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

        return roomVideoRepository
                .findByRoomId(roomId)
                .map(RoomVideo::getVideoUrl)
                .orElse(null);

    }
   @Override
    @Transactional
    public void deleteVideo(Long roomId) {

        Room room = getRoom(roomId);

        validateOwner(room);

        RoomVideo video = roomVideoRepository
                .findByRoomId(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Phòng chưa có video."));

        fileStorageService.deleteVideo(video.getVideoUrl());

        roomVideoRepository.delete(video);

    }
}