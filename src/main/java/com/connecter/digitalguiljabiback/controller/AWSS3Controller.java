package com.connecter.digitalguiljabiback.controller;

import com.connecter.digitalguiljabiback.service.AWSS3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "AWS S3", description = "AWS S3 관련 API")
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class AWSS3Controller {

    private final AWSS3Service awsS3Service;

    @Autowired
    public AWSS3Controller(AWSS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @Operation(summary = "사진 업로드", description = """
    201: 성공<br>
    """)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("filePath") MultipartFile file) {
        try {
            awsS3Service.uploadFileToFolder(file.getOriginalFilename(), file.getInputStream());
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
}

