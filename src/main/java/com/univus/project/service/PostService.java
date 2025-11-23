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

    public Long createPost(PostReqDto dto, String fileUrl, User user) {
        try{
            Board board = boardRepository.findById(dto.getBoardId())
                    .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."));

            Post post = new Post();
            post.setTitle(dto.getTitle());
            post.setContent(dto.getContent());
            post.setBoard(board);
            post.setUser(user);
            post.setFileUrl(fileUrl);
            if (fileUrl != null && !fileUrl.isBlank()) {
                post.setFileUrl(fileUrl);
            }
            if (user == null || user.getId() == null) {
                throw new RuntimeException("로그인한 유저가 존재하지 않습니다.");
            }

            postRepository.save(post);
            return post.getId();
        } catch (Exception e){
            log.error("게시물 생성 실패", e);
            throw e;
        }
    }

    public Long updatePost(Long postId, PostReqDto dto, String fileUrl, User user) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

            // 권한 체크(본인만 수정하도록)
            if (!post.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("수정 권한이 없습니다.");
            }

            post.setTitle(dto.getTitle());
            post.setContent(dto.getContent());

            // Firebase 이미지 URL 새로 전달되면 업데이트
            if (fileUrl != null && !fileUrl.isBlank()) {
                post.setFileUrl(fileUrl);
            }
            if (user == null || user.getId() == null) {
                throw new RuntimeException("로그인한 유저가 존재하지 않습니다.");
            }

            postRepository.save(post);
            return post.getId();

        }  catch (Exception e){
            log.error("게시물 수정 실패", e);
            throw e;
        }
    }

    @Transactional
    public void deletePost(Long postId, User loginUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        if (!post.getUser().getId().equals(loginUser.getId())) {
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
    public List<PostResDto> getPostsByTitle(String title) {
        List<Post> posts = postRepository.findByTitleContaining(title);
        List<PostResDto> postResDtos =new ArrayList<>();
        for(Post post:posts){
            postResDtos.add(convertToDto(post));
        }
        return postResDtos;

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
        postResDto.setCreateTime(post.getCreateTime());
        return postResDto;

    }
    // 게시물 페이지네이션
    public Page<PostResDto> getPostList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return postRepository.findAll(pageable)
                .map(post -> convertToDto(post));   // ★ DTO로 매핑
    }



}