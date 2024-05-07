package com.khu.cloudcomputing.khuropbox.files.service;

import com.khu.cloudcomputing.khuropbox.files.dto.FileHistoryDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesInformationDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import com.khu.cloudcomputing.khuropbox.files.entity.FileHistoryEntity;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilesService{
    FilesInformationDTO findById(Integer id);
    String findLinkById(Integer id);
    List<FilesInformationDTO> findAll();
    //updateFile변경
    void updateFile(FilesUpdateDTO filesUpdateDTO, String fileDescription);
    void updateLink(Integer id, String fileLink);
    void deleteFile(Integer id);
    void deleteAtS3(String filePath);
    Integer insertFile(FilesDTO file);
    String upload(MultipartFile multipartFile, String dirName, Integer id, String fileType) throws IOException;
    ResponseEntity<byte[]> download(String fileUrl) throws IOException;

    //정렬
    public List<FilesInformationDTO> getFilesOrderBy(String orderby);
    //파일 히스토리
    public List<FileHistoryDTO> getFileChangeHistory(Integer id);
    public FileHistoryDTO mapToDTO(Files fileEntity, FileHistoryEntity fileHistoryEntity);
}