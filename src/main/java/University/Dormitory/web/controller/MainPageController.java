package University.Dormitory.web.controller;

import University.Dormitory.domain.User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainPageController {
    //    로그인화면에서 쿠키를 발급했으니 이제 메인페이지에서는 해당 쿠키 정보를 빼와야할 차례.
    @GetMapping("/")
    public String home(@CookieValue(name = "userId") Long userId) {
//        로그인했을 때
//        User loginuser = repository.findById(userId);
//        if (loginuser == null) {
//            return "home";//로그인한 유저의 쿠키가 만료되었거나의 상황 다시 홈페이지로 돌려보냄
//        }
//
//    }

//    달력 스케줄 이동

//    지각 여부 이동

//    기숙사 근무페이지로 이동

//    급여 페이지로 이동
        return "test";
    }
}
