package University.Dormitory.LogInTest;

import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.SignInDTO.SignInRequestDTO;
import University.Dormitory.web.dto.SignInDTO.SignInResponseDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
public class LoginTest {
    @Autowired
    UserCommandService userCommandService;

    SignUpRequestDTO.SignUpDto signUpDto = new SignUpRequestDTO.SignUpDto(); //회원가입용 객체
    SignInRequestDTO.SignInDto signInDto = new SignInRequestDTO.SignInDto();
    @BeforeEach
    void beforeEachSignUp() {
        signUpDto.setPassword("권하림");
        signUpDto.setDormitory(Dormitory.DORMITORY2);
        signUpDto.setUserId(202035505);
        signUpDto.setAuthority("사감");
        signUpDto.setName("권하림");
        userCommandService.SignUp(signUpDto);
    }

    @BeforeEach
    void beforeEachSignIn() {
        signInDto.setPassword("권하림");
        signInDto.setUserId(202035505);
    }


    @Test
    @DisplayName("로그인 결과 반환 테스트")
    void loginTokenTest() {
        SignInResponseDTO.SignInDto signInResult = userCommandService.SignIn(signInDto);
        log.info("[로그인 결과] 토큰 정보: {}", signInResult.getToken());
    }
}