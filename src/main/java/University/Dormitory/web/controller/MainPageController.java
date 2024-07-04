package University.Dormitory.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class MainPageController {
    //    달력 스케줄 이동
    @GetMapping("/calendar")
    public void toCalendar() {

    }
//    기숙사 근무페이지로 이동
    @GetMapping("/dormitory")
    public void dormitory_sechedule() {

    }
}
