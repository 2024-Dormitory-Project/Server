package University.Dormitory.Converter;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.User;
import University.Dormitory.domain.UserAuthorities;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserAuthorityConverter {
    public static List<UserAuthorities> touserAuthorities(SignUpRequestDTO.SignUpDto request, User user) {
        List<Authority> authorities = new ArrayList<>();
        if ("사감".equals(request.getAuthority())) {
            log.info("사감 인식");
            authorities.add(Authority.ASSISTANT);
            authorities.add(Authority.SCHEDULE_ASSISTANT);
            authorities.add(Authority.PERFECT);
            List<UserAuthorities> userAuthoritiesList = new ArrayList<>();
            for (Authority authority : authorities) {
                UserAuthorities userAuthorities = UserAuthorities.builder()
                        .authorities(authority)
                        .user(user)
                        .build();
                userAuthoritiesList.add(userAuthorities);
            }
            return userAuthoritiesList;
        }
        else if ("스케줄 관리 조교".equals(request.getAuthority())) {
            authorities.add(Authority.ASSISTANT);
            authorities.add(Authority.SCHEDULE_ASSISTANT);
            List<UserAuthorities> userAuthoritiesList = new ArrayList<>();
            for (Authority authority : authorities) {
                UserAuthorities userAuthorities = UserAuthorities.builder()
                        .authorities(authority)
                        .user(user)
                        .build();
                userAuthoritiesList.add(userAuthorities);
            }
            return userAuthoritiesList;
        } else {
            authorities.add(Authority.ASSISTANT);
            List<UserAuthorities> userAuthoritiesList = new ArrayList<>();
            for (Authority authority : authorities) {
                UserAuthorities userAuthorities = UserAuthorities.builder()
                        .authorities(authority)
                        .user(user)
                        .build();
                userAuthoritiesList.add(userAuthorities);
            }
            return userAuthoritiesList;
        }
    }
}
