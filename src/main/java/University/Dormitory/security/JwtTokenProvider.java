package University.Dormitory.security;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.exception.Handler.InvalidTokenException;
import University.Dormitory.repository.JPARepository.RefreshTokenRepository;
import io.jsonwebtoken.*;
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
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey = "secretKey";

    @PostConstruct
    protected void init() {
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    public String createAccessToken(String userId, Authority authority, String userName, Dormitory dormitory) {
        LOGGER.info("[CREATE ACCESS TOKEN] 토큰 생성 시작");
        LOGGER.info("[추출된 정보] 권한: {}, 이름: {}, 학번:{},기숙사: {}, 유효기간은 1시간으로 고정. 해당 정보로 토큰 생성합니다.", authority, userName, userId, dormitory);
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("Authority", authority);
        claims.put("dormitory", dormitory);
        claims.put("Name", userName);
        Date now = new Date();
        long tokenValidMillsecond = 1000L * 60 * 60; //1시간
        String token = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + tokenValidMillsecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        LOGGER.info("[createAccessToken] 토큰 생성 완료");
        return token;
    }

    public String createRefreshToken(String userId, Authority authority) {
        LOGGER.info("[CREATE REFRESH TOKEN] 토큰 생성 시작");
        LOGGER.info("UserId를 가지고 있는 REFRESH 토큰입니다.");
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("Authority", authority);
        Date now = new Date();
        long tokenValidMillsecond = 1000L * 60 * 60 * 24 * 90; //세달
        String token = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + tokenValidMillsecond))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        LOGGER.info("[createAccessToken] 토큰 생성 완료, DB에 저장");
        return token;
    }

    public Authentication getAuthenticationFromAccessToken(String token) {
        LOGGER.info("[getAuthenticationFromAccessToken] 토큰 인증 정보 조회 시작");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token =  token.substring(7);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserIdFromAcessToken(token));
        LOGGER.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {}", getUserIdFromAcessToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthenticationFromRefreshToken(String token) {
        LOGGER.info("[getAuthenticationFromRefreshToken] 토큰 인증 정보 조회 시작");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token =  token.substring(7);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserIdFromAcessToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserIdFromAcessToken(String token) {
        LOGGER.info("로그인 추출");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token =  token.substring(7);
        }
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        String authority = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("Authority", String.class);
        LOGGER.info("[getUserId] 토큰 기반 회원 구별 정보 추출 완료, ID:{}", info);
        LOGGER.info("[getUserId] 토큰 기반 회원 구별 정보 추출 완료, Authority:{}", authority);
        return info;
    }

    public String getUserNameFromAccessToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token =  token.substring(7);
        }
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return body.get("Name", String.class);
    }


    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Refresh-Token");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    public boolean validateAccessToken(String token) {
        LOGGER.info("[validateAccessToken] Access 토큰 유효 시작");
        try {
            if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
                token =  token.substring(7);
            }
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            LOGGER.info("[validateAccessToken] 토큰 유효 체크 예외 발생");
            return false;
        }
    }
    public String getUserIdFromRefreshToken(String refreshToken) {
        LOGGER.info("[GET USER ID FROM REFRESH TOKEN] 토큰에서 사용자 ID 추출 시작");

        try {
            if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
                refreshToken =  refreshToken.substring(7);
            }
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String userId = claims.getSubject();
            LOGGER.info("[GET USER ID FROM REFRESH TOKEN] 사용자 ID 추출 완료: {}", userId);
            return userId;
        } catch (JwtException e) {
            LOGGER.error("[GET USER ID FROM REFRESH TOKEN] 토큰 검증 실패: {}", e.getMessage());
            throw new InvalidTokenException("Invalid refresh token");
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        boolean result = true;
        try {
            if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
                refreshToken =  refreshToken.substring(7);
            }
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
            result = !claims.getBody().getExpiration().before(new Date());
            return result;
        } catch (Exception e) {
            LOGGER.info("[validateRefreshToken] refresh 토큰 유효 체크 예외 발생");
            return false;
        }
    }
}