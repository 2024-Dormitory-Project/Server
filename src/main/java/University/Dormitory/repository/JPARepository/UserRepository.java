package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
