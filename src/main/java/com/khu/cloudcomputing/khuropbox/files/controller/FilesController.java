package com.khu.cloudcomputing.khuropbox.files.controller;

import com.amazonaws.Request;
import com.khu.cloudcomputing.khuropbox.apiPayload.ApiResponse;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.SuccessStatus;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.configuration.aws.AwsService;
import com.khu.cloudcomputing.khuropbox.files.dto.FileHistoryDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FileMultipartUploadUrlDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FilesController {
    private final FilesService filesService;
    private final AwsService awsService;
    private final UserRepository userRepository;

    @GetMapping({"/list"})
    public ResponseEntity<ApiResponse<Page<FilesDTO>>> Files(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNum,
                                                             @RequestParam(required = false, defaultValue = "updatedAt", value = "orderBy") String orderBy,
                                                             @RequestParam(required = false, defaultValue = "DESC", value = "sort")String sort,
                                                             @RequestParam(required = false, defaultValue = "", value = "search")String search,
                                                             @RequestParam(required = false, defaultValue = "false", value = "recycleBin")boolean recycleBin) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Page<FilesDTO> files = filesService.findUserPage(id, orderBy, pageNum, sort,search,recycleBin);
        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, files));
    }
    @GetMapping("info/{fileId}")
    public ResponseEntity<ApiResponse<FilesDTO>> FilesId(@PathVariable(value = "fileId") Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);
        if (id.equals(file.getOwner().getId())) {
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, file));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("/start-upload")
    public ResponseEntity<List<FileMultipartUploadUrlDTO>> startMultipartUpload(
            @RequestParam String key,
            @RequestParam long fileSize) {
        int partCount = awsService.calculateMultipartCount(fileSize, 10);
        String uploadId = awsService.createMultipartUpload(key).uploadId();
        List<FileMultipartUploadUrlDTO> presignedUrls = awsService.generateWriteOnlyMultipartPresignedUrls(
                key, Duration.ofMinutes(60), uploadId, partCount);
        return ResponseEntity.ok(presignedUrls);
    }
    @PostMapping("/complete-upload")
    public ResponseEntity<CompleteMultipartUploadResponse> completeMultipartUpload(
            @RequestParam String key,
            @RequestParam String uploadId,
            @RequestBody List<CompletedPart> parts,
            @RequestBody FilesDTO filesDTO) {
        CompleteMultipartUploadResponse response = awsService.completeMultipartUpload(key, uploadId, parts);
        filesService.insertFile(filesDTO);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/abort-upload")
    public ResponseEntity<AbortMultipartUploadResponse> abortUpload(
            @RequestParam String key,
            @RequestParam String uploadId) {
        AbortMultipartUploadResponse response = awsService.abortMultipartUpload(key, uploadId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("download/{fileId}")
    public ResponseEntity<ApiResponse<String>> Download(@PathVariable(value = "fileId") Integer fileId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);
        if (id.equals(file.getOwner().getId())) {
            String filePath = file.getFileLink().substring(50);
            log.info(filePath);
            URL presignedUrl = awsService.generateDownloadPresignedUrl(filePath);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, presignedUrl.toString()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("update")
    public ResponseEntity<ApiResponse<String>> Update(@RequestBody FilesUpdateDTO fileUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileUpdate.getId());
        if (id.equals(file.getOwner().getId())) {
            filesService.updateFile(fileUpdate);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._FILE_UPDATED));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("recyclebin/{fileId}")
    public ResponseEntity<ApiResponse<String>> RecycleBin(@PathVariable(value = "fileId") Integer fileId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);
        if (id.equals(file.getOwner().getId())) {
            filesService.recycleBinFile(fileId);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._FILE_UPDATED));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("restore/{fileId}")
    public ResponseEntity<ApiResponse<String>> Restore(@PathVariable(value = "fileId") Integer fileId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);
        if (id.equals(file.getOwner().getId())) {
            filesService.restoreFile(fileId);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._FILE_UPDATED));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
    @PostMapping("delete/{fileId}")
    public ResponseEntity<ApiResponse<String>> Delete(@PathVariable(value = "fileId") Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);
        if (id.equals(file.getOwner().getId())) {
            String filePath = file.getFileLink().substring(50);
            filesService.deleteAtS3(filePath);
            filesService.deleteFile(fileId);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._FILE_DELETED));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }

    @GetMapping("fileHistory/{fileId}")
    public ResponseEntity<ApiResponse<List<FileHistoryDTO>>> getFileChangeHistory(@PathVariable Integer fileId) {
        List<FileHistoryDTO> changeHistory = filesService.getFileChangeHistory(fileId);
        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, changeHistory));
    }

    @GetMapping("share-file")
    public ResponseEntity<ApiResponse<String>> shareFile(@RequestParam Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);

        if (id.equals(file.getOwner().getId())) {
            String objectKey = file.getFileLink().substring(50); // Adjust the substring index based on your actual file path structure
            URL presignedUrl = awsService.generateDownloadPresignedUrl(objectKey);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, presignedUrl.toString()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
}