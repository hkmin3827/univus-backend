package com.univus.project.service;

import com.univus.project.dto.comment.CommentResDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.post.PostResDto;
import com.univus.project.dto.search.SearchResDto;
import com.univus.project.entity.*;
import com.univus.project.repository.CommentRepository;
import com.univus.project.repository.NoticeRepository;
import com.univus.project.repository.PostRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private NoticeRepository noticeRepository;

    @InjectMocks
    private SearchService searchService;

    private Post post;
    private Comment comment;
    private Notice notice;
    private User user;
    private Board board;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(10L);
        user.setName("홍길동");

        Board board = new Board();
        board.setId(99L);

        post = new Post();
        post.setId(1L);
        post.setTitle("검색 테스트 글");
        post.setUser(user);
        post.setBoard(board);

        comment = new Comment();
        comment.setId(2L);
        comment.setContent("댓글 검색 테스트");
        comment.setWriter(user);
        comment.setPost(post);

        notice = new Notice();
        notice.setId(3L);
        notice.setTitle("공지 검색 테스트");
        notice.setUser(user);

    }

    // ======================= 전체 검색 ========================
    @Test
    void searchAll_success() {
        Long teamId = 100L;
        String keyword = "테스트";

        when(postRepository.searchPosts(teamId, keyword)).thenReturn(List.of(post));
        when(commentRepository.searchComments(teamId, keyword)).thenReturn(List.of(comment));
        when(noticeRepository.searchNotices(teamId, keyword)).thenReturn(List.of(notice));

        SearchResDto result = searchService.searchAll(teamId, keyword);

        assertNotNull(result);

        assertEquals(1, result.getPosts().size());
        assertEquals(PostResDto.class, result.getPosts().get(0).getClass());
        assertEquals(1L, result.getPosts().get(0).getPostId());

        assertEquals(1, result.getComments().size());
        assertEquals(CommentResDto.class, result.getComments().get(0).getClass());
        assertEquals(2L, result.getComments().get(0).getId());

        assertEquals(1, result.getNotices().size());
        assertEquals(NoticeResDto.class, result.getNotices().get(0).getClass());
        assertEquals(3L, result.getNotices().get(0).getId());

        verify(postRepository, times(1)).searchPosts(teamId, keyword);
        verify(commentRepository, times(1)).searchComments(teamId, keyword);
        verify(noticeRepository, times(1)).searchNotices(teamId, keyword);
    }

    @Test
    void searchAll_emptyResult() {
        when(postRepository.searchPosts(anyLong(), anyString())).thenReturn(List.of());
        when(commentRepository.searchComments(anyLong(), anyString())).thenReturn(List.of());
        when(noticeRepository.searchNotices(anyLong(), anyString())).thenReturn(List.of());

        SearchResDto result = searchService.searchAll(100L, "아무도 없음");

        assertEquals(0, result.getPosts().size());
        assertEquals(0, result.getComments().size());
        assertEquals(0, result.getNotices().size());
    }
}
