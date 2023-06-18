package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByName(String name);

    Category findById(long id);

    Page<Category> findAllByAnimal(Pageable pageable, Animal animal);
}
