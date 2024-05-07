package com.khu.cloudcomputing.khuropbox.files.repository;

import com.khu.cloudcomputing.khuropbox.files.entity.FileHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileHistoryRepository extends JpaRepository<FileHistoryEntity, Integer> {
    List<FileHistoryEntity> findAllByOrderByChangeDateDesc(Integer fileid);
}
