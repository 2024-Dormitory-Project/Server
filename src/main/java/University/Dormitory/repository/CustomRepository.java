package University.Dormitory.repository;

import University.Dormitory.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.Querydsl;

import java.util.List;

public class CustomRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public CustomRepository(EntityManager em, JPAQueryFactory query) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }


}
