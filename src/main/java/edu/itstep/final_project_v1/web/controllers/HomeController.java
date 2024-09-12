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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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

        Account account = new Account();
        model.addAttribute("account", account);
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
    public String allPosts(Model model,
                           Principal principal,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "8") int size) {
        StateMethod.populateModel(model, postService, categoryService);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getAll(pageable);
        List<Category> categories = categoryService.getAll();

        Account account = new Account();
        model.addAttribute("account", account);

        Map<Long, Long> likeCounts = postPage.getContent().stream()
                .collect(Collectors.toMap(Post::getId, post -> ratingService.getLikeCountByPostId(post.getId())));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

        for (Post post : postPage.getContent()) {
            String formattedDate = post.getCreatedAt().format(formatter);
            post.setFormattedDate(formattedDate);
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        addUserRatingsToModel(model, postPage.getContent(), principal);

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
    public String getPostsByBlog(@PathVariable Long categoryId,
                                 Model model,
                                 Principal principal,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size) {
        StateMethod.populateModel(model, postService, categoryService);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getPostsByBlog(categoryId, pageable);
        List<Category> categories = categoryService.getAll();

        Map<Long, Long> likeCounts = postPage.getContent().stream()
                .collect(Collectors.toMap(Post::getId, post -> ratingService.getLikeCountByPostId(post.getId())));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

        for (Post post : postPage.getContent()) {
            String formattedDate = post.getCreatedAt().format(formatter);
            post.setFormattedDate(formattedDate);
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        addUserRatingsToModel(model, postPage.getContent(), principal);

        return "posts";
    }
}
