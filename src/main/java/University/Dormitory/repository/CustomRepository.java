package University.Dormitory.repository;

import University.Dormitory.domain.Enum.Authority;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
}
