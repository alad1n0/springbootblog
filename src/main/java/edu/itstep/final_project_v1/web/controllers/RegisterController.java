package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerNewUser(@ModelAttribute Account account) {
        accountService.save(account);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("email", account.getEmail());
        return ResponseEntity.ok(response);
    }
}
