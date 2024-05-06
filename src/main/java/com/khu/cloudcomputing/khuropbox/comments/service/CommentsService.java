package com.khu.cloudcomputing.khuropbox.comments.service;

import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsUpdateDTO;

import java.util.List;

public interface CommentsService{
    List<CommentsDTO> findByFileId(Integer fileId);
    CommentsDTO findById(Integer id);
    Integer createComment(CommentsDTO comment);
    void updateComment(CommentsUpdateDTO commentsUpdateDTO);
    void deleteComment(Integer id);
}