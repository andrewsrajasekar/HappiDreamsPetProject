package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findByNameAndToBeDeletedIsFalse(String name);

    Category findByIdAndToBeDeletedIsFalse(long id);

    Category findByAnimalAndIdAndToBeDeletedIsFalse(Animal animal, long id);

    Page<Category> findAllByAnimalAndToBeDeletedIsFalse(Pageable pageable, Animal animal);

    List<Category> findAllByAnimalAndToBeDeletedIsFalse(Animal animal, Sort sort);

    List<Category> findAllByAnimalAndToBeDeletedIsFalse(Animal animal);
}
