package com.univus.project.service;

import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.post.PostResDto;
import com.univus.project.dto.search.SearchResDto;
import com.univus.project.repository.CommentRepository;
import com.univus.project.repository.NoticeRepository;
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
public class SearchService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NoticeRepository noticeRepository;

    @Transactional(readOnly = true)
    public SearchResDto searchAll(Long teamId, String keyword) {

        List<PostResDto> posts = postRepository.searchPosts(teamId, keyword)
                .stream()
                .map(PostResDto::new)
                .collect(Collectors.toList());

        List<CommentResDto> comments = commentRepository.searchComments(teamId, keyword)
                .stream()
                .map(CommentResDto::new)
                .collect(Collectors.toList());

        List<NoticeResDto> notices = noticeRepository.searchNotices(teamId, keyword)
                .stream()
                .map(NoticeResDto::new)
                .collect(Collectors.toList());

        return new SearchResDto(posts, comments, notices);
    }
}