package com.univus.project.service;

import com.univus.project.dto.post.*;
import com.univus.project.entity.Board;
import com.univus.project.entity.Post;
import com.univus.project.entity.User;
import com.univus.project.repository.BoardRepository;
import com.univus.project.repository.PostRepository;
import com.univus.project.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    private final ActivityLogService activityLogService;

    public Long createPost(Long boardId, PostReqDto dto, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."));

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setBoard(board);
        post.setUser(user);

        if (dto.getFileUrl() != null && !dto.getFileUrl().isBlank()) {
            post.setFileUrl(dto.getFileUrl());
        }
        if (dto.getFileName() != null && !dto.getFileName().isBlank()) {
            post.setFileName(dto.getFileName());
        }

        postRepository.save(post);

        // 🔥🔴 핵심: ActivityLog 업데이트 필수!!
        activityLogService.recalcActivityLog(user.getId(), boardId);

        return post.getId();
    }

    public Long updatePost(Long postId, PostReqDto dto, User user) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        if (dto.getFileUrl() != null && !dto.getFileUrl().isBlank()) {
            post.setFileUrl(dto.getFileUrl());
        }
        if (dto.getFileName() != null && !dto.getFileName().isBlank()) {
            post.setFileName(dto.getFileName());
        }

        return post.getId();
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostListDto> getPostsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."));

        return postRepository.findByBoard(board)
                .stream()
                .map(PostListDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PostListDto> getPosts(Long boardId, int page, int size, String keyword, String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));


        Page<Post> posts;

        if (keyword != null && !keyword.isBlank()) {
            posts = postRepository.findByBoardIdAndTitleContaining(boardId, keyword, pageable);
        } else {
            posts = postRepository.findByBoardId(boardId, pageable);
        }

        return posts.map(PostListDto::new);
    }

    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        return new PostDetailDto(post);
    }

    private PostResDto convertToDto(Post post){
        PostResDto postResDto = new PostResDto();   // 비어있는 객체 생성
        postResDto.setPostId(post.getId());
        postResDto.setName(post.getUser().getName());
        postResDto.setTitle(post.getTitle());
        postResDto.setContent(post.getContent());
        postResDto.setFileUrl(post.getFileUrl());
        postResDto.setFileName(post.getFileName());
        postResDto.setCreateTime(post.getCreateTime());
        return postResDto;

    }


}