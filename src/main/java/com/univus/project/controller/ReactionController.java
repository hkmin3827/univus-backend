package com.univus.project.controller;

import com.univus.project.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reaction")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    // 공감 토글

}
