package edu.itstep.final_project_v1.domain.services;

import edu.itstep.final_project_v1.domain.models.Category;
import edu.itstep.final_project_v1.domain.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository blogRepository;

    public List<Category> getAll() {
        return blogRepository.findAll();
    }

    public Optional<Category> getById(Long id) {
        return blogRepository.findById(id);
    }
}