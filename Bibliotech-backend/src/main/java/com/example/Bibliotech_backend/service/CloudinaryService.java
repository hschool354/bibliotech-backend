package com.example.Bibliotech_backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "bibliotech/books"));

            String publicId = (String) uploadResult.get("public_id");
            String url = (String) uploadResult.get("secure_url");

            logger.info("File uploaded successfully to Cloudinary. Public ID: {}", publicId);
            return url;
        } catch (IOException e) {
            logger.error("Error uploading file to Cloudinary", e);
            throw new RuntimeException("Could not upload image: " + e.getMessage());
        }
    }

    public boolean deleteFile(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            logger.error("Error deleting file from Cloudinary", e);
            return false;
        }
    }

    public String extractPublicIdFromUrl(String url) {
        // Implementation to extract public ID from the URL
        // This is just an example, adjust the logic as needed
        if (url == null || url.isEmpty()) {
            return null;
        }
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\.")[0];
    }
}