package com.khu.cloudcomputing.khuropbox.comments.service;

import com.khu.cloudcomputing.khuropbox.apiPayload.GeneralException;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.ErrorStatus;
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
    /**
     * 파일 ID로 댓글 목록을 조회합니다.
     *
     * @param fileId 파일 ID
     * @return 댓글 정보 DTO 목록
     */
    @Override
    public List<CommentsInfoDTO> findByFileId(Integer fileId) {
        List<Comments> list = commentsRepository.findByFileIdOrderByCreatedAt(fileId);
        List<CommentsInfoDTO> listDTO = new ArrayList<>();
        for (Comments comments : list) {
            listDTO.add(new CommentsInfoDTO(comments));
        }
        return listDTO;
    }

    /**
     * 답글 ID로 댓글 목록을 조회합니다.
     *
     * @param replyId 답글 ID
     * @return 댓글 정보 DTO 목록
     */
    @Override
    public List<CommentsInfoDTO> findByReplyId(Integer replyId) {
        List<Comments> list = commentsRepository.findByReplyIdOrderByCreatedAt(replyId);
        List<CommentsInfoDTO> listDTO = new ArrayList<>();
        for (Comments comments : list) {
            listDTO.add(new CommentsInfoDTO(comments));
        }
        return listDTO;
    }

    /**
     * 댓글 ID로 댓글을 조회합니다.
     *
     * @param id 댓글 ID
     * @return 댓글 정보 DTO
     * @throws GeneralException 댓글을 찾을 수 없는 경우
     */
    @Override
    public CommentsInfoDTO findById(Integer id) {
        return new CommentsInfoDTO(commentsRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMENT_NOT_FOUND.getCode(), "Comment not found", ErrorStatus._COMMENT_NOT_FOUND.getHttpStatus())));
    }

    /**
     * 댓글 ID로 사용자 ID를 조회합니다.
     *
     * @param id 댓글 ID
     * @return 사용자 ID
     * @throws GeneralException 댓글을 찾을 수 없는 경우
     */
    @Override
    public String findUserId(Integer id) {
        return commentsRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMENT_NOT_FOUND.getCode(), "Comment not found", ErrorStatus._COMMENT_NOT_FOUND.getHttpStatus()))
                .getUser().getId();
    }

    /**
     * 새로운 댓글을 생성합니다.
     *
     * @param comment 댓글 DTO
     * @return 생성된 댓글의 ID
     * @throws GeneralException 댓글 생성 실패 시
     */
    @Override
    public Integer createComment(CommentsDTO comment) {
        try {
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdated(false);
            return commentsRepository.save(comment.toEntity()).getId();
        } catch (Exception e) {
            log.error("Failed to create comment", e);
            throw new GeneralException(ErrorStatus._COMMENT_CREATION_FAILED.getCode(), "Failed to create comment", ErrorStatus._COMMENT_CREATION_FAILED.getHttpStatus());
        }
    }

    /**
     * 댓글을 업데이트합니다.
     *
     * @param commentsUpdateDTO 댓글 업데이트 DTO
     * @throws GeneralException 업데이트할 댓글을 찾을 수 없는 경우
     */
    @Override
    public void updateComment(CommentsUpdateDTO commentsUpdateDTO) {
        Comments comments = this.commentsRepository.findById(commentsUpdateDTO.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMENT_NOT_FOUND.getCode(), "Comment not found", ErrorStatus._COMMENT_NOT_FOUND.getHttpStatus()));
        comments.update(commentsUpdateDTO.getComment());
        this.commentsRepository.save(comments);
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param id 댓글 ID
     * @throws GeneralException 삭제할 댓글이 존재하지 않는 경우
     */
    @Override
    public void deleteComment(Integer id) {
        if (!commentsRepository.existsById(id)) {
            throw new GeneralException(ErrorStatus._COMMENT_NOT_FOUND.getCode(), "Comment not found", ErrorStatus._COMMENT_NOT_FOUND.getHttpStatus());
        }
        commentsRepository.deleteById(id);
    }
}
