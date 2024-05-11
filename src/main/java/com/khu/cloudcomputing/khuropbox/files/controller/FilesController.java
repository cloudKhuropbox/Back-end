package com.khu.cloudcomputing.khuropbox.files.controller;

import com.khu.cloudcomputing.khuropbox.files.dto.FileHistoryDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FilesController {
    private final FilesService filesService;
    @GetMapping("list")
    public List<FilesDTO> Files(@RequestParam(required = false, value="orderby")String orderby){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        return filesService.findUserFile(id,orderby);
    }
    @GetMapping("info/{id}")
    public ResponseEntity<?> FilesId(@PathVariable(value="id") Integer fileId){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        FilesDTO file=filesService.findById(fileId);
        if(id.equals(file.getOwner().getId())){
            return ResponseEntity.ok(file);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("delete/{fileId}")
    public ResponseEntity<?> Delete(@PathVariable(value="fileId") Integer fileId){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        FilesDTO file=filesService.findById(fileId);
        if(id.equals(file.getOwner().getId())) {
            String filePath = file.getFileLink().substring(51);
            filesService.deleteAtS3(filePath);
            filesService.deleteFile(fileId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    //file description 추가
    @PostMapping("update")
    public void Update(@RequestBody FilesUpdateDTO fileUpdate){
        filesService.updateFile(fileUpdate);
    }

    @PostMapping("upload")
    public ResponseEntity<?> Upload(@RequestPart(value="fileName") String fileName, @RequestPart(value = "file") MultipartFile multipartFile) {
        String fileLink = "";
        if (multipartFile != null) { // 파일 업로드한 경우에만
            try {// 파일 업로드
                FilesDTO file=new FilesDTO();
                file.setFileName(fileName);
                file.setFileSize(multipartFile.getSize());
                file.setFileLink(fileLink);
                String fileType=multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")+1);
                file.setFileType(fileType);
                Integer index=filesService.insertFile(file);
                fileLink = filesService.upload(multipartFile, "", index, fileType); // S3 버킷의 images 디렉토리 안에 저장됨, S3에 저장된 이름은 id값으로 부여.
                fileLink= URLDecoder.decode(fileLink, StandardCharsets.UTF_8);
                log.info("fileLink = " + fileLink);
                filesService.updateLink(index, fileLink);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                log.info("error");
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
    @GetMapping("download/{fileId}")
    public ResponseEntity<?> Download(@PathVariable(value="fileId") Integer fileId) throws IOException {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        FilesDTO file=filesService.findById(fileId);
        if(id.equals(file.getOwner().getId())) {
            String filePath = file.getFileLink().substring(51);
            log.info(filePath);
            return ResponseEntity.ok(filesService.download(filePath));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    //파일 히스토리 접근
    @GetMapping("fileHistory/{fileId}")
    public ResponseEntity<List<FileHistoryDTO>> getFileChangeHistory(@PathVariable Integer fileId) {
        List<FileHistoryDTO> changeHistory = filesService.getFileChangeHistory(fileId);
        return ResponseEntity.ok(changeHistory);
    }
}
