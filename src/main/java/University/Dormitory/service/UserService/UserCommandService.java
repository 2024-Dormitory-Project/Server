package University.Dormitory.service.UserService;

import University.Dormitory.domain.User;

public interface UserCommandService {
//    신규 조교
    void saveUser(User user);
//    조교 퇴사
    void deleteUser(User user);
}
