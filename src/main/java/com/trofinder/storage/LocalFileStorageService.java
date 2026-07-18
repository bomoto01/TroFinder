package com.trofinder.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    // ==========================================================
    // IMAGE
    // ==========================================================

    @Override
    public List<String> storeRoomImages(
            Long roomId,
            List<MultipartFile> files) {

        return storeFiles(
                roomId,
                files,
                "rooms",
                FileType.IMAGE);
    }

    // ==========================================================
    // VIDEO
    // ==========================================================

    @Override
    public String storeRoomVideo(
            Long roomId,
            MultipartFile file) {

        validate(file, FileType.VIDEO);

        try {

            Path roomDirectory = Paths.get(
                    uploadConfig.getUploadDir(),
                    "videos",
                    roomId.toString());

            Files.createDirectories(roomDirectory);

            String fileName = generateFileName(file);

            Path destination =
                    roomDirectory.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/videos/"
                    + roomId
                    + "/"
                    + fileName;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Không thể upload video.",
                    e);

        }

    }

    // ==========================================================
    // DELETE
    // ==========================================================

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

            throw new RuntimeException(
                    "Không thể xóa file.",
                    e);

        }

    }

    @Override
    public void deleteVideo(String fileUrl) {
        delete(fileUrl);
    }

    // ==========================================================
    // PRIVATE
    // ==========================================================

    /**
     * Upload nhiều ảnh
     */
    private List<String> storeFiles(
            Long roomId,
            List<MultipartFile> files,
            String folder,
            FileType fileType) {

        if (files == null || files.isEmpty()) {
            return List.of();
        }

        try {

            Path roomDirectory = Paths.get(
                    uploadConfig.getUploadDir(),
                    folder,
                    roomId.toString());

            Files.createDirectories(roomDirectory);

            return files.stream()
                    .map(file -> saveFile(
                            roomDirectory,
                            roomId,
                            folder,
                            file,
                            fileType))
                    .toList();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Không thể upload file.",
                    e);

        }

    }

    /**
     * Lưu một file
     */
    private String saveFile(
            Path roomDirectory,
            Long roomId,
            String folder,
            MultipartFile file,
            FileType fileType) {

        validate(file, fileType);

        String fileName = generateFileName(file);

        Path destination =
                roomDirectory.resolve(fileName);

        try {

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

        return "/uploads/"
                + folder
                + "/"
                + roomId
                + "/"
                + fileName;

    }

    /**
     * Validate file
     */
    private void validate(
            MultipartFile file,
            FileType fileType) {

        if (file.isEmpty()) {
            throw new BadRequestException("File rỗng.");
        }

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new BadRequestException(
                    "Không xác định được loại file.");
        }

        switch (fileType) {

            case IMAGE -> {

                if (!contentType.startsWith("image/")) {

                    throw new BadRequestException(
                            "Chỉ cho phép upload ảnh.");

                }

            }

            case VIDEO -> {

                if (!List.of(
                        "video/mp4",
                        "video/webm",
                        "video/quicktime")
                        .contains(contentType)) {

                    throw new BadRequestException(
                            "Chỉ hỗ trợ MP4, WEBM hoặc MOV.");

                }

            }

        }

    }

    /**
     * Sinh tên file
     */
    private String generateFileName(
            MultipartFile file) {

        String original = file.getOriginalFilename();

        String extension = "";

        if (original != null && original.contains(".")) {

            extension =
                    original.substring(
                            original.lastIndexOf("."));

        }

        return UUID.randomUUID() + extension;

    }

    /**
     * Loại file
     */
    private enum FileType {
        IMAGE,
        VIDEO
    }

}