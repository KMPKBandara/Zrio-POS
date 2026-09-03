package com.zrio.pos.category;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(
            CategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(String name) {

        String cleanedName = cleanName(name);

        if (categoryRepository
                .existsByNameIgnoreCase(cleanedName)) {

            throw new IllegalArgumentException(
                    "Category name already exists"
            );
        }

        Category category = new Category();

        category.setName(cleanedName);
        category.setActive(true);

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> findActiveCategories() {

        return categoryRepository
                .findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategories() {

        return categoryRepository
                .findAllByOrderByNameAsc();
    }

    @Transactional
    public Category updateCategory(
            Long id,
            String name
    ) {

        Category category = findRequired(id);

        String cleanedName = cleanName(name);

        if (categoryRepository
                .existsByNameIgnoreCaseAndIdNot(
                        cleanedName,
                        id
                )) {

            throw new IllegalArgumentException(
                    "Category name already exists"
            );
        }

        category.setName(cleanedName);

        return category;
    }

    @Transactional
    public Category changeStatus(
            Long id,
            boolean active
    ) {

        Category category = findRequired(id);

        category.setActive(active);

        return category;
    }

    private Category findRequired(Long id) {

        return categoryRepository
                .findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Category not found"
                        )
                );
    }

    private String cleanName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Category name cannot be blank"
            );
        }

        return name.trim();
    }
}