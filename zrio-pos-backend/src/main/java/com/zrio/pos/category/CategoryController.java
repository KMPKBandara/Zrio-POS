package com.zrio.pos.category;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> getActiveCategories() {

        return categoryService
                .findActiveCategories()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/all")
    public List<CategoryResponse> getAllCategories() {

        return categoryService
                .findAllCategories()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody
            CreateCategoryRequest request
    ) {

        try {

            Category category =
                    categoryService.createCategory(
                            request.name()
                    );

            return toResponse(category);

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateCategoryRequest request
    ) {

        try {

            Category category =
                    categoryService.updateCategory(
                            id,
                            request.name()
                    );

            return toResponse(category);

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );

        } catch (IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage()
            );
        }
    }

    @PatchMapping("/{id}/active")
    public CategoryResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody
            UpdateCategoryStatusRequest request
    ) {

        try {

            Category category =
                    categoryService.changeStatus(
                            id,
                            request.active()
                    );

            return toResponse(category);

        } catch (NoSuchElementException exception) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    private CategoryResponse toResponse(
            Category category
    ) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isActive()
        );
    }
}