package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.UserAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthoritiesRepository extends JpaRepository<UserAuthorities, Long> {
}
