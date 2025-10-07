package com.livestock.backend.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadFile(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file");
        }
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File directory = new File(uploadDir + "/" + subDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File dest = new File(directory.getAbsolutePath() + "/" + fileName);
        file.transferTo(dest);
        return "/uploads/" + subDir + "/" + fileName;
    }
}