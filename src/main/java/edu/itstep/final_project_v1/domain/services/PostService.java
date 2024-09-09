package edu.itstep.final_project_v1.domain.services;

import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.repositories.PostRepository;
import edu.itstep.final_project_v1.domain.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;

    private double averageRating;

    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    public Optional<Post> getByIdWithRatings(Long id) {
        return postRepository.findByIdWithRatings(id);
    }

    public List<Post> getAll() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            double averageRating = ratingRepository.findAverageRatingByPostId(post.getId());
            post.setAverageRating(averageRating);
        }
        return posts;
    }

    public void save(Post post) {
        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }

    public List<Post> getTopRatedPosts(int limit) {
        List<Post> posts = postRepository.findAll();

        posts.forEach(post -> {
            double averageRating = ratingRepository.findAverageRatingByPostId(post.getId());
            post.setAverageRating(averageRating);
        });

        return posts.stream()
                .filter(post -> post.getAverageRating() == 5.0)
                .sorted((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()))
                .limit(limit)
                .toList();
    }

    public List<Post> getPostsByBlog(Long categoryId) {
        List<Post> posts = postRepository.findByCategoryId(categoryId);

        posts.forEach(post -> {
            double averageRating = ratingRepository.findAverageRatingByPostId(post.getId());
            post.setAverageRating(averageRating);
        });

        return posts;
    }
}
