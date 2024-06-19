package com.khu.cloudcomputing.khuropbox.files.repository;

import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends JpaRepository<Files, Integer> {
    Optional<Files> findById(Integer id);
    void deleteById(Integer id);
    //정렬
    @Query("select u.fileKey from Files u where u.owner.id=:userId")
    List<String> findFileKey(@Param(value = "userId")String userId);
    Page<Files> findAllByTeamIdAndFileNameContainingAndIsRecycleBin(Pageable pageable, Integer teamId, String fileName, Boolean isRecycleBin);
    Page<Files> findAllByOwner_IdAndFileNameContainingAndIsRecycleBin(Pageable pageable, String userId, String fileName, Boolean isRecycleBin);
}
