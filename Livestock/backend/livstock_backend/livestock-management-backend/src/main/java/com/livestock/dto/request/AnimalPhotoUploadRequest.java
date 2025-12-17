// src/main/java/com/livestock/dto/request/AnimalPhotoUploadRequest.java
package com.livestock.dto.request;

import org.springframework.web.multipart.MultipartFile;

public class AnimalPhotoUploadRequest {

    private MultipartFile photo;

    // Getter and Setter
    public MultipartFile getPhoto() {
        return photo;
    }

    public void setPhoto(MultipartFile photo) {
        this.photo = photo;
    }
}