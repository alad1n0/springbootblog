package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.models.Rating;
import edu.itstep.final_project_v1.domain.services.AccountService;
import edu.itstep.final_project_v1.domain.services.PostService;
import edu.itstep.final_project_v1.domain.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RatingController {

    private final PostService postService;
    private final RatingService ratingService;
    private final AccountService accountService;

    @PostMapping("/posts/{id}/rate")
    public String ratePost(@PathVariable Long id, @RequestParam int ratingValue, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Account account = accountService.findOneByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Post post = postService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Rating rating = new Rating();
        rating.setValue(ratingValue);
        rating.setPost(post);
        rating.setAccount(account);

        ratingService.save(rating);

        return "redirect:/posts/" + id;
    }
}
