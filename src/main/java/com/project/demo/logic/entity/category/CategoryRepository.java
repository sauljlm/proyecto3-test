package com.project.demo.logic.entity.category;

import com.project.demo.logic.entity.rol.RoleEnum;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findByName(RoleEnum name);
}
