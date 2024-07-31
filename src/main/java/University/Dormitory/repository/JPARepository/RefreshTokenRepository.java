package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByRefreshToken(String refreshToken);
}
