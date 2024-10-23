package com.project.demo.logic.entity.product;

import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);

    Page<Product> getProductByUserId(Long id, Pageable pageable);
}
