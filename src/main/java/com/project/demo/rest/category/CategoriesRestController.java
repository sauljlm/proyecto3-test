package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesRestController {
    @Autowired
    private CategoryRepository CategoryRepository;

    @GetMapping
    public List<Category> getAllCategories() {
        return (List<Category>) CategoryRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        return CategoryRepository.save(category);
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return CategoryRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return CategoryRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(category.getName());
                    existingUser.setDescription(category.getDescription());
                    return CategoryRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    category.setId(id);
                    return CategoryRepository.save(category);
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public void deleteCategory(@PathVariable Long id) {
        CategoryRepository.deleteById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Category authenticatedCategory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Category) authentication.getPrincipal();
    }

}