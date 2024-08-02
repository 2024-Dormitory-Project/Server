package University.Dormitory.repository;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.User;
import University.Dormitory.domain.WorkDate;
import University.Dormitory.domain.WorkScheduleChange;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static University.Dormitory.domain.QPostUser.postUser;
import static University.Dormitory.domain.QUser.user;
import static University.Dormitory.domain.QWorkDate.workDate;
import static University.Dormitory.domain.QWorkScheduleChange.workScheduleChange;

@Slf4j
@Repository
public class CustomRepository {
    private final JPAQueryFactory query;

    public CustomRepository(EntityManager em) {
        this.query = new JPAQueryFactory(em);
    }

    public User findByUserId(long userId) {
        return query.selectFrom(user)
                .where(user.userId.eq(userId))
                .fetchOne();
    }

    public String findPasswordByUserId(long userId) {
        return query.select(user.password).
                from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
    }

    public Authority findAuthoritiesByUserId(long userId) {
        log.info("[findAuthoritiesByUserId] : 권한 정보 찾기 실행. 토큰발행용");
        return query
                .select(user.authority)
                .from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
    }

    public long changeWorkDate(long userId1, long userId2, LocalDateTime userId1Date) {
        return query.update(workDate)
                .set(workDate.user.userId, userId2)
                .where(workDate.user.userId.eq(userId1).and(workDate.scheduledStartTime.eq(userId1Date)))
                .execute();
    }

    public long writeReason(long userID, String reason, LocalDateTime date) {
        return query.update(workDate)
                .set(workDate.Reason, reason)
                .where(workDate.user.userId.eq(userID).and(workDate.scheduledStartTime.eq(date)))
                .execute();
    }

    public MultiValueMap<String, WorkTime> findDormitoryWorkersNameByDateAndDormitory(LocalDate date, Dormitory dormitory) {
        MultiValueMap<String, WorkTime> dormitoryWorkers = new LinkedMultiValueMap<>();

        List<Tuple> fetchWorkTimesAndNames = query
                .select(workDate.user.name, workDate.scheduledStartTime, workDate.scheduledLeaveTime)
                .from(workDate)
                .where(workDate.scheduledStartTime.year().eq(date.getYear())
                        .and(workDate.scheduledStartTime.month().eq(date.getMonthValue()))
                        .and(workDate.scheduledStartTime.dayOfMonth().eq(date.getDayOfMonth()))
                        .and(workDate.user.dormitory.eq(dormitory)))
                .fetch();

        for (Tuple tuple : fetchWorkTimesAndNames) {
            String name = tuple.get(workDate.user.name);
            LocalDateTime scheduledStartTime = tuple.get(workDate.scheduledStartTime);
            LocalDateTime scheduledLeaveTime = tuple.get(workDate.scheduledLeaveTime);

            WorkTime workTime = new WorkTime(scheduledStartTime, scheduledLeaveTime);
            log.info("[이름] : {}, 시작시간:{}, 끝나는 시간:{}", name, scheduledStartTime, scheduledLeaveTime);

            dormitoryWorkers.add(name, workTime);
        }

        return dormitoryWorkers;
    }


    //    public int countWorkDays(long userId, int year, int month) {
//        String monthString = String.format("%04d-%02d", year, month);
//
//        List<WorkDate> list = query.selectFrom(workDate)
//                .where(workDate.user.userId.eq(userId)
//                        .and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, {1})", workDate.scheduledStartTime, "%Y-%m").eq(monthString)))
//                .fetch();
//        return list.size();
//    }아래와 동일
    public int countWorkDays(long userId, int year, int month) {
        List<WorkDate> list = query.selectFrom(workDate)
                .where(workDate.user.userId.eq(userId)
                        .and(workDate.scheduledStartTime.year().eq(year))
                        .and(workDate.scheduledStartTime.month().eq(month)))
                .fetch();
        return list.size();
    }

    //    public int countWorkHours(long userId, int year, int month) {
//        String monthString = String.format("%04d-%02d", year, month);
//
//        List<WorkDate> list = query.selectFrom(workDate)
//                .where(workDate.user.userId.eq(userId)
//                        .and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, '%Y-%m')", workDate.scheduledStartTime).eq(monthString)))
//                .fetch();
//
//        long workedHour = 0;
//        for (WorkDate date : list) {
//            Duration duration = Duration.between(date.getScheduledStartTime(), date.getScheduledLeaveTime());
//            workedHour += duration.toHours();
//        }
//
//        return (int) workedHour;
//    }아래와 동일
    public int countWorkHours(long userId, int year, int month) {
        List<WorkDate> list = query.selectFrom(workDate)
                .where(workDate.user.userId.eq(userId)
                        .and(workDate.scheduledStartTime.year().eq(year))
                        .and(workDate.scheduledStartTime.month().eq(month)))
                .fetch();

        long workedHour = 0;
        for (WorkDate date : list) {
            Duration duration = Duration.between(date.getScheduledStartTime(), date.getScheduledLeaveTime());
            workedHour = workedHour + duration.toMinutes();
        }
        return (int) workedHour;
    }

    //    public List<Tuple> findScheduleTimeAndActualTimeByUserId(long userId, LocalDate date) {
//        return query.select(workDate.scheduledStartTime, workDate.actualStartTime, workDate.user.name)
//                .from(workDate)
//                .where(workDate.user.userId.eq(userId)
//                        .and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, '%Y-%m')", workDate.scheduledStartTime).eq(String.valueOf(date))))
//                .fetch();
//    }아래와동일
    public List<Tuple> findScheduleTimeAndActualTimeByUserId(long userId, LocalDate date) {
        return query.select(workDate.scheduledStartTime, workDate.actualStartTime, workDate.user.name)
                .from(workDate)
                .where(workDate.user.userId.eq(userId)
                        .and(workDate.scheduledStartTime.year().eq(date.getYear()))
                        .and(workDate.scheduledStartTime.month().eq(date.getMonthValue())))
                .fetch();
    }

