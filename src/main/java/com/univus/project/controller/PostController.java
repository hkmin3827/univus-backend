package com.univus.project.controller;


import com.univus.project.config.CustomUserDetails;
import com.univus.project.dto.PageResponse;
import com.univus.project.dto.post.PostDetailDto;
import com.univus.project.dto.post.PostListDto;
import com.univus.project.dto.post.PostReqDto;
import com.univus.project.dto.post.PostResDto;
import com.univus.project.entity.User;
import com.univus.project.repository.PostRepository;
import com.univus.project.repository.UserRepository;
import com.univus.project.service.PostService;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team/{teamId}/board/{boardId}/post")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PostService postService;

    // 게시글 생성
    @PostMapping("/create")
    public ResponseEntity<Long> createPost(
            @PathVariable Long teamId,
            @PathVariable Long boardId,
            @RequestBody PostReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        Long postId = postService.createPost(teamId, boardId, dto, user);
        return ResponseEntity.ok(postId);
    }
    // 게시글 목록 + 검색 + 페이지네이션
    @GetMapping("/list")
    public ResponseEntity<PageResponse<PostListDto>> getPosts(
            @RequestParam Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        Page<PostListDto> posts = postService.getPosts(boardId, page, size, keyword, sort);
        return ResponseEntity.ok(PageResponse.from(posts));
    }

    // 게시글 상세 조회
    @GetMapping("/detail/{postId}")
    public ResponseEntity<PostDetailDto> getPostDetail(@PathVariable Long teamId, @PathVariable Long boardId, @PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(postService.getPostDetail(teamId, boardId, postId, userDetails.getUser().getId()));
    }


    // 게시글 수정
    @PutMapping("/update/{postId}")
    public ResponseEntity<Long> updatePost(
            @PathVariable Long teamId,
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody PostReqDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        Long id = postService.updatePost(teamId, boardId, postId, dto, user);
        return ResponseEntity.ok(id);
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long teamId,
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        postService.deletePost(teamId, boardId,postId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<PostListDto>> getMyPosts(
            @PathVariable Long teamId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<PostListDto> posts = postService.getMyPosts(teamId, userId);
        return ResponseEntity.ok(posts);
    }
}