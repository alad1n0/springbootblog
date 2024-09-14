package edu.itstep.final_project_v1.domain.repositories;

import edu.itstep.final_project_v1.domain.models.Category;
import edu.itstep.final_project_v1.domain.models.Post;
import org.springframework.data.domain.Page;
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

    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p.category FROM Post p WHERE p.id = :postId")
    Category findCategoryByPostId(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.id < :postId ORDER BY p.id DESC")
    List<Post> findPreviousPosts(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.id > :postId ORDER BY p.id ASC")
    List<Post> findNextPosts(@Param("postId") Long postId, Pageable pageable);

    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Post> findByTitleContainingAndCategoryId(String title, Long categoryId, Pageable pageable);
}