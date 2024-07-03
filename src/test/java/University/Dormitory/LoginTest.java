package University.Dormitory;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.User;
import University.Dormitory.repository.JPARepository.UserAuthoritiesRepository;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@Transactional
public class LoginTest {
    @Autowired
    UserCommandService userCommandService;
    @Autowired
    UserAuthoritiesRepository userAuthoritiesRepository;


    @Test
    @DisplayName("유저 저장 잘 되는지 테스트")
    void test1() {
        // given
        SignUpRequestDTO.SignUpDto SignUpDto = new SignUpRequestDTO.SignUpDto();
        SignUpDto.setPassword("123");
        SignUpDto.setDormitory(Dormitory.BUILDING1);
        SignUpDto.setUserId(202035505);
        SignUpDto.setAuthority("사감");
        SignUpDto.setName("권하림");

        // when
        User savedUser = userCommandService.SignUp(SignUpDto);

        // then
        System.out.println(savedUser.getUserId());
        System.out.println(savedUser.getJoinDate());
        System.out.println(savedUser.getName());
        System.out.println(savedUser.getPassword());
        System.out.println(savedUser.getDormitory());
        System.out.println(userAuthoritiesRepository.findById((long) savedUser.getUserId()));
    }
}
