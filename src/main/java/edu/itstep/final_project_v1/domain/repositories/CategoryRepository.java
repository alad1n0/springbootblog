package edu.itstep.final_project_v1.domain.repositories;

import edu.itstep.final_project_v1.domain.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.id IN :categoryIds")
    List<Category> findCategoriesByIds(@Param("categoryIds") Set<Long> categoryIds);
}