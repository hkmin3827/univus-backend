package com.univus.project.controller;


import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.post.PostDetailDto;
import com.univus.project.dto.post.PostListDto;
import com.univus.project.dto.post.PostReqDto;
import com.univus.project.dto.post.PostResDto;
import com.univus.project.entity.User;
import com.univus.project.repository.UserRepository;
import com.univus.project.service.PostService;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PostService postService;

//     게시글 생성 (첨부파일 포함) : 포스트맨 form data로 주고받기
    @PostMapping("/create")
    public ResponseEntity<Long> createPost(
            @ModelAttribute PostReqDto dto,
            @RequestParam(required = false) String fileUrl,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        User user = userDetails.getUser();

        Long postId = postService.createPost(dto, fileUrl, user);
        return ResponseEntity.ok(postId);
    }

    // 특정 게시판의 게시글 목록
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<PostListDto>> getPostsByBoard(@PathVariable Long boardId) {
        List<PostListDto> list = postService.getPostsByBoard(boardId);
        return ResponseEntity.ok(list);
    }
    // 특정 게시글 조회
    @GetMapping("/search")
    public ResponseEntity<List<PostResDto>> getPostsByTitle(@RequestParam String title){
        List<PostResDto> dtos = postService.getPostsByTitle(title);
        return ResponseEntity.ok(dtos);
    }

    // 게시글 상세 조회
    @GetMapping("/detail/{postId}")
    public ResponseEntity<PostDetailDto> getPostDetail(@PathVariable Long postId) {
        PostDetailDto dto = postService.getPostDetail(postId);
        return ResponseEntity.ok(dto);
    }

    // 게시글 수정
    @PutMapping("/update/{postId}")
    public ResponseEntity<Long> updatePost(
            @PathVariable Long postId,
            @ModelAttribute PostReqDto dto,
            @RequestParam(required = false) String fileUrl,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();

        Long id = postService.updatePost(postId, dto, fileUrl, user);
        return ResponseEntity.ok(id);
    }



    // 게시글 삭제
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        postService.deletePost(postId, user);
        return ResponseEntity.ok().build();
    }

}