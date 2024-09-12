package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Category;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.services.AccountService;
import edu.itstep.final_project_v1.domain.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final AccountService accountService;
    private final CategoryService categoryService;

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        List<Category> categories = categoryService.getAll();
        Account user = accountService.getCurrentUser();

        model.addAttribute("categories", categories);
        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Account account) {
        accountService.updateAccount(account);
        return "redirect:/profile";
    }
}
