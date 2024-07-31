package University.Dormitory.service.UserService;

import University.Dormitory.Converter.UserConverter;
import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.RefreshToken;
import University.Dormitory.domain.User;
import University.Dormitory.exception.Handler.PasswordNotMatchException;
import University.Dormitory.exception.Handler.UserAlreadyExistException;
import University.Dormitory.exception.Handler.UserNotFoundException;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.RefreshTokenRepository;
import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.security.JwtTokenProvider;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final CustomRepository customRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //    인터페이스에는 없지만, 쓰이는 함수
    private User findUserById(long userId) {
        return userRepository.findById((long) userId)
                .orElseThrow();
    }


    //    해당 함수 사용 시, authority는 converter 이용해서 넘겨줄 것.
    @Override
    public String updateAuthorityByUserId(long userId, Authority authority) {
        User user = findUserById(userId);
        user.setAuthority(authority);
        userRepository.save(user);
        return "회원 권한 수정 완료";
    }

    @Override
    public boolean checkUserIdDuplicate(long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean checkUserNameDuplicate(String name) {
        return userRepository.existsByName(name);
    }

    @Override
    public User SignUp(SignUpRequestDTO.SignUpDto user) {
        if (checkUserIdDuplicate(user.getUserId())) {
            log.info("[학번 중복 발생]. UserAlreadyExistException 예외 처리합니다.");
            throw new UserAlreadyExistException("이미 존재하는 학번입니다.");
        } else if (checkUserNameDuplicate(user.getName())) {
            throw new UserAlreadyExistException("이미 존재하는 이름입니다.");
        }
        User newUser = UserConverter.toUser(user); //유저 생성
//        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword())); -> 비밀번호 인코딩 X
//        newUser.setPassword(user.getName());
//        newUser.setName(user.getName());
        log.info("name: {}", newUser.getName());
        return userRepository.save(newUser);
    }

    @Override
    public Map<String, String> SignIn(Long userId, String pw) {
        String userName = "";
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userName = user.get().getName();
        } else {
            log.info("로그인에 실패했습니다. ");
            throw new UserNotFoundException("해당 학번 조교가 존재하지 않습니다..");
        }

        log.info("userId:{}", userId);
        log.info("전달받은 비밀번호:{}, 데이터베이스 비밀번호:{}", pw, customRepository.findPasswordByUserId(userId));
        if (!pw.equals(customRepository.findPasswordByUserId(userId))) {
            log.info("[패스워드 틀림] PasswordNotMatchException 예외처리합니다.");
            throw new PasswordNotMatchException("패스워드가 일치하지 않습니다.");
        }
        log.info("[패스워드 일치. 토큰 발급]");
        Authority authoritiesByUserId = customRepository.findAuthoritiesByUserId(userId);
        Dormitory dormitory = customRepository.findDormitoryByUserId(userId);

        // Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(userId), authoritiesByUserId, userName, dormitory);
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(userId), authoritiesByUserId);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String SignOut(long userId) {
        userRepository.deleteById(userId);
        return "조교가 나갔습니다..";
    }

    @Override
    public Optional<Long> findUserIdByName(String name) {
        return customRepository.findUserIdByName(name);
    }

    @Override
    public void updateRefreshToken(String token, long userId) {
        RefreshToken newToken = RefreshToken.builder()
                .refreshToken(token)
                .userId(userId)
                .build();
        log.info("[Token] DB에 Refresh 토큰 저장");
        refreshTokenRepository.save(newToken);
    }

    @Override
    public void deleteRefreshTokenByUserId(long userId) {
        log.info("{}의 refresh토큰을 삭제합니다.", userId);
        refreshTokenRepository.deleteById(userId);
    }
}