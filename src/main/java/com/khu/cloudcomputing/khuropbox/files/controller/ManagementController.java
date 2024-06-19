package com.khu.cloudcomputing.khuropbox.files.controller;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file-management")
public class ManagementController {

    private final FilesService filesService;
    ManagementController(FilesService filesService) {
        this.filesService = filesService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDirectory(
            @RequestParam String currentDir,
            @RequestParam String dirName) {
        try {
            filesService.createDirectory(currentDir, dirName);
            return ResponseEntity.ok("Directory created successfully at: " + currentDir + dirName);
        } catch (Exception e) {
            log.error("Failed to create directory under: {} with name: {}", currentDir, dirName, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating directory: " + e.getMessage(), e);
        }
    }

    @PostMapping("/move")
    public ResponseEntity<String> moveFile(
            @RequestParam String source,
            @RequestParam String target) {
        try {
            filesService.moveFile(source, target);
            return ResponseEntity.ok("File moved successfully.");
        } catch (IOException e) {
            log.error("Failed to move file from {} to {}", source, target, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error moving file: " + e.getMessage(), e);
        }
    }
}
