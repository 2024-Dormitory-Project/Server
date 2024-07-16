package University.Dormitory.security;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;
    @Value("${jwt.secret}")
    private String secretKey = "secretKey";

    @PostConstruct
    protected void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    public String createToken(String userId, Authority authority, String userName, Dormitory dormitory) {
        LOGGER.info("[CREATE TOKEN] 토큰 생성 시작");
        LOGGER.info("[추출된 정보] 권한: {}, 이름: {}, 학번:{},기숙사: {}, 유효기간은 1시간으로 고정. 해당 정보로 토큰 생성합니다.", authority, userName, userId, dormitory);
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("Authority", authority);
        claims.put("dormitory", dormitory);
        claims.put("Name", userName);
        Date now = new Date();
        long tokenValidMillsecond = 1000L * 60 * 60;
        String token = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + tokenValidMillsecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        LOGGER.info("[CreateToken] 토큰 생성 완료");
        return token;
    }

    public Authentication getAuthentication(String token) {
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserId(token));
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {}", getUserId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserId(String token) {
        LOGGER.info("로그인 추출");
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        LOGGER.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info:{}", info);
        return info;
    }


    public String resolveToken(HttpServletRequest request) {
        LOGGER.info("[resolveToken] 헤더에서 Token 값 추출");
        return request.getHeader("X-AUTH-TOKEN");
    }

    public boolean validateToken(String token) {
        LOGGER.info("[validateToken] 토큰 유효 시작");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());

        } catch (Exception e) {
            LOGGER.info("[validateToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }
}