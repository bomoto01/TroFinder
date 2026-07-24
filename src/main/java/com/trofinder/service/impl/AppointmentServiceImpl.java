package com.trofinder.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trofinder.dto.request.AppointmentCreateRequest;
import com.trofinder.dto.request.AppointmentUpdateRequest;
import com.trofinder.dto.response.AppointmentResponse;
import com.trofinder.entity.Appointment;
import com.trofinder.entity.Room;
import com.trofinder.entity.User;
import com.trofinder.enums.AppointmentStatus;
import com.trofinder.exception.BadRequestException;
import com.trofinder.exception.ResourceNotFoundException;
import com.trofinder.exception.UnauthorizedException;
import com.trofinder.mapper.AppointmentMapper;
import com.trofinder.repository.AppointmentRepository;
import com.trofinder.repository.RoomRepository;
import com.trofinder.service.interfaces.AppointmentService;
import com.trofinder.service.interfaces.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    /*
     * ==========================================================
     * DEPENDENCY
     * ==========================================================
     */

    private final AppointmentRepository appointmentRepository;

    private final RoomRepository roomRepository;

    private final AppointmentMapper appointmentMapper;

    private final CurrentUserService currentUserService;

    /*
     * ==========================================================
     * CREATE
     * ==========================================================
     */

    @Override
    @Transactional
    public AppointmentResponse create(AppointmentCreateRequest request) {

        /*
         * Người thuê hiện tại
         */
        User tenant = getCurrentUser();

        /*
         * Phòng cần đặt
         */
        Room room = getRoom(request.getRoomId());

        /*
         * Kiểm tra thời gian hợp lệ
         */
        validateAppointmentTime(
                request.getAppointmentDate(),
                request.getAppointmentTime());

        /*
         * Kiểm tra trùng lịch
         */
        validateDuplicateAppointment(
                room,
                tenant,
                request.getAppointmentDate(),
                request.getAppointmentTime());

        /*
         * Mapping DTO -> Entity
         */
        Appointment appointment =
                appointmentMapper.toEntity(request);

        /*
         * Thiết lập dữ liệu
         */
        appointment.setRoom(room);

        appointment.setOwner(room.getOwner());

        appointment.setTenant(tenant);

        appointment.setStatus(AppointmentStatus.PENDING);

        appointment.setDeleted(false);

        /*
         * Lưu
         */
        return saveAppointment(appointment);
    }

    /*
     * ==========================================================
     * READ
     * ==========================================================
     */

    @Override
    public AppointmentResponse getById(Long id) {

        Appointment appointment =
                getAppointment(id);

        return appointmentMapper.toResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getMyAppointments() {

        User tenant = getCurrentUser();

        return appointmentRepository
                .findByTenantAndDeletedFalseOrderByCreatedAtDesc(tenant)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getOwnerAppointments() {

        User owner = getCurrentUser();

        return appointmentRepository
                .findByOwnerAndDeletedFalseOrderByCreatedAtDesc(owner)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

    @Override
    public List<AppointmentResponse> getRoomAppointments(Long roomId) {

        Room room = getRoom(roomId);

        /*
         * Chỉ chủ phòng mới được xem
         */
        if (!room.getOwner().getId().equals(getCurrentUser().getId())) {

            throw new UnauthorizedException(
                    "Bạn không có quyền xem lịch của phòng này.");
        }

        return appointmentRepository
                .findByRoomAndDeletedFalseOrderByAppointmentDateAscAppointmentTimeAsc(room)
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

        /*
     * ==========================================================
     * UPDATE
     * ==========================================================
     */

    @Override
    @Transactional
    public AppointmentResponse updateStatus(
            Long id,
            AppointmentUpdateRequest request) {

        /*
         * Lấy lịch hẹn
         */
        Appointment appointment =
                getAppointment(id);

        /*
         * Chỉ chủ trọ được cập nhật
         */
        validateOwner(appointment);

        AppointmentStatus newStatus =
                request.getStatus();

        /*
         * Kiểm tra luồng chuyển trạng thái
         */
        validateStatusTransition(
                appointment.getStatus(),
                newStatus);

        /*
         * Cập nhật trạng thái
         */
        appointment.setStatus(newStatus);

        /*
         * Ghi chú
         */
        appointment.setNote(request.getNote());

        /*
         * Thời gian xử lý
         */
        switch (newStatus) {

            case CONFIRMED ->
                    appointment.setConfirmedAt(LocalDateTime.now());

            case CANCELLED ->
                    appointment.setCancelledAt(LocalDateTime.now());

            case COMPLETED ->
                    appointment.setCompletedAt(LocalDateTime.now());

            default -> {
            }
        }

        return saveAppointment(appointment);
    }

    /*
     * ==========================================================
     * DELETE
     * ==========================================================
     */

    @Override
    @Transactional
    public void delete(Long id) {

        /*
         * Lấy lịch hẹn
         */
        Appointment appointment =
                getAppointment(id);

        User currentUser =
                getCurrentUser();

        /*
         * Chỉ người thuê hoặc chủ trọ mới được hủy
         */
        if (!appointment.getTenant().getId().equals(currentUser.getId())
                && !appointment.getOwner().getId().equals(currentUser.getId())) {

            throw new UnauthorizedException(
                    "Bạn không có quyền hủy lịch hẹn này.");
        }

        /*
         * Đã hủy rồi
         */
        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || Boolean.TRUE.equals(appointment.getDeleted())) {

            throw new BadRequestException(
                    "Lịch hẹn đã bị hủy.");
        }

        /*
         * Cập nhật trạng thái
         */
        appointment.setStatus(AppointmentStatus.CANCELLED);

        appointment.setCancelledAt(LocalDateTime.now());

        /*
         * Xóa mềm
         */
        appointment.setDeleted(true);

        saveAppointment(appointment);
    }

        /*
     * ==========================================================
     * PRIVATE METHOD
     * ==========================================================
     */

    /**
     * Lấy người dùng hiện tại.
     */
    private User getCurrentUser() {

        return currentUserService.getCurrentUser();
    }

    /**
     * Lấy phòng theo ID.
     */
    private Room getRoom(Long roomId) {

        return roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy phòng."));
    }

    /**
     * Lấy lịch hẹn theo ID.
     */
    private Appointment getAppointment(Long id) {

        return appointmentRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy lịch hẹn."));
    }

    /**
     * Kiểm tra thời gian đặt lịch.
     */
    private void validateAppointmentTime(
            LocalDate appointmentDate,
            LocalTime appointmentTime) {

        LocalDate today = LocalDate.now();

        if (appointmentDate.isBefore(today)) {

            throw new BadRequestException(
                    "Không thể đặt lịch trong quá khứ.");
        }

        if (appointmentDate.equals(today)
                && appointmentTime.isBefore(LocalTime.now())) {

            throw new BadRequestException(
                    "Không thể đặt lịch trong quá khứ.");
        }
    }

    /**
     * Kiểm tra người thuê đã có lịch hẹn chưa.
     */
    private void validateDuplicateAppointment(
            Room room,
            User tenant,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {

        /*
         * Người thuê đã có lịch đang hoạt động
         */
        boolean tenantExists =
                appointmentRepository
                        .existsByRoomAndTenantAndAppointmentDateAndAppointmentTimeAndStatusInAndDeletedFalse(
                                room,
                                tenant,
                                appointmentDate,
                                appointmentTime,
                                List.of(
                                        AppointmentStatus.PENDING,
                                        AppointmentStatus.CONFIRMED));

        if (tenantExists) {

            throw new BadRequestException(
                    "Bạn đã đặt lịch vào thời gian này.");
        }

        /*
         * Phòng đã có người khác đặt
         */
        boolean roomExists =
                appointmentRepository
                        .existsByRoomAndAppointmentDateAndAppointmentTimeAndStatusInAndDeletedFalse(
                                room,
                                appointmentDate,
                                appointmentTime,
                                List.of(
                                        AppointmentStatus.PENDING,
                                        AppointmentStatus.CONFIRMED));

        if (roomExists) {

            throw new BadRequestException(
                    "Khung giờ này đã có người đặt.");
        }
    }

    /**
     * Kiểm tra quyền chủ trọ.
     */
    private void validateOwner(Appointment appointment) {

        User currentUser = getCurrentUser();

        if (!appointment.getOwner().getId().equals(currentUser.getId())) {

            throw new UnauthorizedException(
                    "Bạn không có quyền thực hiện chức năng này.");
        }
    }

    /**
     * Kiểm tra luồng chuyển trạng thái.
     */
    private void validateStatusTransition(
            AppointmentStatus currentStatus,
            AppointmentStatus newStatus) {

        if (currentStatus == newStatus) {
            return;
        }

        switch (currentStatus) {

            case PENDING -> {

                if (newStatus != AppointmentStatus.CONFIRMED
                        && newStatus != AppointmentStatus.CANCELLED) {

                    throw new BadRequestException(
                            "Không thể chuyển từ "
                                    + currentStatus
                                    + " sang "
                                    + newStatus);
                }
            }

            case CONFIRMED -> {

                if (newStatus != AppointmentStatus.COMPLETED
                        && newStatus != AppointmentStatus.CANCELLED) {

                    throw new BadRequestException(
                            "Không thể chuyển từ "
                                    + currentStatus
                                    + " sang "
                                    + newStatus);
                }
            }

            case CANCELLED ->

                throw new BadRequestException(
                        "Lịch hẹn đã bị hủy.");

            case COMPLETED ->

                throw new BadRequestException(
                        "Lịch hẹn đã hoàn thành.");

            default ->

                throw new BadRequestException(
                        "Trạng thái không hợp lệ.");
        }
    }

    /**
     * Lưu lịch hẹn.
     */
    private AppointmentResponse saveAppointment(
            Appointment appointment) {

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

}
