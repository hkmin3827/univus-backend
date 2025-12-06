package com.univus.project.service;

import com.univus.project.entity.*;
import com.univus.project.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock private ActiveLogRepository activeLogRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private ReactionRepository reactionRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private AttendanceRepository attendanceRepository;
    @Mock private UserRepository userRepository;
    @Mock private BoardRepository boardRepository;

    @InjectMocks
    private ActivityLogService activityLogService;

    private User user;
    private Board board;
    private ActivityLog log;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setName("홍길동");

        board = new Board();
        board.setId(99L);

        log = new ActivityLog();
        log.setUser(user);
        log.setBoard(board);
    }

    // ======================= recalcActivityLog ==========================
    @Test
    void recalcActivityLog_success() {
        Long userId = 1L;
        Long boardId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));

        when(activeLogRepository.findByUserAndBoard(user, board)).thenReturn(Optional.of(log));

        when(postRepository.countByUserAndBoard(user, board)).thenReturn(2);
        when(commentRepository.countByUserAndBoard(user, board)).thenReturn(5);
        when(reactionRepository.countByUserAndBoard(user, board)).thenReturn(3);
        when(todoRepository.countByUserAndBoardAndDone(user, board, true)).thenReturn(4);
        when(todoRepository.countByUserAndBoardAndDone(user, board, false)).thenReturn(1);
        Attendance a1 = new Attendance();
        a1.setDate(LocalDate.now());

        Attendance a2 = new Attendance();
        a2.setDate(LocalDate.now().minusDays(1));

        Attendance a3 = new Attendance();
        a3.setDate(LocalDate.now().minusDays(2));

        when(attendanceRepository.findByUserAndBoard(user, board))
                .thenReturn(List.of(a1, a2, a3));
        when(activeLogRepository.save(any(ActivityLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ActivityLog result = activityLogService.recalcActivityLog(userId, boardId);

        assertNotNull(result);
        assertEquals(2, result.getPostCount());
        assertEquals(5, result.getCommentCount());
        assertEquals(3, result.getReactionCount());
        assertEquals(4, result.getTodoCompleted());
        assertEquals(1, result.getTodoUncompleted());
        assertTrue(result.getContributionScore() > 0);

        verify(activeLogRepository, times(1)).save(any());
    }

    @Test
    void recalcActivityLog_userNotFound_throw() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ActivityLog result = activityLogService.recalcActivityLog(1L, 99L);

        assertNull(result);
    }

    // ========================= calcStreak ===============================
    @Test
    void calcStreak_success() {
        List<LocalDate> dates = List.of(
                LocalDate.now(),
                LocalDate.now().minusDays(1),
                LocalDate.now().minusDays(2),
                LocalDate.now().minusDays(5)
        );

        int streak = activityLogService.calcStreak(dates);

        assertEquals(3, streak);
    }

    @Test
    void calcStreak_empty_returnsZero() {
        assertEquals(0, activityLogService.calcStreak(Collections.emptyList()));
    }

    // ========================= calcMonth ================================
    @Test
    void calcMonth_success() {
        LocalDate now = LocalDate.now();
        List<LocalDate> dates = List.of(
                now,
                now.minusDays(2),
                now.minusMonths(1)
        );

        int monthCount = activityLogService.calcMonth(dates);

        assertEquals(2, monthCount);
    }

    // ====================== getBoardUserContributions ====================
    @Test
    void getBoardUserContributions_success() {
        ActivityLog log1 = new ActivityLog();
        log1.setUser(user);
        log1.setContributionScore(20);

        when(activeLogRepository.findByBoardId(99L)).thenReturn(List.of(log1));

        var result = activityLogService.getBoardUserContributions(99L);

        assertEquals(1, result.size());
        assertEquals(20, result.get(0).getContributionScore());
    }
}