    public Dormitory findDormitoryByUserId(long userId) {
        return query.select(user.dormitory)
                .from(user)
                .where(user.userId.eq((userId)))
                .fetchOne();
    }

    public List<LocalDate> getExistingWorkDates(long userId, int year, int month) {
        List<LocalDateTime> fetch = query
                .select(workDate.scheduledStartTime)
                .from(workDate)
                .where(workDate.user.userId.eq(userId)
                        .and(workDate.scheduledStartTime.year().eq(year))
                        .and(workDate.scheduledStartTime.month().eq(month)))
                .distinct()
                .fetch();
        List<LocalDate> exist = new ArrayList<>();
        for (LocalDateTime localDateTime : fetch) {
            LocalDate localDate = localDateTime.toLocalDate();
            exist.add(localDate);
        }
        return exist;
    }


    /**
     * 현재 사용자가 수락한 변경내역
     *
     * @param userId
     * @param date   : 연,월로만 할 꺼니까 일은 상관없음.
     * @return
     */
//    public List<WorkScheduleChange> getWorkChangeHistoryByAcceptorUserId(long userId, LocalDate date) {
//        return query
//                .selectFrom(workScheduleChange)
//                .where(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, '%Y-%m')", workDate.scheduledStartTime).eq(String.valueOf(date))
//                        .and(workScheduleChange.acceptor.userId.eq(userId)))
//                .fetch();
//    }
    public List<WorkScheduleChange> getWorkChangeHistoryByAcceptorUserId(long userId, LocalDate date) {
        return query
                .selectFrom(workScheduleChange)
                .where(workScheduleChange.acceptor.userId.eq(userId)
                        .and(workScheduleChange.beforeChangeDate.year().eq(date.getYear()))
                        .and(workScheduleChange.beforeChangeDate.month().eq(date.getMonthValue())))
                .fetch();
    }

    /**
     * 현재 사용자가 신청한 변경내역
     *
     * @param userId
     * @param date   : 연,월로만 할 꺼니까 일은 상관없음.
     * @return
     */
//    public List<WorkScheduleChange> getWorkChangeHistoryByApplicantUserId(long userId, LocalDate date) {
//        return query
//                .selectFrom(workScheduleChange)
//                .where(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, '%Y-%m')", workDate.scheduledStartTime).eq(String.valueOf(date))
//                        .and(workScheduleChange.applicant.userId.eq(userId)))
//                .fetch();
//    }
    public List<WorkScheduleChange> getWorkChangeHistoryByApplicantUserId(long userId, LocalDate date) {
        return query
                .selectFrom(workScheduleChange)
                .where(workScheduleChange.applicant.userId.eq(userId)
                        .and(workScheduleChange.beforeChangeDate.year().eq(date.getYear()))
                        .and(workScheduleChange.beforeChangeDate.month().eq(date.getMonthValue())))
                .fetch();
    }

    public Optional<Long> findUserIdByName(String name) {
        return Optional.ofNullable(query.select(user.userId)
                .from(user)
                .where(user.name.eq(name))
                .fetchOne());
    }


    public String deleteSchedule(LocalDate date, Dormitory dormitory) {
        query.delete(workDate)
                .where(
                        workDate.scheduledStartTime.year().eq(date.getYear())
                                .and(workDate.scheduledStartTime.month().eq(date.getMonthValue()))
                                .and(workDate.user.dormitory.eq(dormitory))
                )
                .execute();
        log.info("삭제 성공");
        return "해당 일 스케줄 삭제 성공";
    }


    public void updateActualStartTime(Long userId, LocalDateTime scheduledTime, LocalDateTime actualTime) {
        query.update(workDate)
                .where(workDate.scheduledStartTime.eq(scheduledTime)
                        .and(workDate.user.userId.eq(userId)))
                .set(workDate.actualStartTime, actualTime)
                .execute();
    }

    public boolean existsByUserIdAndscheduledStartTime(long userId, LocalDateTime scheduledStartTime) {
        List<WorkDate> results = query.selectFrom(workDate)
                .where(workDate.scheduledStartTime.eq(scheduledStartTime)
                        .and(workDate.user.userId.eq(userId)))
                .fetch();

        return !results.isEmpty();
    }

    public Map<Integer, List<String>> postworkes(LocalDate date) {
        List<Tuple> fetch = query.select(postUser.user.name, postUser.postWorkDate.dayOfMonth())
                .where(postUser.postWorkDate.year().eq(date.getYear())
                        .and(postUser.postWorkDate.month().eq(date.getMonthValue())))
                .fetch();
        Map<Integer, List<String>> workerList = new ConcurrentHashMap<>();
        for (Tuple tuple : fetch) {
            Integer day = tuple.get(postUser.postWorkDate.dayOfMonth());
            String name = tuple.get(postUser.user.name);

            // 날짜가 존재하지 않으면 새로운 리스트를 생성하고 추가
            workerList.computeIfAbsent(day, k -> new ArrayList<>()).add(name);
        }
        return workerList;
    }

    @Getter
    public static class WorkTime {
        private LocalDateTime startTime;
        private LocalDateTime leaveTime;

        WorkTime(LocalDateTime startTime, LocalDateTime leaveTime) {
            this.startTime = startTime;
            this.leaveTime = leaveTime;
        }
    }
}
