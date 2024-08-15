package University.Dormitory.service.UserService;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.Enum.Dormitory;
import University.Dormitory.domain.User;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserCommandService {

    String updateAuthorityByUserId(long userId, Authority authority);
    
    //    학번 중복 여부 확인
    boolean checkUserIdDuplicate(long userId);

    boolean checkUserNameDuplicate(String name);

    //    회원가입시키기
    User SignUp(SignUpRequestDTO.SignUpDto user);

    //    로그인 기능
    Map<String ,String> SignIn(Long userId, String pw);

    //    회원탈퇴
    String SignOut(long userId);

    Optional<Long> findUserIdByName(String name);

//    refreshToken 새로 저장
    void updateRefreshToken(String token, long userId);

//    refreshToken 삭제
    void deleteRefreshTokenByUserId(long userId);

    //    N번째 기숙사의 모든 사용자의 이름 불러오기
    List<String> allNamesInNDormitory(Dormitory dormitory);
}