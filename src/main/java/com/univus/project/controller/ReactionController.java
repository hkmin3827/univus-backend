package com.univus.project.controller;

import com.univus.project.config.CustomUserDetails;
import com.univus.project.constant.ReactionType;
import com.univus.project.dto.reaction.ReactionResDto;
import com.univus.project.entity.User;
import com.univus.project.service.ReactionService;
import com.univus.project.dto.reaction.ReactionReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/reaction")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/{postId}/toggle")
    public ResponseEntity<ReactionType> toggleReaction(@PathVariable Long postId,
                                                       @RequestBody ReactionReqDto request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        ReactionType result = reactionService.toggleReaction(postId, user, request.getType());
        return ResponseEntity.ok(result); // null 이면 프론트에서 "반응 없음"으로 처리
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<ReactionResDto>> getReactions(@PathVariable Long postId,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<ReactionResDto> reactions = reactionService.getReactions(postId, user);
        return ResponseEntity.ok(reactions);
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Long> getReactionCount(@PathVariable Long postId) {
        long count = reactionService.getReactionCount(postId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{postId}/countbytype")
    public ResponseEntity<Long> getReactionCountByType(@PathVariable Long postId,
                                                       @RequestParam("type") ReactionType type) {
        long count = reactionService.getReactionCountByType(postId, type);
        return ResponseEntity.ok(count);
    }
}