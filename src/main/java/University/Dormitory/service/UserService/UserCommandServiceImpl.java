package University.Dormitory.service.UserService;

import University.Dormitory.Converter.UserConverter;
import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.User;
import University.Dormitory.exception.SignInFailException;
import University.Dormitory.exception.SignInFailedException;
import University.Dormitory.exception.UserAlreadyExistException;
import University.Dormitory.repository.CustomRepository;
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

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomRepository customRepository;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public boolean checkUserIdDuplicate(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public User SignUp(SignUpRequestDTO.SignUpDto user) {
        if(checkUserIdDuplicate((long) user.getUserId())) {
            log.info("[학번 중복 발생]. 예외 처리합니다.");
            throw new UserAlreadyExistException("유저가 이미 존재합니다");
        }
        User newUser = UserConverter.toUser(user); //유저 생성
        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(newUser); // User 엔티티 저장
        return savedUser;
    }

    @Override
    public SignInResponseDTO.SignInDto SignIn(SignInRequestDTO.SignInDto user) {
        int userId = user.getUserId();
        String userName = "";
        Optional<User> byId = userRepository.findById((long) userId);
        if (byId.isPresent()) {
            userName = byId.get().getName();
        } else {
            throw new SignInFailedException("로그인에 실패하였습니다.");
        }

        log.info("userId:{}", userId);
        log.info("전달받은 비밀번호:{}, 데이터베이스 비밀번호:{}", user.getPassword(), customRepository.findPasswordByUserId(userId));
        if (!bCryptPasswordEncoder.matches(user.getPassword(), customRepository.findPasswordByUserId(userId))) {
            log.info("[패스워드 틀림]");
            throw new SignInFailException("패스워드가 일치하지 않습니다.");
        }
        log.info("[패스워드 일치. 토큰 발급]");
        Authority authoritiesByUserId = customRepository.findAuthoritiesByUserId(userId);
        SignInResponseDTO.SignInDto result = SignInResponseDTO.SignInDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(userId), authoritiesByUserId,userName))
                .build();
        return result;
    }

    public String SignOut(int userId) {
        userRepository.deleteById((long) userId);
        return "조교가 나갔습니다..";
    }
}
