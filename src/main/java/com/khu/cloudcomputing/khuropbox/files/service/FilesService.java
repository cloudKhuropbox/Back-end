package com.khu.cloudcomputing.khuropbox.files.service;

import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesInformationDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilesService{
    FilesInformationDTO findById(Integer id);
    String findLinkById(Integer id);
    List<FilesInformationDTO> findAll();
    void updateFile(FilesUpdateDTO filesUpdateDTO);
    void updateLink(Integer id, String fileLink);
    void deleteFile(Integer id);
    void deleteAtS3(String filePath);
    Integer insertFile(FilesDTO file);
    String upload(MultipartFile multipartFile, String dirName, Integer id, String fileType) throws IOException;
    ResponseEntity<byte[]> download(String fileUrl) throws IOException;

    //정렬
    public List<FilesInformationDTO> getFilesOrderBy(String orderby);
}