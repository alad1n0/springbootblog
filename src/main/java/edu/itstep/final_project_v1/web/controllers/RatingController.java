package edu.itstep.final_project_v1.web.controllers;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.models.Rating;
import edu.itstep.final_project_v1.domain.services.AccountService;
import edu.itstep.final_project_v1.domain.services.PostService;
import edu.itstep.final_project_v1.domain.services.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RatingController {

    private final PostService postService;
    private final RatingService ratingService;
    private final AccountService accountService;

//    @PostMapping("/posts/{id}/rate")
//    public String ratePost(@PathVariable Long id, Principal principal) {
//        return handleRating(id, principal, "/posts");
//    }
//
//    @PostMapping("/top_post/{id}/rate")
//    public String rateTopPost(@PathVariable Long id, Principal principal) {
//        return handleRating(id, principal, "/");
//    }

//    @PostMapping("/post/{id}/rate")
//    public String rateSinglePost(@PathVariable Long id, Principal principal) {
//        return rateTopPost(id, principal, "/posts/" + id);
//    }

    @PostMapping("/top_post/{id}/rate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rateTopPost(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Post post = optionalPost.get();
        Optional<Account> optionalAccount = accountService.findOneByEmail(principal.getName());

        if (optionalAccount.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Account account = optionalAccount.get();
        Rating existingRating = ratingService.findByPostAndAccount(post, account);
        boolean liked;

        if (existingRating == null) {
            Rating newRating = new Rating();
            newRating.setValue(1);
            newRating.setPost(post);
            newRating.setAccount(account);
            ratingService.save(newRating);
            liked = true;
        } else {
            ratingService.delete(existingRating);
            liked = false;
        }

        long likeCount = ratingService.getLikeCountByPostId(post.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("postId", post.getId());
        response.put("likeCount", likeCount);
        response.put("liked", liked);

        return ResponseEntity.ok(response);
    }
}