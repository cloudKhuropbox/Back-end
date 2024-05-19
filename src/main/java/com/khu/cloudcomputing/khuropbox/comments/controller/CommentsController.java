package com.khu.cloudcomputing.khuropbox.comments.controller;

import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsInfoDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsUpdateDTO;
import com.khu.cloudcomputing.khuropbox.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentsController {
    private final UserRepository userRepository;
    private final CommentsService commentsService;
    @GetMapping("comments/{fileId}")
    public List<CommentsInfoDTO> Files(@PathVariable(value="fileId")Integer fileId){
        return commentsService.findByFileId(fileId);
    }
    @GetMapping("comment/{id}")//쓰지 않을 것으로 보임
    public CommentsInfoDTO Comment(@PathVariable(value="id") Integer id){
        return commentsService.findById(id);
    }
    @GetMapping("reply/{replyId}")
    public List<CommentsInfoDTO> ReplyInfo(@PathVariable(value="replyId")Integer replyId){
        return commentsService.findByReplyId(replyId);
    }
    @PostMapping("create")
    public Integer Create(@RequestBody CommentsDTO comment){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userId=authentication.getName();
        comment.setUser(userRepository.findAllById(userId).orElseThrow());
        return commentsService.createComment(comment);
    }
    @PostMapping("delete/{id}")
    public ResponseEntity<?> Delete(@PathVariable(value="id") Integer id){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String userId=authentication.getName();
        if(userId.equals(commentsService.findUserId(id))){
            commentsService.deleteComment(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @PostMapping("update")
    public ResponseEntity<?> Update(@RequestBody CommentsUpdateDTO commentsUpdate){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String userId=authentication.getName();
        if(userId.equals(commentsService.findUserId(commentsUpdate.getId()))){
            commentsService.updateComment(commentsUpdate);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
