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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final FileService fileService;

    public Long createPost(PostReqDto dto, MultipartFile image, User user) {
        try{
            Board board = boardRepository.findById(dto.getBoardId())
                    .orElseThrow(() -> new RuntimeException("게시판이 존재하지 않습니다."));

            Post post = new Post();
            post.setTitle(dto.getTitle());
            post.setContent(dto.getContent());
            post.setBoard(board);
            post.setUser(user);

            if (image != null && !image.isEmpty()) {
                String savedPath = fileService.saveImage(image);
                post.setImagePath(savedPath);
            }

            postRepository.save(post);
            return post.getId();
        } catch (Exception e){
            log.error("게시물 생성 실패 : {}", e.getMessage());
            return null;
        }
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
    public PostDetailDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        return new PostDetailDto(post);
    }


}