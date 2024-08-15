package University.Dormitory.repository.JPARepository;


import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);

    @Query("SELECT u.name FROM User u WHERE u.dormitory = :dormitory")
    List<String> findNamesByDormitory(@Param("dormitory") Dormitory dormitory);
}
