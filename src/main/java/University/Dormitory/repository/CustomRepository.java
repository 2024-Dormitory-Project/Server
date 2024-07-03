package University.Dormitory.repository;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.UserAuthorities;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

import static University.Dormitory.domain.QUser.user;
import static University.Dormitory.domain.QUserAuthorities.userAuthorities;


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
        List<UserAuthorities> authorities = query.select(userAuthorities)
                .from(user)
                .where(user.userId.eq(userId))
                .fetch();
        if(authorities.contains("PERFECT")) {
            return Authority.PERFECT;
        }
        else if(authorities.contains("SCHEDULE_ASSISTANT")) {
            return Authority.SCHEDULE_ASSISTANT;
        }
        else
            return Authority.ASSISTANT;
    }
}
