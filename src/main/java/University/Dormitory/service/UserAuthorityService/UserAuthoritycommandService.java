package University.Dormitory.service.UserAuthorityService;

public interface UserAuthoritycommandService {
//    권한 추가
    void addAuthorityByUserId(int userId);
//    권한 제거
    void removeAuthorityByUserId(int userId);

//    현재 권한 정보
    void findAuthorityByUserId(int userId);

}
