package University.Dormitory.web.controller;

import University.Dormitory.domain.User;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.SignInDTO.SignInResponseDTO;
import University.Dormitory.web.dto.SignOutDTO.SignOutRequestDTO;
import University.Dormitory.web.dto.SignOutDTO.SignOutResponseDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/login")
public class LoginController {

    private final UserCommandService userCommandService;

    @PostMapping("/signin")
    public SignInResponseDTO.SignInSuccess SignIn(HttpServletRequest request,
                                                  @JsonProperty("ID") Long userId, @JsonProperty("PW") String pw) {
        log.info("[PostMapping] [SignIn 실행]");
        log.info("로그인 시도 정보: id : {}, pw : {}", userId, pw);
        String accessToken = userCommandService.SignIn(userId, pw);
        log.info("로그인 성공. AccessToken 반환");
        return SignInResponseDTO.SignInSuccess.builder()
                .token("Bearer " + accessToken)
                .message("로그인 성공")
                .isSuccess(true)
                .build();
    }

//    @GetMapping("/refresh")
//    public SignInResponseDTO.SignInSuccess RefreshToken() {
//
//    }

    @GetMapping("/")
    public String home() {
        log.info("[GetMapping] [로그인 페이지로 리다이렉트]");
        return "redirect:/login/signin";
    }

    @GetMapping("/signin")
    public String SignIn() {
        return "login";
    }

    @PostMapping("/perfect/signup")
    public SignUpResponseDTO.SignUpDto SignUp(@RequestBody @Valid SignUpRequestDTO.SignUpDto request) {
        log.info("[PostMapping] [SignUp 실행]");
        log.info("회원가입 시도, 권한정보:{}, 기숙사:{}, 이름:{}, 학번:{}", request.getAuthority(), request.getDormitory(), request.getName(),
                request.getUserId());
        User user = userCommandService.SignUp(request);
        return SignUpResponseDTO.SignUpDto.builder()
                .isSuccess(true)
                .message("회원 정보 저장 성공").
                build();
    }

    @PostMapping("/perfect/signout")
    public SignOutResponseDTO.SignOutDTO SignOut(@RequestBody @Valid SignOutRequestDTO.SignOutDTO request) {
        log.info("[회원탈퇴 시도], 탈퇴하려는 학번:{}, 탈퇴하려는 이름:{}", request.getUserId(), request.getName());
        userCommandService.SignOut(request.getUserId());
        return SignOutResponseDTO.SignOutDTO
                .builder()
                .isSuccess(true)
                .message("조교가 사라졌습니다")
                .build();
    }
}