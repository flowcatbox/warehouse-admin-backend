package com.warehouse.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @PostMapping("/single")
    public ResponseEntity<?> uploadSingleFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "file-" + System.currentTimeMillis());
        response.put("filename", file.getOriginalFilename());
        response.put("originalName", file.getOriginalFilename());
        response.put("fileSize", file.getSize());
        response.put("fileType", file.getContentType());
        response.put("url", "https://example.com/files/" + file.getOriginalFilename());
        response.put("uploadTime", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/multiple")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object>[] responses = new HashMap[files.length];

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            Map<String, Object> response = new HashMap<>();
            response.put("id", "file-" + System.currentTimeMillis() + "-" + i);
            response.put("filename", file.getOriginalFilename());
            response.put("originalName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("fileType", file.getContentType());
            response.put("url", "https://example.com/files/" + file.getOriginalFilename());
            response.put("uploadTime", java.time.LocalDateTime.now().toString());
            responses[i] = response;
        }

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        return ResponseEntity.ok().build();
    }
}