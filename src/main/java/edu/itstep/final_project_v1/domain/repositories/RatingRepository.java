package edu.itstep.final_project_v1.domain.repositories;

import edu.itstep.final_project_v1.domain.models.Account;
import edu.itstep.final_project_v1.domain.models.Post;
import edu.itstep.final_project_v1.domain.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByPostAndAccount(Post post, Account account);

    @Query("SELECT COALESCE(AVG(r.value), 0) FROM Rating r WHERE r.post.id = :postId")
    double findAverageRatingByPostId(@Param("postId") Long postId);

    long countByPostId(Long postId);
}