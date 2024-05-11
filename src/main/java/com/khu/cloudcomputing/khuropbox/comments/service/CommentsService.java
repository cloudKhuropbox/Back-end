package com.khu.cloudcomputing.khuropbox.comments.service;

import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsInfoDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsUpdateDTO;

import java.util.List;

public interface CommentsService{
    List<CommentsInfoDTO> findByFileId(Integer fileId);
    CommentsInfoDTO findById(Integer id);
    String findUserId(Integer id);
    Integer createComment(CommentsDTO comment);
    void updateComment(CommentsUpdateDTO commentsUpdateDTO);
    void deleteComment(Integer id);
}