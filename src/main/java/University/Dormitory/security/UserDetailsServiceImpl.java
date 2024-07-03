package University.Dormitory.security;

import University.Dormitory.repository.JPARepository.UserAuthoritiesRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserAuthoritiesRepository userAuthoritiesRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("[loadUserByUserName(Sring으로 들어온거 INT로 파싱함)] loadUserByUsername 수행. username : {}" , username);
        return userAuthoritiesRepository.getByUserId(Integer.parseInt(username));
    }
}
