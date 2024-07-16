package University.Dormitory.service.UserService;

import University.Dormitory.Converter.UserConverter;
import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.User;
import University.Dormitory.exception.Handler.PasswordNotMatchException;
import University.Dormitory.exception.Handler.UserAlreadyExistException;
import University.Dormitory.exception.Handler.UserNotFoundException;
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
    
//    인터페이스에는 없지만, 쓰이는 함수
    private User findUserById(int userId) {
        return userRepository.findById((long) userId)
                .orElseThrow();
    }


//    해당 함수 사용 시, authority는 converter 이용해서 넘겨줄 것.
    @Override
    public void updateAuthorityByUserId(int userId, Authority authority) {
        User user = findUserById(userId);
        user.setAuthority(authority);
        userRepository.save(user);
    }

    @Override
    public void findAuthorityByUserId(int userId) {
        User user = findUserById(userId);
    }

    @Override
    public boolean checkUserIdDuplicate(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public User SignUp(SignUpRequestDTO.SignUpDto user) {
        if(checkUserIdDuplicate((long) user.getUserId())) {
            log.info("[학번 중복 발생]. UserAlreadyExistException 예외 처리합니다.");
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
            log.info("로그인에 실패했습니다. ");
            throw new UserNotFoundException("해당 학번 조교가 존재하지 않습니다..");
        }

        log.info("userId:{}", userId);
        log.info("전달받은 비밀번호:{}, 데이터베이스 비밀번호:{}", user.getPassword(), customRepository.findPasswordByUserId(userId));
        if (!bCryptPasswordEncoder.matches(user.getPassword(), customRepository.findPasswordByUserId(userId))) {
            log.info("[패스워드 틀림] PasswordNotMatchException 예외처리합니다.");
            throw new PasswordNotMatchException("패스워드가 일치하지 않습니다.");
        }
        log.info("[패스워드 일치. 토큰 발급]");
        Authority authoritiesByUserId = customRepository.findAuthoritiesByUserId(userId);
        Dormitory dormitory = customRepository.findDormitoryByUserId(userId);
        return SignInResponseDTO.SignInDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(userId), authoritiesByUserId,userName,dormitory))
                .build();
    }

    public String SignOut(int userId) {
        userRepository.deleteById((long) userId);
        return "조교가 나갔습니다..";
    }
}