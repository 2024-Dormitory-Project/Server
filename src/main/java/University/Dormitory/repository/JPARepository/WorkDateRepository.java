package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.WorkDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WorkDateRepository extends JpaRepository<WorkDate, Long> {
    Optional<WorkDate> findByUserIdAndScheduledStartTime(Long userId, LocalDateTime scheduledStartTime);
    Optional<WorkDate> findByUserIdAndActualStartTime(Long userId, LocalDateTime scheduledStartTime);
}
