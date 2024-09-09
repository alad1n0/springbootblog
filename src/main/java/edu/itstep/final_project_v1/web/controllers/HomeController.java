package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Category;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.services.CategoryService;
import edu.itstep.final_project_v1.domain.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PostService postService;
    private final CategoryService categoryService;

    @GetMapping("/")
    public String home(Model model) {
        List<Post> topPosts = postService.getTopRatedPosts(5);
        model.addAttribute("topPosts", topPosts);
        return "home";
    }

    @GetMapping("/posts")
    public String allPosts(Model model) {
        List<Post> posts = postService.getAll();
        List<Category> categories = categoryService.getAll();
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categories);
        return "posts";
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
