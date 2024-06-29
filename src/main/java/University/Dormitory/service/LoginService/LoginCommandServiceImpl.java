package University.Dormitory.service.LoginService;

import University.Dormitory.domain.User;

import java.util.Optional;

public class LoginCommandServiceImpl implements LoginCommandService {
    /**
     * @param loginId
     * @param password
     * @return null이면 로그인 실패
     */
    @Override
    public User login(Long loginId, String password) {
//        Optional<User> OptionalLoginUser=  repository.findByLoginId(loginId);
//        User user = OptionalLoginUser.get();
//        if(user.getPassword().equals(password)) {
//            return user;
//        }
//        else {
//            return null;
//        }
//      아래와 같은 기능. 취사선택하기.
        Optional<User> OptionalLoginUser = repository.findByLoginId(loginId);
        return OptionalLoginUser.filter(user -> user.getPassword().equals(password))
                .orElse(null); //여기에는 이제 예외 반환처리하면될듯?
    }


}
