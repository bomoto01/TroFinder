package com.trofinder.storage;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Upload nhiều ảnh của một phòng.
     */
    List<String> storeRoomImages(
            Long roomId,
            List<MultipartFile> files
    );
    String storeRoomVideo(
        Long roomId,
        MultipartFile file);

    /**
     * Xóa một file.
     */
    void delete(String fileUrl);

    void deleteVideo(String fileUrl);
}