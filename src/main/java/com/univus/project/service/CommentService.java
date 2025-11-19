package com.univus.project.service;

import com.univus.project.dto.comment.CommentReqDto;
import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.dto.post.PostReqDto;
import com.univus.project.entity.Comment;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import com.univus.project.repository.CommentRepository;
import com.univus.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Long createComment(CommentReqDto dto, User writer) {

        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setPost(post);
        comment.setWriter(writer);

        commentRepository.save(comment);
        return comment.getId();
    }

    @Transactional(readOnly = true)
    public List<CommentResDto> getComments(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        return commentRepository.findByPost(post)
                .stream()
                .map(CommentResDto::new)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId, User loginUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (!comment.getWriter().getId().equals(loginUser.getId())) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    public Long updateComment(Long commentId, CommentReqDto dto, User user) {
        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

            // 권한 체크(본인만 수정하도록)
            if (!comment.getWriter().getId().equals(user.getId())) {
                throw new RuntimeException("수정 권한이 없습니다.");
            }

            comment.setContent(dto.getContent());

            if (user == null || user.getId() == null) {
                throw new RuntimeException("로그인한 유저가 존재하지 않습니다.");
            }

            commentRepository.save(comment);
            return comment.getId();

        }  catch (Exception e){
            log.error("댓글 수정 실패", e);
            throw e;
        }
    }
}
