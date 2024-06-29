package University.Dormitory.service.LoginService;

import University.Dormitory.domain.User;

public interface LoginCommandService {
//    로그인 기능
    public User login(Long loginId, String password);
}
