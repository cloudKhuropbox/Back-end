package com.khu.cloudcomputing.khuropbox.files.controller;

import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FilesController {
    private final FilesService filesService;
    private final UserRepository userRepository;
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
    @PostMapping("upload")
    public ResponseEntity<?> Upload(@RequestPart(value = "file") List<MultipartFile> multipartFiles) {
        String fileLink = "";
        List<FilesDTO> filesList=new ArrayList<>();
        for(MultipartFile multipartFile:multipartFiles) {
            if (multipartFile != null) { // 파일 업로드한 경우에만
                try {// 파일 업로드
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    String id = authentication.getName();
                    FilesDTO file = new FilesDTO();
                    file.setFileName(multipartFile.getOriginalFilename());
                    file.setFileSize(multipartFile.getSize());
                    file.setFileLink(fileLink);
                    file.setOwner(userRepository.findAllById(id).orElseThrow());
                    String fileType = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
                    file.setFileType(fileType);
                    Integer index = filesService.insertFile(file);
                    fileLink = filesService.upload(multipartFile, id + '/', index, fileType); // S3 버킷의 images 디렉토리 안에 저장됨, S3에 저장된 이름은 id값으로 부여.
                    fileLink = URLDecoder.decode(fileLink, StandardCharsets.UTF_8);
                    log.info("fileLink = " + fileLink);
                    filesService.updateLink(index, fileLink);
                    filesList.add(filesService.findById(index));
                } catch (IOException e) {
                    log.info("error");
                    return ResponseEntity.badRequest().build();
                }
            }
        }
        return ResponseEntity.ok(filesList);
    }
    @GetMapping("download/{fileId}")
    public ResponseEntity<?> Download(@PathVariable(value="fileId") Integer fileId) throws IOException {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String id=authentication.getName();
        FilesDTO file=filesService.findById(fileId);
        if(id.equals(file.getOwner().getId())) {
            String filePath = file.getFileLink().substring(50);
            log.info(filePath);
            return ResponseEntity.ok(filesService.download(filePath));
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
}
