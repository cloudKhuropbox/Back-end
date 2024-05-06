package com.khu.cloudcomputing.khuropbox.comments.repository;

import com.khu.cloudcomputing.khuropbox.comments.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {
    List<Comments> findByFileId(Integer fileId);
    Optional<Comments> findById(Integer id);
    void deleteById(Integer id);
}
