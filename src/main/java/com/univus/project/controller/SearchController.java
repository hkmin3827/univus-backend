package com.univus.project.controller;

import com.univus.project.dto.search.SearchResDto;
import com.univus.project.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResDto> search(
            @RequestParam Long teamId,
            @RequestParam String keyword) {

        SearchResDto result = searchService.searchAll(teamId, keyword);
        return ResponseEntity.ok(result);
    }
}
