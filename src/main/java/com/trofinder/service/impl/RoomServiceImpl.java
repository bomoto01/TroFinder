package com.trofinder.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

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
import com.trofinder.exception.BadRequestException;

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

        Room room = buildRoom(
                request,
                owner,
                ward,
                amenities);

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
        room.setStatus(RoomStatus.HIDDEN);

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

        /*
        * Lấy phòng
        */
        Room room = getRoom(roomId);

        /*
        * Kiểm tra quyền
        */
        validateOwner(room);

        /*
        * Kiểm tra danh sách ảnh
        */
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ít nhất một ảnh.");
        }

        /*
        * Kiểm tra số lượng ảnh tối đa
        */
        List<RoomImage> currentImages =
                roomImageRepository.findByRoomIdOrderByDisplayOrder(roomId);

        if (currentImages.size() + files.size() > 20) {
            throw new BadRequestException(
                    "Mỗi phòng chỉ được tối đa 20 ảnh.");
        }

        /*
        * Upload ảnh lên Cloudinary
        */
        List<String> imageUrls =
                fileStorageService.storeRoomImages(roomId, files);

        /*
        * Kiểm tra đã có thumbnail chưa
        */
        boolean hasThumbnail = currentImages.stream()
                .anyMatch(RoomImage::getThumbnail);

        /*
        * Thứ tự hiển thị
        */
        int displayOrder = currentImages.size() + 1;

        /*
        * Tạo danh sách RoomImage
        */
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

        /*
        * Lưu một lần
        */
        roomImageRepository.saveAll(roomImages);

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

        /*
        * Chủ bài đăng
        */
        room.setOwner(owner);

        /*
        * Địa chỉ
        */
        room.setWard(ward);

        /*
        * Tiện ích
        */
        room.setAmenities(amenities);
        room.setAdditionalAmenities(request.getAdditionalAmenities());

        /*
        * Thông tin liên hệ
        */
        room.setContactName(request.getContactName());
        room.setContactPhone(request.getContactPhone());
        room.setZaloPhone(request.getZaloPhone());
        room.setZaloQrUrl(request.getZaloQrUrl());

        /*
        * Số lượng phòng
        */
        room.setQuantity(request.getQuantity());

        /*
        * Cài đặt liên hệ
        */
        room.setAllowBooking(request.getAllowBooking());
        room.setAllowCall(request.getAllowCall());
        room.setAllowChat(request.getAllowChat());
    }
    /**
     * Cập nhật dữ liệu Room
     */
    private void updateRoom(
        Room room,
        RoomCreateRequest request,
        Ward ward,
        Set<Amenity> amenities) {

    /*
     * ==========================
     * THÔNG TIN LIÊN HỆ
     * ==========================
     */
    room.setContactName(request.getContactName());
    room.setContactPhone(request.getContactPhone());
    room.setZaloPhone(request.getZaloPhone());
    room.setZaloQrUrl(request.getZaloQrUrl());

    /*
     * ==========================
     * THÔNG TIN BÀI ĐĂNG
     * ==========================
     */
    room.setTitle(request.getTitle());
    room.setDescription(request.getDescription());

    room.setRoomType(request.getRoomType());

    /*
     * ==========================
     * GIÁ
     * ==========================
     */
    room.setPrice(request.getPrice());
    room.setDeposit(request.getDeposit());
    room.setElectricPrice(request.getElectricPrice());
    room.setWaterPrice(request.getWaterPrice());

    /*
     * ==========================
     * THÔNG TIN PHÒNG
     * ==========================
     */
    room.setArea(request.getArea());
    room.setQuantity(request.getQuantity());

    /*
     * Nếu hiện tại số phòng còn trống lớn hơn tổng số phòng
     * thì điều chỉnh lại để không bị sai dữ liệu.
     */
    if (room.getAvailableQuantity() > request.getQuantity()) {
        room.setAvailableQuantity(request.getQuantity());
    }

    /*
     * ==========================
     * ĐỊA CHỈ
     * ==========================
     */
    room.setAddress(request.getAddress());
    room.setWard(ward);

    room.setLatitude(request.getLatitude());
    room.setLongitude(request.getLongitude());

    /*
     * ==========================
     * TIỆN ÍCH
     * ==========================
     */
    room.setAmenities(amenities);
    room.setAdditionalAmenities(request.getAdditionalAmenities());

    /*
     * ==========================
     * CÀI ĐẶT LIÊN HỆ
     * ==========================
     */
    room.setAllowBooking(request.getAllowBooking());
    room.setAllowCall(request.getAllowCall());
    room.setAllowChat(request.getAllowChat());
    }
    /**
     * Giá trị mặc định
     */
    private void applyDefaultValues(Room room) {

        /*
        * Trạng thái
        */
        room.setStatus(RoomStatus.AVAILABLE);
        room.setApprovalStatus(ApprovalStatus.PENDING);

        /*
        * Số lượng phòng còn trống
        */
        room.setAvailableQuantity(room.getQuantity());

        /*
        * Thống kê
        */
        room.setViewCount(0L);
        room.setFavoriteCount(0);
        room.setContactCount(0);
        room.setAppointmentCount(0);
        room.setShareCount(0);

        /*
        * Hiển thị
        */
        room.setFeatured(false);
        room.setPriorityScore(0);
        room.setVerified(false);

        /*
        * Thời gian
        */
        room.setPublishedAt(null);

        /*
        * Xóa mềm
        */
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