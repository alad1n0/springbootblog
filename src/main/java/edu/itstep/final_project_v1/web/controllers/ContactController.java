package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.services.CategoryService;
import edu.itstep.final_project_v1.domain.services.PostService;
import edu.itstep.final_project_v1.web.config.StateMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ContactController {
    private final PostService postService;
    private final CategoryService categoryService;

    @GetMapping("/contact")
    public String about(Model model) {
        StateMethod.populateModel(model, postService, categoryService);
        return "contact";
    }
}
