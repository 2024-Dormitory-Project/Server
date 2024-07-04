package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.PostUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostUserRepository extends JpaRepository<PostUser, Long> {
}
