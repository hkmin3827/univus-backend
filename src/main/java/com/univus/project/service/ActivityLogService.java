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
import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ActivityLogService {
    private final ActiveLogRepository activeLogRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
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
                    .orElseGet(() -> createNewLog(user, board));
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
            int streak = calcStreak(attendanceDates);
            int monthCount = calcMonth(attendanceDates);

            // 저장
            log.setPostCount(postCount);
            log.setCommentCount(commentCount);
            // log.setReactionCount(reactionCount); 마찬가지로 리액션 레포지토리 구현 시 확인 후 생성 예정

            log.setTodoCompleted(todoDone);
            log.setTodoUncompleted(todoNotDone);

            log.setAttendanceTotal(total);
            log.setAttendanceStreak(streak);
            log.setAttendanceThisMonth(monthCount);

            log.setLastUpdated(LocalDateTime.now());

            return activeLogRepository.save(log);

        } catch (Exception e) {
            log.error("활동 로그 계산 실패(userId:{}, boardId: {}): {}", userId, boardId, e.getMessage());
            return null;
        }
    }

    // 2) 활동로그가 없으면 새로운 활동로그 객체 생성
    private ActivityLog createNewLog(User user, Board board) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setBoard(board);
        return log;
    }

//    //3) 게시판 전체 로그 조회
//    private int calcDateStreak(List<LocalDate> list) {
//        try {
//            Board board = boardRepository.findById()
//                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));
//
//            return activeLogRepository.findByBoard(board);
//        } catch (Exception e) {
//            log.error("게시판 로그 조회 실패 (boardId: {}) : {}", boardId, e.getMessage());
//            return List.of();
//        }
//    } BoardRepository 생성 확인 후 맞춰서 다시 작성 예정 (일단 제 마음대로 작성했습니다.)

    // 4) 연속 출석일 계산
    private int calcStreak(List<LocalDate> dates) {
        try {
            if(dates == null || dates.isEmpty()) return 0;

            Set<LocalDate> set = new HashSet<>(dates);
            List<LocalDate> sorted = set.stream()
                    .sorted(Comparator.reverseOrder())
                    .toList();
            LocalDate today = LocalDate.now();
            LocalDate cursor = set.contains(today) ? today : sorted.get(0);

            int streak = 0;
            while (set.contains(cursor)) {
                streak ++;
                cursor = cursor.minusDays(1);
            }
            return streak;
        } catch (Exception e) {
            log.error("연속 출석일 계산 실패: {}", e.getMessage());
            return 0;
        }
    }

    // 5) 이번달 출석 획수
    private int calcMonth(List<LocalDate> dates) {
        try {
            if (dates == null || dates.isEmpty()) return 0;

            YearMonth now = YearMonth.now();
            return (int) dates.stream()
                    .filter(d -> YearMonth.from(d).equals(now))
                    .distinct()
                    .count();
        } catch (Exception e) {
            log.error("이번 달 출석 수 계산 실패: {}", e.getMessage());
            return 0;
        }
    }
}
