package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.UserAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthoritiesRepository extends JpaRepository<UserAuthorities, Long> {
    List<String> findUserAuthoritiesByUserId(Long userId);
}
