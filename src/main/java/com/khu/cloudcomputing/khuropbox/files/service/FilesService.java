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
    Page<FilesDTO> findUserPage(String userId, String orderby, int pageNum, String sort,String search,Boolean isRecycleBin);
    Page<FilesDTO> findTeamFile(Integer teamId, String orderby, int pageNum, String sort,String search, Boolean isRecycleBin);
    //updateFile변경
    void updateFile(FilesUpdateDTO filesUpdateDTO);
    void recycleBinFile(Integer id);
    void restoreFile(Integer id);
    void deleteFile(Integer id);
    void deleteAtS3(String filePath);
    Integer insertFile(FilesDTO file);

    //파일 히스토리
    List<FileHistoryDTO> getFileChangeHistory(Integer id);
    FileHistoryDTO mapToDTO(Files fileEntity, FileHistoryEntity fileHistoryEntity);

    Boolean isExistFileKey(String userId, String fileKey);
    //디렉토리 생성, 파일 이동
    public void createDirectory(String currentDit, String newDirName);
    public void moveFile(String source, String target) throws IOException;
};