package com.trofinder.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.trofinder.config.UploadConfig;
import com.trofinder.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final UploadConfig uploadConfig;

    @Override
    public List<String> storeRoomImages(
            Long roomId,
            List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<String> imageUrls = new ArrayList<>();

        try {

            Path roomDirectory = Paths.get(
                    uploadConfig.getUploadDir(),
                    "rooms",
                    roomId.toString());

            Files.createDirectories(roomDirectory);

            for (MultipartFile file : files) {

                validateImage(file);

                String fileName = generateFileName(file);

                Path destination = roomDirectory.resolve(fileName);

                Files.copy(
                        file.getInputStream(),
                        destination,
                        StandardCopyOption.REPLACE_EXISTING);

                imageUrls.add(
                        "/uploads/rooms/"
                                + roomId
                                + "/"
                                + fileName);
            }

            return imageUrls;

        } catch (IOException e) {

            throw new RuntimeException("Không thể upload ảnh.", e);

        }

    }

    @Override
    public void delete(String fileUrl) {

        try {

            String relativePath =
                    fileUrl.replaceFirst("^/uploads/", "");

            Path filePath = Paths.get(
                    uploadConfig.getUploadDir(),
                    relativePath);

            Files.deleteIfExists(filePath);

        } catch (IOException e) {

            throw new RuntimeException("Không thể xóa file.", e);

        }

    }

    /**
     * Validate ảnh
     */
    private void validateImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new BadRequestException("File rỗng.");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {

            throw new BadRequestException("Chỉ cho phép upload ảnh.");

        }

    }

    /**
     * Sinh tên file
     */
    private String generateFileName(MultipartFile file) {

        String original = file.getOriginalFilename();

        String extension = "";

        if (original != null && original.contains(".")) {

            extension = original.substring(
                    original.lastIndexOf("."));

        }

        return UUID.randomUUID() + extension;

    }

}