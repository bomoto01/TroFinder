package com.trofinder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Getter
@Configuration
public class UploadConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

}