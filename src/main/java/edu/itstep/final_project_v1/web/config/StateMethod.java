package edu.itstep.final_project_v1.web.config;

import edu.itstep.final_project_v1.domain.models.Category;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.services.CategoryService;
import edu.itstep.final_project_v1.domain.services.PostService;
import org.springframework.ui.Model;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class StateMethod {

    public static void populateModel(Model model, PostService postService, CategoryService categoryService) {
        List<Post> topPosts = postService.getTopRatedPosts(6);
        List<Category> categories = categoryService.getAll();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

        for (Post post : topPosts) {
            String formattedDate = post.getCreatedAt().format(formatter);
            post.setFormattedDate(formattedDate);

            Category postCategory = post.getCategory();
            post.setCategory(postCategory);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("topPosts", topPosts);
    }
}
