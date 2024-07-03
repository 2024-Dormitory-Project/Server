package University.Dormitory.RepositoryTest;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Slf4j
@Transactional
public class CustomRepositoryTest {
    @Autowired
    CustomRepository customRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserCommandService userCommandService;

    @BeforeEach
    void beforeach() {
        SignUpRequestDTO.SignUpDto SignUpDto = new SignUpRequestDTO.SignUpDto();
        SignUpDto.setPassword("123");
        SignUpDto.setDormitory(Dormitory.BUILDING2);
        SignUpDto.setUserId(202035505);
        SignUpDto.setAuthority("사감");
        SignUpDto.setName("권하림");
        log.info("DTO 정보: [pw:1234, 권한:사감, 권하림, BUILDING2] 입력 완료");
        userCommandService.SignUp(SignUpDto);
    }

    @Test
    @DisplayName("유저아이디로 비밀번호 가져오기")
    public void getPasswordByUserId() {
        String passwordByUserId = customRepository.findPasswordByUserId(202035505);
        log.info("[사용자 학번으로부터 얻어온 암호화된 비밀번호] : {}", passwordByUserId);
    }

    @Test
    @DisplayName("권한 발행하기")
    public void findAuthority() {
        Authority authoritiesByUserId = customRepository.findAuthoritiesByUserId(202035505);
        log.info("[사용자 학번으로부터 얻어온 권한 정보]: {}", authoritiesByUserId);
    }


}