package com.khu.cloudcomputing.khuropbox.comments.service;

import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsInfoDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsUpdateDTO;
import com.khu.cloudcomputing.khuropbox.comments.entity.Comments;
import com.khu.cloudcomputing.khuropbox.comments.repository.CommentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentsServiceImpl implements CommentsService {
    private final CommentsRepository commentsRepository;
    @Override
    public List<CommentsInfoDTO> findByFileId(Integer fileId){
        List<Comments> list = commentsRepository.findByFileId(fileId);
        List<CommentsInfoDTO> listDTO = new ArrayList<>();
        for (Comments comments : list) {
            listDTO.add(new CommentsInfoDTO(comments));
        }
        return listDTO;
    }
    @Override
    public CommentsInfoDTO findById(Integer id){
        return new CommentsInfoDTO(commentsRepository.findById(id).orElseThrow());
    }
    @Override
    public String findUserId(Integer id){
        return commentsRepository.findById(id).orElseThrow().getUser().getId();
    }
    @Override
    public Integer createComment(CommentsDTO comment){
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdated(false);
        return commentsRepository.save(comment.toEntity()).getId();
    }
    @Override
    public void updateComment(CommentsUpdateDTO commentsUpdateDTO){
        Comments comments=this.commentsRepository.findById(commentsUpdateDTO.getId()).orElseThrow();
        comments.update(commentsUpdateDTO.getComment());
        this.commentsRepository.save(comments);
    }
    @Override
    public void deleteComment(Integer id){
        commentsRepository.deleteById(id);
    }
}
