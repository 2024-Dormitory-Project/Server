package University.Dormitory.service.UserService;

import University.Dormitory.Converter.UserAuthorityConverter;
import University.Dormitory.Converter.UserConverter;
import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.User;
import University.Dormitory.domain.UserAuthorities;
import University.Dormitory.exception.SignInFailException;
import University.Dormitory.repository.CustomRepository;
import University.Dormitory.repository.JPARepository.UserAuthoritiesRepository;
import University.Dormitory.repository.JPARepository.UserRepository;
import University.Dormitory.security.JwtTokenProvider;
import University.Dormitory.web.dto.SignInDTO.SignInRequestDTO;
import University.Dormitory.web.dto.SignInDTO.SignInResponseDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final UserAuthoritiesRepository userAuthoritiesRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomRepository customRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public boolean checkUserIdDuplicate(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public User SignUp(SignUpRequestDTO.SignUpDto user) {

        User newUser = UserConverter.toUser(user); //유저 생성
        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(newUser); // User 엔티티 저장
        List<UserAuthorities> userAuthoritiesList = UserAuthorityConverter.touserAuthorities(user, savedUser);
        for (UserAuthorities userAuthorities : userAuthoritiesList) {
            // 각 UserAuthorities 객체를 데이터베이스에 저장
            userAuthoritiesRepository.save(userAuthorities);
        }
        return savedUser;
    }

    @Override
    public SignInResponseDTO.SignInDto SignIn(SignInRequestDTO.SignInDto user) {
        int userId = user.getUserId();
        if (!bCryptPasswordEncoder.matches(user.getPassword(), customRepository.findPasswordByUserId(userId))) {
            log.info("[패스워드 틀림]");
            throw new SignInFailException("패스워드가 일치하지 않습니다.");
        }
        log.info("[패스워드 일치. 토큰 발급]");
        Authority authoritiesByUserId = customRepository.findAuthoritiesByUserId(userId);
        SignInResponseDTO.SignInDto signInResultDto = SignInResponseDTO.SignInDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(userId), authoritiesByUserId))
                .build();
        return signInResultDto;
    }

    public String SignOut(int userId) {
        userRepository.deleteById((long) userId);
        return "조교가 나갔습니다..";
    }
}
