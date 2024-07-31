package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.PostUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface PostUserRepository extends JpaRepository<PostUser, Long> {
    @Query("SELECT p FROM PostUser p WHERE p.user.userId = :userId AND p.postWorkDate = :postWorkDate")
    Optional<PostUser> findByUserIdAndPostWorkDate(Long userId, LocalDate postWorkDate);

}
