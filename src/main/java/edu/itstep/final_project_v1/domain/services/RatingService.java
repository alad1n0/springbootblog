package edu.itstep.final_project_v1.domain.services;
import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.models.Rating;
import edu.itstep.final_project_v1.domain.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    public void save(Rating rating) {
        ratingRepository.save(rating);
    }

    public void delete(Rating rating) {
        ratingRepository.delete(rating);
    }

    public Rating findByPostAndAccount(Post post, Account account) {
        return ratingRepository.findByPostAndAccount(post, account).orElse(null);
    }

    public long getLikeCountByPostId(Long postId) {
        return ratingRepository.countByPostId(postId);
    }
}