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
import java.util.List;

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
        String changeDescription="test4: 이거저거 바꿔봤다";
        filesService.updateFile(fileUpdate, changeDescription);
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
    @DisplayName("파일 정렬 테스트: FileName")
    void orderFilesByName(){

        filesService.insertFile(new FilesDTO(Files.builder()
                .fileName("1test")
                .fileLink("/usr/bin")
                .fileSize(2048L)
                .fileType(".txt")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build()));

        filesService.insertFile(new FilesDTO(Files.builder()
                .fileName("test2")
                .fileLink("/usr/bin")
                .fileSize(2048L)
                .fileType(".txt")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build()));

        filesService.insertFile(new FilesDTO(Files.builder()
                .fileName("3test")
                .fileLink("/usr/bin")
                .fileSize(2048L)
                .fileType(".txt")
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .build()));

        List<FilesInformationDTO> orderedFiles = filesService.getFilesOrderBy("fileName");
        assertNotNull(orderedFiles);
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