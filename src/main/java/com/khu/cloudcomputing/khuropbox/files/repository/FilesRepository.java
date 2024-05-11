package com.khu.cloudcomputing.khuropbox.files.repository;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Integer> {
    Optional<Files> findById(Integer id);
    void deleteById(Integer id);
    //정렬
    List<Files> findAllByOrderByUpdatedAtDesc();
    List<Files> findAllByOrderByFileName();
    List<Files> findAllByOrderByFileSizeDesc();
    List<Files> findAllByOrderByFileType();
    List<Files> findAllByTeamIdOrderByUpdatedAtDesc(Integer teamId);
    List<Files> findAllByTeamIdOrderByFileName(Integer teamId);
    List<Files> findAllByTeamIdOrderByFileSizeDesc(Integer teamId);
    List<Files> findAllByTeamIdOrderByFileType(Integer teamId);
    List<Files> findAllByOwner_IdOrderByUpdatedAtDesc(String userId);
    List<Files> findAllByOwner_IdOrderByFileName(String userId);
    List<Files> findAllByOwner_IdOrderByFileSizeDesc(String userId);
    List<Files> findAllByOwner_IdOrderByFileType(String userId);
}
