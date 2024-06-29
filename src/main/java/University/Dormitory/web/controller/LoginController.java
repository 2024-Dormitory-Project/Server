package University.Dormitory.web.controller;

import University.Dormitory.service.LoginService.LoginCommandService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginCommandService loginCommandService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm;

    @PostMapping("/login")
    public String login(HttpServletResponse httpServletResponse) {
        if (/*에러가 있을 시*/) {
            return error;
        }

    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "userId");
        return "로그인 페이지로 다시 연결하기";
    }

    private static void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
