package University.Dormitory.repository;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.WorkDate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static University.Dormitory.domain.QWorkDate.workDate;
import static generated.University.Dormitory.domain.QUser.user;

@Slf4j
@Repository
public class CustomRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public CustomRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public String findPasswordByUserId(int userId) {
        return query.select(user.password).
                from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
    }

    public Authority findAuthoritiesByUserId(int userId) {
        log.info("[findAuthoritiesByUserId] : 권한 정보 찾기 실행. 토큰발행용");
        Authority authority = query
                .select(user.authority)
                .from(user)
                .where(user.userId.eq(userId))
                .fetchOne();
        return authority;
    }

    public List<Integer> findUserIdsByDate(LocalDate date) {
        List<Integer> userIdList = new ArrayList<>();

        return query.select(workDate.user.userId)
                .from(workDate)
                .where(Expressions.dateTemplate(LocalDate.class, "DATE_FORMAT({0}, {1})", workDate.actualLeaveTime, "%Y-%m-%d").eq(date))
                .fetch();
    }

    public long changeWorkDate(int userId1, int userId2, LocalDateTime userId1Date) {
        return query.update(workDate)
                .set(workDate.user.userId, userId2)
                .where(workDate.user.userId.eq(userId1).and(workDate.scheduledStartTime.eq(userId1Date)))
                .execute();
    }

    public long writeReason(int userID, String reason, LocalDateTime date) {
        return  query.update(workDate)
                .set(workDate.Reason, reason)
                .where(workDate.user.userId.eq(userID).and(workDate.scheduledStartTime.eq(date)))
                .execute();
    }


}
