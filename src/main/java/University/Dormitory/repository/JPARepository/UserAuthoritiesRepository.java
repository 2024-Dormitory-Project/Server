package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.UserAuthorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserAuthoritiesRepository extends JpaRepository<UserAuthorities, Long> {
    @Query("SELECT ua FROM UserAuthorities ua WHERE ua.user.userId = :userId")
    UserDetails getByUserId(@Param("userId") int userId);
}
