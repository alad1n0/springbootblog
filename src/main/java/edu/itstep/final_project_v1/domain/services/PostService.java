package edu.itstep.final_project_v1.domain.services;

import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.repositories.PostRepository;
import edu.itstep.final_project_v1.domain.repositories.RatingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;

    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    public Optional<Post> getByIdWithRatings(Long id) {
        return postRepository.findByIdWithRatings(id);
    }

    public List<Post> getAll() {
        return postRepository.findAll().stream()
                .peek(post -> {
                    double averageRating = ratingRepository.findAverageRatingByPostId(post.getId());
                    post.setAverageRating(averageRating);
                })
                .toList();
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
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postRepository.findPostsWithAtLeastTwoLikes(pageable);

        posts.forEach(post -> {
            double averageRating = ratingRepository.findAverageRatingByPostId(post.getId());
            post.setAverageRating(averageRating);
        });

        return posts.stream()
                .sorted((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()))
                .collect(Collectors.toList());
    }

    public List<Post> getPostsByBlog(Long categoryId) {
        return postRepository.findByCategoryId(categoryId).stream()
                .peek(post -> {
                    double averageRating = ratingRepository.findAverageRatingByPostId(post.getId());
                    post.setAverageRating(averageRating);
                })
                .toList();
    }
}
