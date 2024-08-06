package University.Dormitory.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ObjectNode json = new ObjectMapper().createObjectNode();
        String path = request.getRequestURI();
        LOGGER.info("[path]: {}", path);
        // Swagger UI와 관련된 경로를 허용
        if (path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources/") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/swagger-ui.html") ||
                path.startsWith("/health") ||
                "/".equals(path) ||
                "/signin".equals(path) ||
                "/favicon.ico".equals(path)
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
                } else {
                    LOGGER.info("토큰 유효기간 만료");
                    json.put("Success", false);
                    json.put("Error", "INVALID_TOKEN");
                    json.put("Message", "토큰 유효기간이 지났습니다.");
                    String newResponse = new ObjectMapper().writeValueAsString(json);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.setContentLength(newResponse.length());
                    response.getOutputStream().write(newResponse.getBytes());
                }
            }
        } catch (AuthenticationException e) {
            LOGGER.info("허가되지 않은 권한이 접근하였습니다");
            json.put("Success", false);
            json.put("Error", "UNDEFINED_AUTHORITY");
            json.put("Message", "허가되지 않았습니다.");
            String newResponse = new ObjectMapper().writeValueAsString(json);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getOutputStream().write(newResponse.getBytes());
        } catch (RuntimeException e) {
            LOGGER.info("Filter-Chain에서 오류가 발생했습니다.");
            json.put("Success", false);
            json.put("Error", "UNKNOWN_ERROR");
            json.put("Message", "오류가 발생했습니다");
            String newResponse = new ObjectMapper().writeValueAsString(json);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getOutputStream().write(newResponse.getBytes());
        }
    }
}