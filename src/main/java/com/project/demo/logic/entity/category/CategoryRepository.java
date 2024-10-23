package com.project.demo.logic.entity.category;

import com.project.demo.logic.entity.rol.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(RoleEnum name);
}
