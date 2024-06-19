package com.khu.cloudcomputing.khuropbox.comments.repository;

import com.khu.cloudcomputing.khuropbox.comments.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {
    List<Comments> findByFileIdOrderByCreatedAt(Integer fileId);
    List<Comments> findByReplyIdOrderByCreatedAt(Integer replyId);
    Optional<Comments> findById(Integer id);
    void deleteById(Integer id);
}
