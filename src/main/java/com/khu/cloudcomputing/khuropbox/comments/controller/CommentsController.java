package com.khu.cloudcomputing.khuropbox.comments.controller;

import com.khu.cloudcomputing.khuropbox.apiPayload.ApiResponse;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.SuccessStatus;
import com.khu.cloudcomputing.khuropbox.auth.persistence.UserRepository;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsInfoDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsUpdateDTO;
import com.khu.cloudcomputing.khuropbox.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<ApiResponse<List<CommentsInfoDTO>>> Files(@PathVariable(value = "fileId") Integer fileId) {
        List<CommentsInfoDTO> comments = commentsService.findByFileId(fileId);
        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, comments));
    }

    @GetMapping("comment/{id}")
    public ResponseEntity<ApiResponse<CommentsInfoDTO>> Comment(@PathVariable(value = "id") Integer id) {
        CommentsInfoDTO comment = commentsService.findById(id);
        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, comment));
    }

    @GetMapping("reply/{replyId}")
    public ResponseEntity<ApiResponse<List<CommentsInfoDTO>>> ReplyInfo(@PathVariable(value = "replyId") Integer replyId) {
        List<CommentsInfoDTO> comments = commentsService.findByReplyId(replyId);
        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, comments));
    }

    @PostMapping("create")
    public ResponseEntity<ApiResponse<Integer>> Create(@RequestBody CommentsDTO comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        comment.setUser(userRepository.findAllById(userId).orElseThrow());
        comment.setFileId(comment.getFileId());
        Integer commentId = commentsService.createComment(comment);
        return ResponseEntity.status(SuccessStatus._COMMENT_CREATED.getHttpStatus())
                .body(new ApiResponse<>(SuccessStatus._COMMENT_CREATED, commentId));
    }

    @PostMapping("delete/{id}")
    public ResponseEntity<ApiResponse<String>> Delete(@PathVariable(value = "id") Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if (userId.equals(commentsService.findUserId(id))) {
            commentsService.deleteComment(id);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._COMMENT_DELETED));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }

    @PostMapping("update")
    public ResponseEntity<ApiResponse<String>> Update(@RequestBody CommentsUpdateDTO commentsUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        if (userId.equals(commentsService.findUserId(commentsUpdate.getId()))) {
            commentsService.updateComment(commentsUpdate);
            return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._COMMENT_UPDATED));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse<>(HttpStatus.FORBIDDEN, "Forbidden", null));
    }
}