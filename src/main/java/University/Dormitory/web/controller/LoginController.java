package University.Dormitory.web.controller;

import University.Dormitory.apiPayLoad.ApiResponse;
import University.Dormitory.domain.User;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.SignInDTO.SignInRequestDTO;
import University.Dormitory.web.dto.SignInDTO.SignInResponseDTO;
import University.Dormitory.web.dto.SignOutDTO.SignOutRequestDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/login")
public class LoginController {

    private final UserCommandService userCommandService;

    @PostMapping("/SignIn")
    public ApiResponse<SignInResponseDTO.SignInDto> SignIn(@RequestBody @Valid SignInRequestDTO.SignInDto request) {
        log.info("[PostMapping] [SignIn 실행]");
        log.info("로그인 시도 정보: id : {}, pw : {}", request.getUserId(), request.getPassword());
        SignInResponseDTO.SignInDto signInDto = userCommandService.SignIn(request);
        log.info("로그인 성공. SignInResponseDTO.SignInDTO 객체 반환");
        return ApiResponse.onSuccess(signInDto);
    }

    @GetMapping("/")
    public String home() {
        log.info("[GetMapping] [로그인 페이지로 리다이렉트]");
        return "redirect:/login/SignIn";
    }

    @GetMapping("/SignIn")
    public String SignIn() {
        return "login";
    }

    @PostMapping("/SignUp")
    public ApiResponse<SignUpResponseDTO.SignUpDto> SignUp(@RequestBody @Valid SignUpRequestDTO.SignUpDto request) {
        log.info("[PostMapping] [SignUp 실행]");
        log.info("회원가입 시도, 권한정보:{}, 기숙사:{}, 이름:{}, 학번:{}, 비밀번호:{}", request.getAuthority(), request.getDormitory(), request.getName(),
                request.getUserId(), request.getPassword());
        User user = userCommandService.SignUp(request);
        SignUpResponseDTO.SignUpDto result = SignUpResponseDTO.SignUpDto.builder()
                .name(user.getName())
                .userId(user.getUserId())
                .date(LocalDate.now())
                .build();
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/SignOut")
    public ApiResponse<String> SignOut(@RequestBody @Valid SignOutRequestDTO.SignOutDTO request) {
        log.info("[회원탈퇴 시도], 탈퇴하려는 학번:{}, 탈퇴하려는 이름:{}", request.getUserId(), request.getName());
        userCommandService.SignOut(request.getUserId());
        return ApiResponse.onSuccess("오늘도 하나의 조교가 나갔습니다...");
    }
}
