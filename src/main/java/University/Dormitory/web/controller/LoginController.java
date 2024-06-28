package University.Dormitory.web.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class LoginController {
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
