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

    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        List<Post> posts = postService.getAll();
        List<Category> categories = categoryService.getAll();

        model.addAttribute("account", new Account());
        model.addAttribute("posts", formatPosts(posts));
        model.addAttribute("likeCounts", getLikeCounts(posts));
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
                           @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        StateMethod.populateModel(model, postService, categoryService);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getAll(pageable);
        List<Category> categories = categoryService.getAll();

        model.addAttribute("account", new Account());
        model.addAttribute("posts", formatPosts(postPage.getContent()));
        model.addAttribute("likeCounts", getLikeCounts(postPage.getContent()));
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        addUserRatingsToModel(model, postPage.getContent(), principal);

        return "posts";
    }

    @GetMapping("/posts/by-category/{categoryId}")
    public String getPostsByCategory(@PathVariable Long categoryId,
                                     Model model,
                                     Principal principal,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        StateMethod.populateModel(model, postService, categoryService);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getPostsByBlog(categoryId, pageable);
        List<Category> categories = categoryService.getAll();

        model.addAttribute("posts", formatPosts(postPage.getContent()));
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("likeCounts", getLikeCounts(postPage.getContent()));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());

        addUserRatingsToModel(model, postPage.getContent(), principal);

        return "posts";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam("s") String title,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              Model model,
                              Principal principal,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        StateMethod.populateModel(model, postService, categoryService);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = (categoryId != null)
                ? postService.searchPostsByTitleAndCategoryWithPagination(title, categoryId, pageable)
                : postService.searchPostsByTitleWithPagination(title, pageable);

        List<Category> categories = categoryService.getAll();

        model.addAttribute("posts", formatPosts(postPage.getContent()));
        model.addAttribute("likeCounts", getLikeCounts(postPage.getContent()));
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("searchQuery", title);
        model.addAttribute("selectedCategoryId", categoryId);

        addUserRatingsToModel(model, postPage.getContent(), principal);

        return "posts";
    }

    private List<Post> formatPosts(List<Post> posts) {
        return posts.stream()
                .peek(post -> post.setFormattedDate(post.getCreatedAt().format(DATE_FORMATTER)))
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getLikeCounts(List<Post> posts) {
        return posts.stream()
                .collect(Collectors.toMap(Post::getId, post -> ratingService.getLikeCountByPostId(post.getId())));
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
}