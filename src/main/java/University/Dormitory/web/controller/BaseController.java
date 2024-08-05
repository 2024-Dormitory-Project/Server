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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            description = "헤더의 Authorization 있는 건 Access Token이에요, Authorize할 떄는 Bearer 빼고 인증"
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
                .refreshToken("Bearer " + refreshToken)
                .accessToken("Bearer " + accessToken)
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
            description = "대충 테스트 해봤는데 이건 POSTMAN으로 했음. Swagger에 헤더 넣는거 오류떠서 "
    )
    public SignInResponseDTO.NewAccessToken newAccessToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
//        먼저 서버에 refreshToken이 존재하는지 확인
        boolean exist = refreshTokenRepository.existsByRefreshToken(refreshToken);
        if (!exist) {
//            존재하지 않으면 처리되지 않는 토큰으로 처리
            throw new RefreshTokenInvalidException("존재하지 않는 Refresh 토큰입니다. 새로 로그인해서 토큰 발급필요");
        }
//        만약 DB에 존재하면 그 refrshToken의 기간이 만료되었는지 확인
        boolean b = jwtTokenProvider.validateRefreshToken(refreshToken);
        if (b) {//만약 해당 refresh토큰의 기간이 만료되지 않았다면 RefreshToken을 바탕으로 새로 Access토큰 새로 발급
            log.info("새로운 access 토큰 발급");
            String userIdFromRefreshToken = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
            User byId = userRepository.findById(Long.valueOf(userIdFromRefreshToken)).orElseThrow(
                    () -> new UserNotFoundException("[Refresh 토큰 발급] : 해당 학번은 존재하지 않습니다")
            );
            return SignInResponseDTO.NewAccessToken.builder()
                    .isSuccess(true)
                    .accessToken(jwtTokenProvider.createAccessToken(
                            userIdFromRefreshToken, byId.getAuthority(), byId.getName(), byId.getDormitory()))
                    .build();
        } else {//Refresh토큰이 만료되었으므로 로그인해서 새로 Refresh 토큰을 발급받아야함
            throw new RefreshTokenInvalidException("Refresh 토큰의 기한이 만료되었습니다");
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "I'm healthy!";
    }
}
