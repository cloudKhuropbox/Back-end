package com.khu.cloudcomputing.khuropbox.files.controller;

import com.khu.cloudcomputing.khuropbox.files.dto.FileHistoryDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesInformationDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FilesController {
    private final FilesService filesService;
    @GetMapping("files")
    public List<FilesInformationDTO> Files(@RequestParam(required = false, value="orderby")String orderby){
        return filesService.getFilesOrderBy(orderby);
    }
    @GetMapping("files/{id}")
    public FilesInformationDTO FilesId(@PathVariable(value="id") Integer id){
        return filesService.findById(id);
    }
    @PostMapping("delete/{id}")
    public void Delete(@PathVariable(value="id") Integer id){
        String filePath = filesService.findLinkById(id).substring(51);
        filesService.deleteAtS3(filePath);
        filesService.deleteFile(id);
    }
    //file description 추가
    @PostMapping("update")
    public void Update(@RequestBody FilesUpdateDTO fileUpdate){
        filesService.updateFile(fileUpdate);
    }

    @PostMapping("upload")
    public void Upload(@RequestPart(value="fileName") String fileName, @RequestPart(value = "file") MultipartFile multipartFile) {
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
            } catch (IOException e) {
                log.info("error");
            }
        }
    }
    @GetMapping("download/{id}")
    public ResponseEntity<byte[]> Download(@PathVariable(value="id") Integer id) throws IOException {
        String filePath = filesService.findLinkById(id).substring(51);
        log.info(filePath);
        return filesService.download(filePath);
    }

    //파일 히스토리 접근
    @GetMapping("fileHistory/{fileId}")
    public ResponseEntity<List<FileHistoryDTO>> getFileChangeHistory(@PathVariable Integer fileId) {
        List<FileHistoryDTO> changeHistory = filesService.getFileChangeHistory(fileId);
        return ResponseEntity.ok(changeHistory);
    }
}
