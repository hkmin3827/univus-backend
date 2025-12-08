package com.univus.project.service;

import com.univus.project.constant.NotificationType;
import com.univus.project.dto.comment.CommentReqDto;
import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.entity.Comment;
import com.univus.project.entity.Notification;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import com.univus.project.repository.CommentRepository;
import com.univus.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    // 🔥 추가: 활동 로그 서비스 주입
    private final ActivityLogService activityLogService;

    // ✅ 댓글 작성
    public Long createComment(CommentReqDto dto, User writer) {

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setBoard(post.getBoard());
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setWriter(writer);

        commentRepository.save(comment);

        // 🔥 댓글 생성 후 활동 로그 재계산
        try {
            Long boardId = post.getBoard().getId();
            activityLogService.recalcActivityLog(writer.getId(), boardId);
        } catch (Exception e) {
            log.error("댓글 작성 후 활동 로그 계산 실패 (userId:{}, postId:{}): {}",
                    writer.getId(), post.getId(), e.getMessage());
        }

        if (!writer.getId().equals(post.getUser().getId())) {    // 자기자신 제외
            Notification n = Notification.builder()
                    .userId(post.getUser().getId())               // 게시글 작성자
                    .teamId(post.getBoard().getTeam().getId())       // 팀 ID
                    .boardId(post.getBoard().getId())                // 게시판 ID
                    .postId(post.getId())
                    .type(NotificationType.COMMENT)
                    .message("'" + post.getTitle() + "' 리포트에 새로운 피드백이 달렸습니다.")
                    .createdAt(LocalDateTime.now())
                    .checked(false)
                    .build();

            notificationService.create(n);
        }
        return comment.getId();
    }

    @Transactional(readOnly = true)
    public Page<CommentResDto> getComments(Long postId, int page, int size, String keyword) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<Comment> comments;

        if (keyword != null && !keyword.isBlank()) {
            comments = commentRepository.findByPostIdAndContentContaining(postId, keyword, pageable);
        } else {
            comments = commentRepository.findByPostId(postId, pageable);
        }

        return comments.map(CommentResDto::new);
    }

    // ✅ 댓글 삭제
    // 전체 게시글에서 키워드 기반 댓글 검색
    @Transactional(readOnly = true)
    public Page<CommentResDto> searchAllComments(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Comment> comments = commentRepository.findByContentContaining(keyword, pageable);
        return comments.map(CommentResDto::new);
    }

    public void deleteComment(Long commentId, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        // 삭제 전에 boardId 뽑아두기
        Long boardId = null;
        try {
            boardId = comment.getPost().getBoard().getId();
        } catch (Exception e) {
            log.warn("댓글 삭제 시 보드 정보 조회 실패(commentId:{}): {}", commentId, e.getMessage());
        }

        commentRepository.delete(comment);

        // 🔥 댓글 삭제 후 활동 로그 재계산
        if (boardId != null) {
            try {
                activityLogService.recalcActivityLog(user.getId(), boardId);
            } catch (Exception e) {
                log.error("댓글 삭제 후 활동 로그 계산 실패 (userId:{}, boardId:{}): {}",
                        user.getId(), boardId, e.getMessage());
            }
        }
    }

    // ✅ 댓글 수정 (내용만 바뀌고 개수는 그대로라, 기여도 재계산은 안 해도 됨)
    public Long updateComment(Long commentId, CommentReqDto dto, User user) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());
        return comment.getId();
    }

    @Transactional(readOnly = true)
    public List<CommentResDto> getMyComments(Long teamId, Long userId) {
        List<Comment> comments = commentRepository.findByTeamAndUser(teamId, userId);
        return comments.stream().map(CommentResDto::new).collect(Collectors.toList());
    }
}
