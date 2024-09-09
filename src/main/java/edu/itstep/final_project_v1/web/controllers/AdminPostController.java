package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Category;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.services.CategoryService;
import edu.itstep.final_project_v1.domain.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/posts")
public class AdminPostController {

    private final PostService postService;
    private final CategoryService categoryService;

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAll());
        return "admin/posts_list";
    }

    @PostMapping("/{id}")
    public String updatePost(
            @PathVariable Long id,
            @ModelAttribute Post post,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(name = "categoryId", required = false) Long categoryId) {

        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());

            if (categoryId != null) {
                Category category = categoryService.getById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
                existingPost.setCategory(category);
            } else {
                existingPost.setCategory(null);
            }

            if (!imageFile.isEmpty()) {
                try {
                    String base64Image = convertToBase64(imageFile);
                    existingPost.setImageBase64(base64Image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            postService.save(existingPost);
        }

        return "redirect:/admin/posts";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getPostForEdit(@PathVariable Long id, Model model)
    {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<Category> categories = categoryService.getAll();
            model.addAttribute("post", post);
            model.addAttribute("categories", categories);
            return "admin/posts_form";
        } else {
            return "404";
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        Optional<Post> postOptional = postService.getById(id);
        postOptional.ifPresent(postService::delete);
        return "redirect:/admin/posts";
    }

    private String convertToBase64(MultipartFile file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(file.getBytes());
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}