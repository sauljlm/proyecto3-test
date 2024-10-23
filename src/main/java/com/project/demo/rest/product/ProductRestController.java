package com.project.demo.rest.product;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.HttpResponse;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.product.ProductRepository;
import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.user.User;
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
@RequestMapping("/products")
public class ProductRestController {
    @Autowired
    private ProductRepository ProductRepository;

    @Autowired
    private CategoryRepository CategoryRepository;

    @GetMapping
    public ResponseEntity<?> getAllProducts (
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Product> productsPage = ProductRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(productsPage.getTotalPages());
        meta.setTotalElements(productsPage.getTotalElements());
        meta.setPageNumber(productsPage.getNumber() + 1);
        meta.setPageSize(productsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Product created successfully",
                productsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/category/{categoryId}/categories")
    public ResponseEntity<?> getAllByUser (@PathVariable Long categoryId,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           HttpServletRequest request) {
        Optional<Category> foundCategory = CategoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {

            Pageable pageable = PageRequest.of(page-1, size);
            Page<Product> productsPage = ProductRepository.getProductByCategoryId(categoryId, pageable);
            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(productsPage.getTotalPages());
            meta.setTotalElements(productsPage.getTotalElements());
            meta.setPageNumber(productsPage.getNumber() + 1);
            meta.setPageSize(productsPage.getSize());


            return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                    productsPage.getContent(), HttpStatus.OK, meta);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PostMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> addProductToCategory(@PathVariable Long categoryId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Category> foundCategory = CategoryRepository.findById(categoryId);
        if(foundCategory.isPresent()) {
            product.setCategory(foundCategory.get());
            Product savedProduct = ProductRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product created successfully",
                    savedProduct, HttpStatus.CREATED, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Category id " + categoryId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId, HttpServletRequest request) {
        Product product = ProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return new GlobalResponseHandler().handleResponse("Product retrieved successfully",
                product, HttpStatus.OK,request);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = ProductRepository.findById(productId);
        if(foundProduct.isPresent()) {
            product.setId(foundProduct.get().getId());
            ProductRepository.save(product);
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    product, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> patchProduct(@PathVariable Long productId, @RequestBody Product product, HttpServletRequest request) {
        Optional<Product> foundProduct = ProductRepository.findById(productId);
        if(foundProduct.isPresent()) {
            if(product.getStock() != null) foundProduct.get().setStock(product.getStock());
            if(product.getDescription() != null) foundProduct.get().setDescription(product.getDescription());
            ProductRepository.save(foundProduct.get());
            return new GlobalResponseHandler().handleResponse("Product updated successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId, HttpServletRequest request) {
        Optional<Product> foundProduct = ProductRepository.findById(productId);
        if(foundProduct.isPresent()) {
            Optional<Category> category = CategoryRepository.findById(foundProduct.get().getCategory().getId());
            category.get().getProducts().remove(foundProduct.get());
            ProductRepository.deleteById(foundProduct.get().getId());
            return new GlobalResponseHandler().handleResponse("Product deleted successfully",
                    foundProduct.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Product id " + productId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Product authenticatedProduct() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Product) authentication.getPrincipal();
    }

}