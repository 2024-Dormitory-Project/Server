package University.Dormitory.service.UserService;

import University.Dormitory.domain.User;
import University.Dormitory.repository.JPARepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public void saveUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }


}
