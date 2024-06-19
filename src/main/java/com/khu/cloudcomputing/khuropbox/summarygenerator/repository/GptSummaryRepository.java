package com.khu.cloudcomputing.khuropbox.summarygenerator.repository;

import com.khu.cloudcomputing.khuropbox.summarygenerator.entity.GptSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GptSummaryRepository extends JpaRepository<GptSummaryEntity, Integer> {
}
