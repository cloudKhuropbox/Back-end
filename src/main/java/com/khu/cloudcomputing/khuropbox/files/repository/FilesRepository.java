package com.khu.cloudcomputing.khuropbox.files.repository;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Integer> {
    List<Files> findAll();
    Optional<Files> findById(Integer id);
    void deleteById(Integer id);

    //정렬
    List<Files> findAllByOrderByUpdatedAtDesc();
    List<Files> findAllByOrderByFileName();
    List<Files> findAllByOrderByFileSizeDesc();
    List<Files> findAllByOrderByFileType();
}
