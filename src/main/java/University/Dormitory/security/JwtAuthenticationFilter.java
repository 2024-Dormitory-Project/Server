package University.Dormitory.security;

import University.Dormitory.domain.User;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.repository.JPARepository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        LOGGER.info("[path]: {}", path);
        // Swagger UI와 관련된 경로를 허용
        if (path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources/") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/swagger-ui.html") ||
                "/".equals(path) ||
                "/signin".equals(path) ||
                "/favicon.ico".equals(path)
//              Swagger 세팅을 위해 잠깐 풀어둠
                ||
                path.startsWith("/perfect")

        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtTokenProvider.resolveAccessToken(request);
        LOGGER.info("[doFilterInternal] accessToken 값 추출 완료. accessToken{} ", accessToken);

        LOGGER.info("[doFilterInternal] token 값 유효성 체크 시작");
        if (accessToken != null && jwtTokenProvider.validateAccessToken(accessToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOGGER.info("[doFilterInternal] access token 값 유효성 체크 완료");
            filterChain.doFilter(request, response);
            return;
        } else {
            // Access Token이 유효하지 않은 경우
            LOGGER.info("[doFilterInternal] access token 값 유효성 체크 실패");
            String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
            LOGGER.info("[doFilterInternal] refresh token 값 추출 완료. token{} ", refreshToken);

            if (refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                // Refresh Token이 유효한 경우
                LOGGER.info("[doFilterInternal] refresh token 값 유효성 체크 완료");
                long userIdFromRefreshToken = Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken));
                User user = userRepository.findById(userIdFromRefreshToken).orElseThrow(
                        () -> new UserNotFoundException("해당 ID를 찾지 못했습니다.")
                );
                String newAccessToken = jwtTokenProvider.createAccessToken(String.valueOf(userIdFromRefreshToken), user.getAuthority(), user.getName(), user.getDormitory());
                response.setHeader("Authorization", "Bearer " + newAccessToken);
                LOGGER.info("[doFilterInternal] new access token 발급 완료");
                filterChain.doFilter(request, response);
                return;
            } else {
                // Refresh Token도 유효하지 않은 경우
                LOGGER.info("[doFilterInternal] refresh token 값 유효성 체크 실패");
                throw new RuntimeException("유효값 실패");
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            }
        }
    }
}