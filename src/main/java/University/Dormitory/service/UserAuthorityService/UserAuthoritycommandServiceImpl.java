package University.Dormitory.service.UserAuthorityService;

import University.Dormitory.domain.User;
import University.Dormitory.repository.JPARepository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserAuthoritycommandServiceImpl implements UserAuthoritycommandService {
    private final UserRepository userRepository;

    private User findUserById(int userId) {
        return userRepository.findById((long) userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public void addAuthorityByUserId(int userId) {
        User user = findUserById(userId);
        // add authority logic here
    }

    @Override
    public void removeAuthorityByUserId(int userId) {
        User user = findUserById(userId);
        // remove authority logic here
    }

    @Override
    public void findAuthorityByUserId(int userId) {
        User user = findUserById(userId);
        // find authority logic here
    }


}
