package com.livestock.backend.controller;



import com.livestock.backend.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/animal-photo")
    public ResponseEntity<Map<String, String>> uploadAnimalPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        String url = fileService.uploadFile(file, "animal-photos");
        return ResponseEntity.ok(Map.of("url", url, "message", "Photo uploaded successfully"));
    }

    @PostMapping("/receipt")
    public ResponseEntity<Map<String, String>> uploadReceipt(@RequestParam("file") MultipartFile file) throws IOException {
        String url = fileService.uploadFile(file, "receipts");
        return ResponseEntity.ok(Map.of("url", url, "message", "Receipt uploaded successfully"));
    }
}