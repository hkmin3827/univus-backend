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

    // 🔥 공통 TOP5 Pageable (0페이지, 5개)
    private static final Pageable TOP5_PAGEABLE = PageRequest.of(0, 5);

    /**
     * 1) 활동 로그 재계산 및 저장
     *  - (user, board) 기준으로 게시글/댓글/리액션/투두/출석 카운트 계산
     *  - 가중치를 적용한 contributionScore 계산 후 ActivityLog 저장
     */
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
            int reactionCount = reactionRepository.countByUserAndBoard(user, board);

            // todolist 부분
            int todoDone = todoRepository.countByUserAndBoardAndDone(user, board, true);
            int todoNotDone = todoRepository.countByUserAndBoardAndDone(user, board, false);

            // 출석 부분
            List<LocalDate> attendanceDates = attendanceRepository.findByUserAndBoard(user, board)
                    .stream()
                    .map(Attendance::getDate)
                    .toList();

            int total = (int) attendanceDates.stream().distinct().count();
            int streak = calcStreak(attendanceDates);
            int monthCount = calcMonth(attendanceDates);

            // 기본 카운트 저장
            log.setPostCount(postCount);
            log.setCommentCount(commentCount);
            log.setReactionCount(reactionCount);

            log.setTodoCompleted(todoDone);
            log.setTodoUncompleted(todoNotDone);

            log.setAttendanceTotal(total);
            log.setAttendanceStreak(streak);
            log.setAttendanceThisMonth(monthCount);

            // 🔥 기여도 점수 계산 (가중치는 팀에서 조정 가능)
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

    /**
     * 2) 활동로그가 없으면 새로운 활동로그 객체 생성
     */
    private ActivityLog createNewLog(User user, Board board) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setBoard(board);
        return log;
    }

    /**
     * 3) 특정 사용자 활동 로그 조회
     */
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
     * 4) 인사이트용 - 특정 보드의 팀원별 기여도 리스트
     *    - 보드 내 모든 ActivityLog를 가져와서 BoardUserContributionDto로 변환
     *    - contributionScore 기준 내림차순 정렬
     *    - 🔥 교수(PROFESSOR)는 제외
     */
    public List<BoardUserContributionDto> getBoardUserContributions(Long boardId) {
        try {
            // board 엔티티 안 거치고, 바로 board_id 기준으로 조회
            List<ActivityLog> logs = activeLogRepository.findByBoardId(boardId);

            return logs.stream()
                    .filter(log -> log.getUser().getRole() != Role.PROFESSOR) // 교수 제외
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

    /**
     * 5) 인사이트용 - 특정 사용자 상세 기여도 정보
     *    - 하나의 (user, board)에 대한 ActivityLog를 DTO로 변환
     *    - React 쪽에서 그래프/카드로 풀어 쓰기 좋게 구성
     */
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

    /**
     * 6) 연속 출석일 계산
     */
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

    /**
     * 7) 이번 달 출석 횟수
     */
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

    /**
     * React용: (로그인한) User + Board 기준으로 ActivityLog를 DTO로 반환
     *  - 출석 정보까지 포함해서 반환
     *  - 필요하면 항상 최신 상태가 되도록 재계산 호출
     */
    public ActivityLogResDto getActivityLogForUserAndBoard(User user, Long boardId) {
        try {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. boardId=" + boardId));

            // 항상 최신 데이터가 필요하다면 재계산 한 번 돌려주기
            ActivityLog log = recalcActivityLog(user.getId(), boardId);
            if (log == null) {
                // 재계산이 실패한 경우를 대비한 fallback
                log = activeLogRepository.findByUserAndBoard(user, board)
                        .orElseGet(() -> createNewLog(user, board));
            }

            return new ActivityLogResDto(log);

        } catch (Exception e) {
            log.error("활동 로그 DTO 조회 실패(userId:{}, boardId:{}): {}", user.getId(), boardId, e.getMessage());
            return null;
        }
    }

    /**
     * ✅ 오늘 해당 보드에 출석 체크
     *  - attendance 테이블에 기록 저장
     *  - 그 다음 활동 로그 재계산(recalcActivityLog)까지 한 번에 처리
     */
    public void checkIn(Long userId, Long boardId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userId=" + userId));

            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없습니다. boardId=" + boardId));

            LocalDate today = LocalDate.now();

            // 이미 오늘 출석했으면 중복 저장 X
            boolean exists = attendanceRepository
                    .findByUserAndBoardAndDate(user, board, today)
                    .isPresent();

            if (exists) {
                log.info("이미 오늘 출석한 사용자입니다. userId={}, boardId={}", userId, boardId);
                return;
            }

            // 출석 엔티티 저장 (@PrePersist로 date = today 자동 세팅)
            Attendance attendance = new Attendance();
            attendance.setUser(user);
            attendance.setBoard(board);
            attendanceRepository.save(attendance);

            // 출석까지 포함해서 활동로그 재계산
            recalcActivityLog(userId, boardId);

        } catch (Exception e) {
            log.error("출석 체크 실패(userId:{}, boardId:{}): {}", userId, boardId, e.getMessage());
            throw e;
        }
    }

    // 2) 게시글 TOP5 (교수 제외)
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

    // 3) 댓글 TOP5 (교수 제외)
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

    // 4) 리액션 TOP5 (교수 제외)
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
