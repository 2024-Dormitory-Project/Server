package University.Dormitory.repository.JPARepository;

import University.Dormitory.domain.WorkDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDateRepository extends JpaRepository<WorkDate, Long> {
}
