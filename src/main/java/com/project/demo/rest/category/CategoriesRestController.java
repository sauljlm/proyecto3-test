package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoriesRestController {
    @Autowired
    private CategoryRepository CategoryRepository;

    @GetMapping
    public ResponseEntity<?> getAllCategories (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Category> categoriesPage = CategoryRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(categoriesPage.getTotalPages());
        meta.setTotalElements(categoriesPage.getTotalElements());
        meta.setPageNumber(categoriesPage.getNumber() + 1);
        meta.setPageSize(categoriesPage.getSize());

        return new GlobalResponseHandler().handleResponse("Category created successfully",
                categoriesPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> addCategory(@RequestBody Category category, HttpServletRequest request) {
        Category savedCategory = CategoryRepository.save(category);
        return new GlobalResponseHandler().handleResponse("Category created successfully",
                savedCategory, HttpStatus.CREATED, request);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId, HttpServletRequest request) {
        Category category = CategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        return new GlobalResponseHandler().handleResponse("Category retrieved successfully",
                category, HttpStatus.OK,request);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> foundCategory = CategoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {
            category.setId(foundCategory.get().getId());
            CategoryRepository.save(category);
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    category, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> patchCategory(@PathVariable Long categoryId, @RequestBody Category category, HttpServletRequest request) {
        Optional<Category> foundCategory = CategoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {
            if(category.getDescription() != null) foundCategory.get().setDescription(category.getDescription());
            CategoryRepository.save(foundCategory.get());
            return new GlobalResponseHandler().handleResponse("Category updated successfully",
                    foundCategory.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId, HttpServletRequest request) {
        Optional<Category> foundCategory = CategoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {
            CategoryRepository.deleteById(foundCategory.get().getId());
            return new GlobalResponseHandler().handleResponse("Category deleted successfully",
                    foundCategory.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Category authenticatedCategory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Category) authentication.getPrincipal();
    }

}