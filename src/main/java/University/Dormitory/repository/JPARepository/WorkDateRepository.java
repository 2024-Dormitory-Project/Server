package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.WorkDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WorkDateRepository extends JpaRepository<WorkDate, Long> {

    @Query("SELECT w FROM WorkDate w WHERE w.user.userId = :userId AND w.scheduledStartTime = :scheduledStartTime")
    Optional<WorkDate> findByUserIdAndScheduledStartTime(@Param("userId") Long userId, @Param("scheduledStartTime") LocalDateTime scheduledStartTime);

    @Query("SELECT w FROM WorkDate w WHERE w.user.userId = :userId AND w.actualStartTime = :actualStartTime")
    Optional<WorkDate> findByUserIdAndActualStartTime(@Param("userId") Long userId, @Param("actualStartTime") LocalDateTime actualStartTime);
}