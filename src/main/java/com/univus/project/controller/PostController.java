package com.univus.project.controller;


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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {
    private final UserRepository userRepository;
    private final PostService postService;

//     게시글 생성 (첨부파일 포함) : 포스트맨 form data로 주고받기
    @PostMapping("/create")
    public ResponseEntity<Long> createPost(
            @ModelAttribute PostReqDto dto,
            @RequestParam(required = false) String fileUrl
    ) {
        User user = getLoggedInMember();  // 실제 구현 필요

        Long postId = postService.createPost(dto, fileUrl, user);
        return ResponseEntity.ok(postId);
    }

    private User getLoggedInMember() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
    }
    // 1L : 임시
    // getLoggedInMember() : 실제 로직으로 변경 필요



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
            @RequestParam(required = false) String fileUrl
    ) {
        User user = getLoggedInMember();  // 실제 구현 필요

        Long id = postService.updatePost(postId, dto, fileUrl, user);
        return ResponseEntity.ok(id);
    }


}