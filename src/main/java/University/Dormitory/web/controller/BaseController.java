package University.Dormitory.web.controller;

import University.Dormitory.domain.User;
import University.Dormitory.exception.Handler.RefreshTokenInvalidException;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.repository.JPARepository.RefreshTokenRepository;
import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.security.JwtTokenProvider;
import University.Dormitory.service.UserService.UserCommandService;
import University.Dormitory.web.dto.SignInDTO.SignInRequestDTO;
import University.Dormitory.web.dto.SignInDTO.SignInResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BaseController {
    private final UserCommandService userCommandService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String redirect() {
        return "redirect:/login/signIn";
    }

    @PostMapping("/signin")
    @Operation(
            summary = "로그인",
            description = "헤더의 Authorization 있는 건 Access Token이에요"
    )
    public ResponseEntity<SignInResponseDTO.SignInSuccess> SignIn(
            @RequestBody SignInRequestDTO.SignInDto signIn) {
        log.info("[PostMapping] [SignIn 실행]");
        log.info("로그인 시도 정보: id : {}, pw : {}", signIn.getUserId(), signIn.getPassword());
        Map<String, String> token = userCommandService.SignIn(signIn.getUserId(), signIn.getPassword());
        String accessToken = token.get("accessToken");
        String refreshToken = token.get("refreshToken");
        log.info("[로그인 성공] : accessToken: {}, refreshTokn: {}", accessToken, refreshToken);

        SignInResponseDTO.SignInSuccess response = SignInResponseDTO.SignInSuccess.builder()
                .message("로그인 성공")
                .isSuccess(true)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Refresh-Token", "Bearer " + refreshToken);

        userCommandService.updateRefreshToken(refreshToken, signIn.getUserId());

        return ResponseEntity.ok().headers(headers).body(response);
    }

    @GetMapping("/refresh")
    @Operation(
            summary = "Access 토큰 발급",
            description = "대충 구현은 했으나 필요없어진 API"
    )
    public SignInResponseDTO.NewAccessToken newAccessToken(@RequestParam("token") String token) {
        log.info("token : {}", token);

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            throw new RefreshTokenInvalidException("Refresh 토큰 값이 존재하지 않습니다.");
        }

        String refreshToken = token.substring(7);
        log.info("{}", refreshToken);
        boolean exist = refreshTokenRepository.existsByRefreshToken(refreshToken);
        if (!exist) {
            throw new RefreshTokenInvalidException("존재하지 않는 Refresh 토큰입니다. 새로 로그인해서 토큰 발급필요");
        }

        String userIdFromRefreshToken = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
        userCommandService.deleteRefreshTokenByUserId(Long.valueOf(userIdFromRefreshToken));

        User byId = userRepository.findById(Long.valueOf(userIdFromRefreshToken))
                .orElseThrow(() -> new UserNotFoundException("RefreshToken에 학번이 없습니다. 발급 불가."));

        log.info("새로운 access 토큰 발급");
        return SignInResponseDTO.NewAccessToken.builder()
                .isSuccess(true)
                .accessToken(jwtTokenProvider.createAccessToken(
                        String.valueOf(byId.getUserId()), byId.getAuthority(), byId.getName(), byId.getDormitory()))
                .build();
    }
}
