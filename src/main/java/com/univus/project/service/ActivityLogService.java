package com.univus.project.service;

import com.univus.project.constant.Role;
import com.univus.project.dto.activityLog.ActivityLogResDto;
import com.univus.project.dto.activityLog.ActivityTop5Dto;
import com.univus.project.dto.activityLog.BoardUserContributionDto;
import com.univus.project.dto.activityLog.UserContributionDetailDto;
import com.univus.project.entity.*;
import com.univus.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
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

    private static final Pageable TOP5_PAGEABLE = PageRequest.of(0, 5);

    public ActivityLog recalcActivityLog(Long userId, Long boardId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

            ActivityLog log = activeLogRepository.findByUserAndBoard(user, board)
                    .orElseGet(() -> createNewLog(user, board));

            int postCount = postRepository.countByUserAndBoard(user, board);
            int commentCount = commentRepository.countByUserAndBoard(user, board);
            int reactionCount = reactionRepository.countByUserAndBoard(user, board);

            int todoDone = todoRepository.countByUserAndBoardAndDone(user, board, true);
            int todoNotDone = todoRepository.countByUserAndBoardAndDone(user, board, false);

            List<LocalDate> attendanceDates = attendanceRepository.findByUserAndBoard(user, board)
                    .stream()
                    .map(Attendance::getDate)
                    .toList();

            int total = (int) attendanceDates.stream().distinct().count();
            int streak = calcStreak(attendanceDates);
            int monthCount = calcMonth(attendanceDates);

            log.setPostCount(postCount);
            log.setCommentCount(commentCount);
            log.setReactionCount(reactionCount);

            log.setTodoCompleted(todoDone);
            log.setTodoUncompleted(todoNotDone);

            log.setAttendanceTotal(total);
            log.setAttendanceStreak(streak);
            log.setAttendanceThisMonth(monthCount);

            int score =
                    postCount       * 5 +   // 글 1개 = 5점
                            commentCount    * 2 +   // 댓글 1개 = 2점
                            reactionCount   * 1 +   // 공감 1개 = 1점
                            todoDone        * 4 +   // 투두 완료 1개 = 4점
                            total           * 2 +   // 출석 1일 = 2점
                            streak          * 1 +   // 연속 출석 1일 = 1점
                            monthCount      * 1;    // 이번 달 출석 1일 = 1점

            log.setContributionScore(score);
            log.setLastUpdated(LocalDateTime.now());

            return activeLogRepository.save(log);

        } catch (Exception e) {
            log.error("활동 로그 계산 실패(userId:{}, boardId: {}): {}", userId, boardId, e.getMessage());
            return null;
        }
    }

    // 활동로그가 없으면 새로운 활동로그 객체 생성
    private ActivityLog createNewLog(User user, Board board) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setBoard(board);
        return log;
    }

    public ActivityLog getUserLog(Long userId, Long boardId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

            return activeLogRepository.findByUserAndBoard(user, board)
                    .orElse(null);
        } catch (Exception e) {
            log.error("사용자 활동 로그 조회 실패(userId:{}, boardId:{}): {}", userId, boardId, e.getMessage());
            return null;
        }
    }

    /**
     *  인사이트용 - 특정 보드의 팀원별 기여도 리스트
     *    - 보드 내 모든 ActivityLog를 가져와서 BoardUserContributionDto로 변환
     *    - contributionScore 기준 내림차순 정렬
     *    - 교수(PROFESSOR)는 제외
     */
    public List<BoardUserContributionDto> getBoardUserContributions(Long boardId) {
        try {
            List<ActivityLog> logs = activeLogRepository.findByBoardId(boardId);

            return logs.stream()
                    .filter(log -> log.getUser().getRole() != Role.PROFESSOR)
                    .map(log -> new BoardUserContributionDto(
                            log.getUser().getId(),
                            log.getUser().getName(),
                            log.getUser().getImage(),
                            log.getContributionScore()
                    ))
                    .sorted(Comparator.comparingInt(BoardUserContributionDto::getContributionScore).reversed())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("보드별 팀원 기여도 조회 실패(boardId:{}): {}", boardId, e.getMessage());
            return List.of();
        }
    }

    public UserContributionDetailDto getUserContributionDetail(Long userId, Long boardId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다."));

            ActivityLog log = activeLogRepository.findByUserAndBoard(user, board)
                    .orElseThrow(() -> new RuntimeException("활동 로그가 없습니다."));

            return new UserContributionDetailDto(
                    user.getId(),
                    user.getName(),
                    user.getImage(),

                    log.getPostCount(),
                    log.getCommentCount(),
                    log.getReactionCount(),
                    log.getTodoCompleted(),
                    log.getTodoUncompleted(),

                    log.getAttendanceTotal(),
                    log.getAttendanceStreak(),
                    log.getAttendanceThisMonth(),

                    log.getContributionScore()
            );

        } catch (Exception e) {
            log.error("사용자 기여도 상세 조회 실패(userId:{}, boardId:{}): {}", userId, boardId, e.getMessage());
            return null;
        }
    }

    // 연속 출석일 계산
    public int calcStreak(List<LocalDate> dates) {
        try {
            if (dates == null || dates.isEmpty()) return 0;

            Set<LocalDate> set = new HashSet<>(dates);
            List<LocalDate> sorted = set.stream()
                    .sorted(Comparator.reverseOrder())
                    .toList();

            LocalDate today = LocalDate.now();
            LocalDate cursor = set.contains(today) ? today : sorted.get(0);

            int streak = 0;
            while (set.contains(cursor)) {
                streak++;
                cursor = cursor.minusDays(1);
            }
            return streak;
        } catch (Exception e) {
            log.error("연속 출석일 계산 실패: {}", e.getMessage());
            return 0;
        }
    }

    public int calcMonth(List<LocalDate> dates) {
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

    public ActivityLogResDto getActivityLogForUserAndBoard(User user, Long boardId) {
        try {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. boardId=" + boardId));

            ActivityLog log = recalcActivityLog(user.getId(), boardId);
            if (log == null) {
                log = activeLogRepository.findByUserAndBoard(user, board)
                        .orElseGet(() -> createNewLog(user, board));
            }

            return new ActivityLogResDto(log);

        } catch (Exception e) {
            log.error("활동 로그 DTO 조회 실패(userId:{}, boardId:{}): {}", user.getId(), boardId, e.getMessage());
            return null;
        }
    }

    public void checkIn(Long userId, Long boardId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userId=" + userId));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. boardId=" + boardId));

            LocalDate today = LocalDate.now();

            boolean exists = attendanceRepository
                    .findByUserAndBoardAndDate(user, board, today)
                    .isPresent();

            if (exists) {
                log.info("이미 오늘 출석한 사용자입니다. userId={}, boardId={}", userId, boardId);
                return;
            }

            Attendance attendance = new Attendance();
            attendance.setUser(user);
            attendance.setBoard(board);
            attendanceRepository.save(attendance);

            recalcActivityLog(userId, boardId);

        } catch (Exception e) {
            log.error("출석 체크 실패(userId:{}, boardId:{}): {}", userId, boardId, e.getMessage());
            throw e;
        }
    }

    public List<ActivityTop5Dto> getPostTop5(Long boardId) {
        try {
            return postRepository.findPostTop5ByBoardId(boardId, TOP5_PAGEABLE)
                    .stream()
                    .filter(dto -> dto.getRole() != Role.PROFESSOR)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("게시글 TOP5 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }

    public List<ActivityTop5Dto> getCommentTop5(Long boardId) {
        try {
            return commentRepository.findCommentTop5ByBoardId(boardId, TOP5_PAGEABLE)
                    .stream()
                    .filter(dto -> dto.getRole() != Role.PROFESSOR)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("댓글 TOP5 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }

    public List<ActivityTop5Dto> getReactionTop5(Long boardId) {
        try {
            return reactionRepository.findReactionTop5ByBoardId(boardId, TOP5_PAGEABLE)
                    .stream()
                    .filter(dto -> dto.getRole() != Role.PROFESSOR)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("리액션 TOP5 조회 실패: {}", e.getMessage());
            return List.of();
        }
    }
}
