package University.Dormitory.repository;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.WorkDate;
import University.Dormitory.domain.WorkScheduleChange;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static University.Dormitory.domain.QUser.user;
import static University.Dormitory.domain.QWorkDate.workDate;
import static University.Dormitory.domain.QWorkScheduleChange.workScheduleChange;

@Slf4j
@Repository
public class CustomRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public CustomRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public String findPasswordByUserId(long userId) {
        return query.select(user.password).
                from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
    }

    public Authority findAuthoritiesByUserId(long userId) {
        log.info("[findAuthoritiesByUserId] : 권한 정보 찾기 실행. 토큰발행용");
        Authority authority = query
                .select(user.authority)
                .from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
        return authority;
    }

    //    안쓰는 것. 아마 특정일에 일하는 모든 근무자들 날짜에 의해서만 가져오는 쿼리문인듯
    public List<Long> findUserIdsByDate(LocalDate date) {
        List<Long> userIdList = new ArrayList<>();

        return query.select(workDate.user.userId)
                .from(workDate)
                .where(Expressions.dateTemplate(LocalDate.class, "DATE_FORMAT({0}, {1})", workDate.actualLeaveTime, "%Y-%m-%d").eq(date))
                .fetch();
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

    public Map<String, WorkTime> findDormitoryWorkersNameByDateAndDormitory(LocalDate date, Dormitory dormitory) {
        Map<String, WorkTime> dormitoryWorkers = new ConcurrentHashMap<>();

        List<Tuple> fetchWorkTimesAndNames = query
                .select(workDate.user.name, workDate.scheduledStartTime, workDate.scheduledLeaveTime)
                .from(workDate)
                .where(Expressions.dateTemplate(LocalDate.class, "DATE_FORMAT({0}, {1})", workDate.scheduledStartTime, "%Y-%m-%d").eq(date)
                        .and(workDate.user.dormitory.eq(dormitory)))
                .fetch();

        for (Tuple tuple : fetchWorkTimesAndNames) {
            String name = tuple.get(workDate.user.name);
            LocalDateTime scheduledStartTime = tuple.get(workDate.scheduledStartTime);
            LocalDateTime scheduledLeaveTime = tuple.get(workDate.scheduledLeaveTime);

            WorkTime workTime = new WorkTime(scheduledStartTime, scheduledLeaveTime);
            log.info("[이름] : {}, 시작시간:{}, 끝나는 시간:{}", name, scheduledStartTime, scheduledLeaveTime);
            dormitoryWorkers.put(name, workTime);
        }

        return dormitoryWorkers;
    }

    public int countWorkDays(long userId, int year, int month) {
        String monthString = String.format("%04d-%02d", year, month);

        List<WorkDate> list = query.selectFrom(workDate)
                .where(workDate.user.userId.eq(userId)
                        .and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, {1})", workDate.scheduledStartTime, "%Y-%m").eq(monthString)))
                .fetch();
        return list.size();
    }

    public List<Tuple> findWorksByUserIdAndDate(long userId, int year, int month) {
        String monthString = String.format("%04d-%02d", year, month);
        return query.select(workDate.scheduledStartTime, workDate.scheduledLeaveTime)
                .from(workDate)
                .where(workDate.user.userId.eq(userId)
                        .and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, {1})", workDate.scheduledStartTime, "%Y-%m").eq(monthString)))
                .fetch();
    }

    public int countWorkHours(long userId, int year, int month) {
        String monthString = String.format("%04d-%02d", year, month);

        List<WorkDate> list = query.selectFrom(workDate)
                .where(workDate.user.userId.eq(userId)
                        .and(Expressions.dateTemplate(String.class, "DATE_FORMAT({0}, '%Y-%m')", workDate.scheduledStartTime).eq(monthString)))
                .fetch();

        long workedHour = 0;
        for (WorkDate date : list) {
            Duration duration = Duration.between(date.getScheduledStartTime(), date.getScheduledLeaveTime());
            workedHour += duration.toHours();
        }

        return (int) workedHour;
    }

    public List<Tuple> findScheduleTimeByUsreId(long userId) {
        return query.select(workDate.scheduledStartTime, workDate.scheduledLeaveTime)
                .from(workDate)
                .where(workDate.user.userId.eq(userId))
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

    public List<WorkScheduleChange> getWorkChangeHistoryByAcceptorUserId(long userId, int year, int month) {
        return query
                .selectFrom(workScheduleChange)
                .where(workScheduleChange.acceptor.userId.eq(userId))
                .fetch();
    }

    @Getter
    public class WorkTime {
        private LocalDateTime startTime;
        private LocalDateTime leaveTime;

        WorkTime(LocalDateTime startTime, LocalDateTime leaveTime) {
            this.startTime = startTime;
            this.leaveTime = leaveTime;
        }
    }
}
