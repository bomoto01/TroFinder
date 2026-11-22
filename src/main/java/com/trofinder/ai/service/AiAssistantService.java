package com.trofinder.ai.service;

import org.springframework.stereotype.Service;

import com.trofinder.ai.client.AiClient;
import com.trofinder.entity.Amenity;
import com.trofinder.entity.Room;
import com.trofinder.repository.RoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiAssistantService {

    private final AiClient aiClient;
    private final RoomRepository roomRepository;

    public String ask(String question, Long roomId) {
        StringBuilder systemContext = new StringBuilder();
        systemContext.append("Bạn là trợ lý ảo bất động sản TroFinder thông minh, tận tâm. Hãy trả lời ngắn gọn, lịch sự, chính xác dựa trên thông tin phòng trọ được cung cấp.");

        if (roomId != null) {
            Room room = roomRepository.findByIdAndDeletedFalse(roomId).orElse(null);
            if (room != null) {
                systemContext.append(String.format(
                        " Dữ liệu phòng trọ hiện tại: Tiêu đề: '%s', Loại phòng: %s, Diện tích: %s m2, Giá thuê: %s VNĐ/tháng, Tiền cọc: %s VNĐ, Giá điện: %s VNĐ/số, Giá nước: %s VNĐ, Địa chỉ: '%s', Người liên hệ: %s (SĐT: %s).",
                        room.getTitle(),
                        room.getRoomType(),
                        room.getArea(),
                        room.getPrice(),
                        room.getDeposit(),
                        room.getElectricPrice(),
                        room.getWaterPrice(),
                        room.getAddress(),
                        room.getContactName(),
                        room.getContactPhone()
                ));

                if (room.getDescription() != null && !room.getDescription().isBlank()) {
                    systemContext.append(" Mô tả phòng từ chủ nhà: '").append(room.getDescription()).append("'.");
                }

                if (room.getAmenities() != null && !room.getAmenities().isEmpty()) {
                    String amenitiesList = room.getAmenities().stream()
                            .map(Amenity::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                    systemContext.append(" Tiện ích có sẵn: ").append(amenitiesList).append(".");
                }

                if (room.getAdditionalAmenities() != null && !room.getAdditionalAmenities().isBlank()) {
                    systemContext.append(" Tiện ích bổ sung: ").append(room.getAdditionalAmenities()).append(".");
                }
            }
        }

        return aiClient.generateResponse(systemContext.toString(), question);
    }

    public String getProviderName() {
        return aiClient.getProviderName();
    }
}
