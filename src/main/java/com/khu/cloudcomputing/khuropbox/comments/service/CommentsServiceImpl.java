package com.khu.cloudcomputing.khuropbox.comments.service;

import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsUpdateDTO;
import com.khu.cloudcomputing.khuropbox.comments.entity.Comments;
import com.khu.cloudcomputing.khuropbox.comments.repository.CommentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private final CommentsRepository commentsRepository;
    @Override
    public List<CommentsDTO> findByFileId(Integer fileId){
        List<Comments> list = commentsRepository.findByFileId(fileId);
        List<CommentsDTO> listDTO = new ArrayList<>();
        for (Comments comments : list) {
            listDTO.add(new CommentsDTO(comments));
        }
        return listDTO;
    }
    @Override
    public CommentsDTO findById(Integer id){
        return new CommentsDTO(commentsRepository.findById(id).orElseThrow());
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
