package University.Dormitory.security;

import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.web.dto.MainPageDTO.MainResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

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
        try {
            String accessToken = jwtTokenProvider.resolveAccessToken(request);
            LOGGER.info("[doFilterInternal] accessToken 값 추출 완료. accessToken : {} ", accessToken);

            LOGGER.info("[doFilterInternal] token 값 유효성 체크 시작");
            if (accessToken != null && jwtTokenProvider.validateAccessToken(accessToken)) {
                Authentication authentication = jwtTokenProvider.getAuthenticationFromAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOGGER.info("[doFilterInternal] access token 값 유효성 체크 완료");
                filterChain.doFilter(request, response);
                return;
            } else {
                // Access Token이 유효하지 않은 경우
                String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
//           만약 /refresh로 접근한 경우 refresh토큰에 있는 ROlE 검증
                if (path.startsWith("/refresh") && jwtTokenProvider.validateRefreshToken(refreshToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthenticationFromRefreshToken(refreshToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    LOGGER.info("Refresh토큰 권한 검증");
                    filterChain.doFilter(request, response);
                    return;
                } else {//수정필요 아직 json으로 return이 안됨
                    LOGGER.info("토큰 유효기간 만료");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    MainResponseDTO.ServeletResponse error = new MainResponseDTO.ServeletResponse();
                    error.setIsSuccess(false);
                    error.setMessage("INVALID_TOKEN");
                    String result = objectMapper.writeValueAsString(error);
                    response.getWriter().write(result);
                    response.getWriter().flush();
                }
            }
        } catch (AuthenticationException e) {
            LOGGER.info("허가되지 않은 권한이 접근하였습니다");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            MainResponseDTO.ServeletResponse error = new MainResponseDTO.ServeletResponse();
            error.setIsSuccess(false);
            error.setMessage("허가되지 않은 접근입니다");
            String result = objectMapper.writeValueAsString(error);
            response.getWriter().write(result);
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            MainResponseDTO.ServeletResponse error = new MainResponseDTO.ServeletResponse();
            error.setIsSuccess(false);
            error.setMessage("잘못된 요청입니다");
            String result = objectMapper.writeValueAsString(error);
            response.getWriter().write(result);
        }

    }
}