package edu.itstep.final_project_v1.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showUserLogin() {
        return "login";
    }
}
