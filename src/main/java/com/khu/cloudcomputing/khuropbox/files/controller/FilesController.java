package com.khu.cloudcomputing.khuropbox.files.controller;

import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.configuration.AwsService;
import com.khu.cloudcomputing.khuropbox.files.dto.FileHistoryDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FilesController {
    private final FilesService filesService;
    private final UserRepository userRepository;
    private final AwsService awsService;
    @GetMapping({"/"})
    public Page<FilesDTO> Files(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNum,
                                @RequestParam(required = false, defaultValue = "updatedAt", value = "orderBy") String orderBy,
                                @RequestParam(required = false, defaultValue = "DESC", value = "sort") String sort){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        return filesService.findUserPage(id,orderBy, pageNum, sort);
    }
    @GetMapping("info/{fileId}")
    public ResponseEntity<?> FilesId(@PathVariable(value="fileId") Integer fileId){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        FilesDTO file=filesService.findById(fileId);
        if(id.equals(file.getOwner().getId())){
            return ResponseEntity.ok(file);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/get-upload-url")
    public ResponseEntity<String> getUploadUrl(@RequestBody Map<String, String> params) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String dirName = params.get("dirName");
        Integer id = Integer.valueOf(params.get("id"));
        String fileType = params.get("fileType");
        String url = awsService.generateUploadPresignedUrl(dirName, id, fileType);
        return ResponseEntity.ok(url);
    }

    @GetMapping("download/{fileId}")
    public ResponseEntity<?> Download(@PathVariable(value = "fileId") Integer fileId) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);
        if (id.equals(file.getOwner().getId())) {
            String filePath = file.getFileLink().substring(50);
            log.info(filePath);
            URL presignedUrl = awsService.generateDownloadPresignedUrl(filePath);
            return ResponseEntity.ok(presignedUrl.toString());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("update")
    public void Update(@RequestBody FilesUpdateDTO fileUpdate){
        filesService.updateFile(fileUpdate);
    }

    @PostMapping("delete/{fileId}")
    public ResponseEntity<?> Delete(@PathVariable(value="fileId") Integer fileId){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        FilesDTO file=filesService.findById(fileId);
        if(id.equals(file.getOwner().getId())) {
            String filePath = file.getFileLink().substring(50);
            filesService.deleteAtS3(filePath);
            filesService.deleteFile(fileId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    //파일 히스토리 접근
    @GetMapping("fileHistory/{fileId}")
    public ResponseEntity<List<FileHistoryDTO>> getFileChangeHistory(@PathVariable Integer fileId) {
        List<FileHistoryDTO> changeHistory = filesService.getFileChangeHistory(fileId);
        return ResponseEntity.ok(changeHistory);
    }

    @GetMapping("share-file")
    public ResponseEntity<?> shareFile(@RequestParam Integer fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        FilesDTO file = filesService.findById(fileId);

        if (id.equals(file.getOwner().getId())) {
            String objectKey = file.getFileLink().substring(50); // Adjust the substring index based on your actual file path structure
            URL presignedUrl = awsService.generateDownloadPresignedUrl(objectKey);
            return ResponseEntity.ok().body(presignedUrl.toString());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

}
