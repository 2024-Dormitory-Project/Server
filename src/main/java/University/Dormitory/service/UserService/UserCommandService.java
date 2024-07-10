package University.Dormitory.service.UserService;

import University.Dormitory.domain.Enum.Authority;
import University.Dormitory.domain.User;
import University.Dormitory.web.dto.SignInDTO.SignInRequestDTO;
import University.Dormitory.web.dto.SignInDTO.SignInResponseDTO;
import University.Dormitory.web.dto.SignUpDTO.SignUpRequestDTO;

public interface UserCommandService {

    void updateAuthorityByUserId(int userId, Authority authority);

    //    현재 권한 정보
    void findAuthorityByUserId(int userId);

    
    //    학번 중복 여부 확인
    boolean checkUserIdDuplicate(Long userId);

    //    회원가입시키기
    User SignUp(SignUpRequestDTO.SignUpDto user);

    //    로그인 기능
    SignInResponseDTO.SignInDto SignIn(SignInRequestDTO.SignInDto user);

    //    회원탈퇴
    String SignOut(int userId);
}