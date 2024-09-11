package edu.itstep.final_project_v1.domain.repositories;

import edu.itstep.final_project_v1.domain.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>
{
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.ratings r WHERE p.id = :id")
    Optional<Post> findByIdWithRatings(@Param("id") Long id);
    @Query("SELECT p FROM Post p WHERE (SELECT COUNT(r) FROM Rating r WHERE r.post.id = p.id) >= 2")
    List<Post> findPostsWithAtLeastTwoLikes(Pageable pageable);
    List<Post> findByCategoryId(Long categoryId);
}

