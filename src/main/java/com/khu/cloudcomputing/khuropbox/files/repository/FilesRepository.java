package com.khu.cloudcomputing.khuropbox.files.repository;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Integer> {
    Optional<Files> findById(Integer id);
    void deleteById(Integer id);
    //정렬
    Page<Files> findAllByTeamIdAndFileNameContainingAndIsRecycleBin(Pageable pageable, Integer teamId, String fileName, Boolean isRecycleBin);
    Page<Files> findAllByOwner_IdAndFileNameContainingAndIsRecycleBin(Pageable pageable, String userId, String fileName, Boolean isRecycleBin);
}
