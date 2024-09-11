package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Category;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.services.AccountService;
import edu.itstep.final_project_v1.domain.services.CategoryService;
import edu.itstep.final_project_v1.domain.services.PostService;
import edu.itstep.final_project_v1.domain.services.RatingService;
import edu.itstep.final_project_v1.web.config.StateMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final RatingService ratingService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        List<Post> posts = postService.getAll();
        List<Category> categories = categoryService.getAll();

        Map<Long, Long> likeCounts = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> ratingService.getLikeCountByPostId(post.getId())));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        for (Post post : posts) {
            String formattedDate = post.getCreatedAt().format(formatter);
            post.setFormattedDate(formattedDate);

            Category postCategory = post.getCategory();
            post.setCategory(postCategory);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("categories", categories);

        if (principal != null) {
            addUserRatingsToModel(model, posts, principal);
        } else {
            model.addAttribute("userLiked", Collections.emptyMap());
        }

        return "home";
    }

    @GetMapping("/posts")
    public String allPosts(Model model, Principal principal) {
        StateMethod.populateModel(model, postService, categoryService);

        List<Post> posts = postService.getAll();
        List<Category> categories = categoryService.getAll();

        Map<Long, Long> likeCounts = posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> ratingService.getLikeCountByPostId(post.getId())));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

        for (Post post : posts) {
            String formattedDate = post.getCreatedAt().format(formatter);
            post.setFormattedDate(formattedDate);
        }

        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("categories", categories);

        addUserRatingsToModel(model, posts, principal);

        return "posts";
    }

    private void addUserRatingsToModel(Model model, List<Post> posts, Principal principal) {
        if (principal != null) {
            Account account = accountService.findOneByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            Map<Long, Boolean> existingRating = posts.stream()
                    .collect(Collectors.toMap(Post::getId, post -> ratingService.findByPostAndAccount(post, account) != null));

            model.addAttribute("userLiked", existingRating);
        } else {
            model.addAttribute("userLiked", Collections.emptyMap());
        }
    }

    @GetMapping("/posts/by-category/{categoryId}")
    public String getPostsByBlog(@PathVariable Long categoryId, Model model) {
        List<Post> posts = postService.getPostsByBlog(categoryId);
        List<Category> categories = categoryService.getAll();

        model.addAttribute("posts", posts);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedBlogId", categoryId);

        return "posts";
    }
}
