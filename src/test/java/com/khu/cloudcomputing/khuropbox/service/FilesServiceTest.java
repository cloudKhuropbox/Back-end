package com.khu.cloudcomputing.khuropbox.service;

import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesInformationDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import com.khu.cloudcomputing.khuropbox.files.service.FilesService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilesServiceTest {
    @Autowired
    private FilesService filesService;
    @Test
    @Transactional
    @DisplayName("파일 업로드, id로 찾기 테스트")
    void insertAndFindFileById() {
        //given
        Integer insertFile=filesService.insertFile(new FilesDTO(Files.builder()
                .fileName("test")
                .fileLink("/usr/bin")
                .fileSize(2048L)
                .fileType(".txt")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build()));
        //when
        FilesInformationDTO findFile=filesService.findById(insertFile);
        //then
        assertEquals(insertFile, findFile.getId());
    }
    @Test
    @Transactional
    @DisplayName("파일 이름 업데이트 테스트")
    void updateFileName() {
        //given
        Integer insertFile=filesService.insertFile(new FilesDTO(Files.builder()
                .fileName("test2")
                .fileLink("/usr/bin")
                .fileSize(2048L)
                .fileType(".txt")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build()));
        //when
        String updatedName="test3";
        FilesUpdateDTO fileUpdate=new FilesUpdateDTO(insertFile, updatedName,"/");
        filesService.updateFile(fileUpdate);
        FilesInformationDTO updatedFile=filesService.findById(insertFile);
        //then
        assertEquals(updatedName, updatedFile.getFileName());
        assertNotNull(updatedFile.getUpdatedAt());
    }

    @Test
    @DisplayName("모든 파일 찾는 테스트")
    void findAll() {
    }

    @Test
    @Transactional
    @DisplayName("파일 삭제 테스트")
    void deleteFile() {
        //given
        Integer insertFile=filesService.insertFile(new FilesDTO(Files.builder()
                        .fileName("test4")
                        .fileLink("/usr/bin")
                        .fileSize(2048L)
                        .fileType(".txt")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .build())
                );
        //when
        filesService.deleteFile(insertFile);
        //then
        assertNull(filesService.findById(insertFile).getId());
    }

}