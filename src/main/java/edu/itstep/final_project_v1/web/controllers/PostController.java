package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.*;
import edu.itstep.final_project_v1.domain.services.*;
import edu.itstep.final_project_v1.web.dto.CommentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final RatingService ratingService;
    private final AccountService accountService;
    private final CommentService commentService;

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model, Principal principal) {
        List<Category> categories = categoryService.getAll();
        model.addAttribute("categories", categories);

        Optional<Post> optionalPost = postService.getByIdWithRatings(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
            String formattedDate = post.getCreatedAt().format(formatter);
            post.setFormattedDate(formattedDate);

            Category postCategory = postService.getCategoryByPostId(id);
            model.addAttribute("postCategory", postCategory);

            List<Rating> ratings = post.getRatings();
            double averageRating = 0;
            if (!ratings.isEmpty()) {
                averageRating = ratings.stream().mapToInt(Rating::getValue).average().orElse(0);
            }

            long likeCount = ratingService.getLikeCountByPostId(id);
            long commentCount = commentService.countByPostId(id);

            List<Comment> comments = commentService.getByPostId(id);

            DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy / HH:mm", Locale.ENGLISH);
            for (Comment comment : comments) {
                String formattedCommentDate = comment.getCreatedAt().format(originalFormatter);
                comment.setFormattedDate(formattedCommentDate);
            }

            model.addAttribute("post", post);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("comments", comments);
            model.addAttribute("commentCount", commentCount);
            model.addAttribute("likeCount", likeCount);

            if (principal != null) {
                Account account = accountService.findOneByEmail(principal.getName())
                        .orElseThrow(() -> new IllegalArgumentException("Account not found"));
                Rating existingRating = ratingService.findByPostAndAccount(post, account);
                model.addAttribute("userLiked", existingRating != null);
            } else {
                model.addAttribute("userLiked", false);
            }

            Post previousPost = postService.getPreviousPost(id);
            Post nextPost = postService.getNextPost(id);

            model.addAttribute("previousPostUrl", previousPost != null ? "/posts/" + previousPost.getId() : null);
            model.addAttribute("nextPostUrl", nextPost != null ? "/posts/" + nextPost.getId() : null);

            return "post";
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long postId, @ModelAttribute Comment newComment, Principal principal) {
        Optional<Post> optionalPost = postService.getById(postId);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Account account = accountService.findOneByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));

            newComment.setPost(post);
            newComment.setAccount(account);

            Comment savedComment = commentService.save(newComment);

            CommentDTO commentDTO = new CommentDTO(savedComment);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("comment", commentDTO);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Post not found");

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post, @RequestParam("image") MultipartFile imageFile, @RequestParam(name = "categoryId", required = false) Long categoryId, Principal principal) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            String authUsername = principal.getName();
            if (!existingPost.getAccount().getEmail().equals(authUsername)) {
                return "404";
            }

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

        return "redirect:/posts/" + id;
    }

    @GetMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(Model model) {
        Post post = new Post();
        List<Category> categories = categoryService.getAll();
        model.addAttribute("post", post);
        model.addAttribute("categories", categories);
        return "post_new";
    }

    @PostMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(@ModelAttribute Post post, @RequestParam(name = "categoryId", required = false) Long categoryId, @RequestParam(name = "image", required = false) MultipartFile imageFile, Principal principal) {
        String authUsername = principal.getName();

        Account account = accountService.findOneByEmail(authUsername)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (categoryId != null) {
            Category category = categoryService.getById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String base64Image = convertToBase64(imageFile);
                post.setImageBase64(base64Image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        post.setAccount(account);
        postService.save(post);
        return "redirect:/";
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getPostForEdit(@PathVariable Long id, Model model, Principal principal) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            String authUsername = principal.getName();
            if (!post.getAccount().getEmail().equals(authUsername)) {
                return "404";
            }

            List<Category> categories = categoryService.getAll();
            model.addAttribute("post", post);
            model.addAttribute("categories", categories);
            return "post_edit";
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/{id}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@PathVariable Long id, Principal principal) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            String authUsername = principal.getName();
            if (!post.getAccount().getEmail().equals(authUsername)) {
                return "404";
            }

            postService.delete(post);
            return "redirect:/";
        } else {
            return "404";
        }
    }

    private String convertToBase64(MultipartFile file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(file.getBytes());
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}