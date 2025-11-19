package com.univus.project.service;

import com.univus.project.dto.notice.NoticeModifyDto;
import com.univus.project.dto.notice.NoticeResDto;
import com.univus.project.dto.notice.NoticeWriteDto;
import com.univus.project.entity.*;
import com.univus.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ActivityLogService {
    private final ActiveLogRepository activeLogRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
//    private final ReactionRepository reactionRepository;
    private final TodoRepository todoRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 1) 활동 로그 재계산 및 저장
    public ActivityLog recalcActivityLog(Long userId, Long boardId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

            ActivityLog log = activeLogRepository.findByUserAndBoard(user, board)
                    .orElseGet(() -> {
                        ActivityLog n = new ActivityLog();
                        n.setUser(user);
                        n.setBoard(board);
                        return n;
                    });
            // 게시글, 댓글, 공감 부분
            int postCount = postRepository.countByUserAndBoard(user, board);
            int commentCount = commentRepository.countByUserAndBoard(user, board);
            // int reactionCount = reactionRepository.countByTargeUserAndBoard(user, board); 리액션 레포지토리 구현 시 확인 후 생성 예정

            // todolist 부분
            int todoDone = todoRepository.countByUserAndBoardAndDone(user, board, true);
            int todoNotDone = todoRepository.countByUserAndBoardAndDone(user, board, false);

            // 출석 부분
            List<LocalDate> attendanceDates = attendanceRepository.findByUserAndBoard(user, board)
                    .stream()
                    .map(Attendance::getDate)
                    .toList();

            int total = attendanceDates.size();
//            int streak = calcStreak(attendanceDates);      // ⭐ 여기서 사용
//            int monthCount = calcMonth(attendanceDates);

            // 저장
            log.setPostCount(postCount);
            log.setCommentCount(commentCount);
            // log.setReactionCount(reactionCount); 마찬가지로 리액션 레포지토리 구현 시 확인 후 생성 예정

            log.setTodoCompleted(todoDone);
            log.setTodoUncompleted(todoNotDone);

            log.setAttendanceTotal(total);
//            log.setAttendanceStreak(streak);
//            log.setAttendanceThisMonth(monthCount);

            log.setLastUpdated(LocalDateTime.now());

            return activeLogRepository.save(log);

        } catch (Exception e) {
            log.error("활동 로그 계산 실패(userId:{}, boardId: {}): {}", userId, boardId, e.getMessage());
            return null;
        }
    }

    // 2) 활동로그가 없으면 새로운 활동로그 객체 생성
    private ActivityLog createnewLog(User user, Board board) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setBoard(board);
        return log;
    }



//    //3) 게시판 전체 로그 조회
//    private int calcDateStreak(List<LocalDate> list) {
//        try {
//            Board board = boardRepository.findById()
//                    .orElseThrow()
//        }
//    }
}
