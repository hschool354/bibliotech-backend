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
        if (url == null || url.isEmpty()) {
            return null;
        }

        try {
            //https://res.cloudinary.com/dgtfegznk/image/upload/v1741658639/Call_It_Home_r8bsqb.jpg
            //https://res.cloudinary.com/dgtfegznk/image/upload/v1741658639/Don_t_Push_the_Button_cvmlva.jpg
            // Extract the public ID from a URL like:
            String[] urlParts = url.split("/");
            String fileName = urlParts[urlParts.length - 1];
            String folder = urlParts[urlParts.length - 2];
            String publicId = folder + "/" + fileName.substring(0, fileName.lastIndexOf('.'));
            return publicId;
        } catch (Exception e) {
            logger.error("Error extracting public ID from URL: {}", url, e);
            return null;
        }
    }
}