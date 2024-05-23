package com.khu.cloudcomputing.khuropbox.files.service;

import com.khu.cloudcomputing.khuropbox.files.dto.FileHistoryDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import com.khu.cloudcomputing.khuropbox.files.entity.FileHistoryEntity;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface FilesService{
    FilesDTO findById(Integer id);
    Page<FilesDTO> findUserPage(String userId, String orderby, int pageNum, String sort);
    Page<FilesDTO> findTeamFile(Integer teamId, String orderby, int pageNum, String sort);
    //updateFile변경
    void updateFile(FilesUpdateDTO filesUpdateDTO);
    void updateLink(Integer id, String fileLink);
    void deleteFile(Integer id);
    void deleteAtS3(String filePath);
    Integer insertFile(FilesDTO file);
    String upload(MultipartFile multipartFile, String dirName, Integer id, String fileType) throws IOException;
    ResponseEntity<byte[]> download(String fileUrl) throws IOException;

    //파일 히스토리
    List<FileHistoryDTO> getFileChangeHistory(Integer id);
    FileHistoryDTO mapToDTO(Files fileEntity, FileHistoryEntity fileHistoryEntity);


    //디렉토리 생성, 파일 이동
    public void createDirectory(String currentDit, String newDirName);
    public void moveFile(String source, String target) throws IOException;

    //파일 일회용 링크 생성-공유
    public URL generatePresignedUrl(String objectKey) throws IOException;

};