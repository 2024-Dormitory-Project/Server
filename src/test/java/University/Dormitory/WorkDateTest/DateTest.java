package University.Dormitory.WorkDateTest;

import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.WorkDateRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
public class DateTest {
    @Autowired
    private static WorkDateRepository workDateRepository;
    @Autowired
    private static CustomRepository customRepository;
//    @BeforeEach
//    void workDateInsert() {
//        WorkDate workDate = new WorkDate();
//
//    }
    @Test
    @DisplayName("근무자 저장 테스트")
    void saveUser() {

    }
}
