package com.khu.cloudcomputing.khuropbox.summurygenerator.repository;

import com.khu.cloudcomputing.khuropbox.summurygenerator.entity.GptSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GptSummaryRepository extends JpaRepository<GptSummaryEntity, Long> {
}
