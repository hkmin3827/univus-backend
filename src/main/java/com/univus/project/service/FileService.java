package com.univus.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

// 이미지 업로드 처리를 위한 service 로직
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileService {
    private final String uploadDir = "/uploads/";

    public String saveImage(MultipartFile image) {

        try {
            String originalName = image.getOriginalFilename();
            String fileName = UUID.randomUUID() + "_" + originalName;

            Path savePath = Paths.get(uploadDir, fileName);

            Files.createDirectories(savePath.getParent());
            image.transferTo(savePath.toFile());

            return "/static/images/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패");
        }
    }
}
