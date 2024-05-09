package com.khu.cloudcomputing.khuropbox.comments.controller;

import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsDTO;
import com.khu.cloudcomputing.khuropbox.comments.dto.CommentsUpdateDTO;
import com.khu.cloudcomputing.khuropbox.comments.service.CommentsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentsController {
    private final CommentsService commentsService;
    @GetMapping("comments/{fileId}")
    public List<CommentsDTO> Files(@PathVariable(value="fileId")Integer fileId){
        return commentsService.findByFileId(fileId);
    }
    @GetMapping("comment/{id}")
    public CommentsDTO FilesId(@PathVariable(value="id") Integer id){
        return commentsService.findById(id);
    }
    @PostMapping("create")
    public Integer Create(@RequestBody CommentsDTO comment){
        return commentsService.createComment(comment);
    }
    @PostMapping("delete/{id}")
    public void Delete(@PathVariable(value="id") Integer id){
        commentsService.deleteComment(id);
    }
    @PostMapping("update")
    public void Update(@RequestBody CommentsUpdateDTO commentsUpdate){
        commentsService.updateComment(commentsUpdate);
    }
}
