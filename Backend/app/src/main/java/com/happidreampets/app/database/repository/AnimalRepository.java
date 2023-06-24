package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.Animal;

@Repository
public interface AnimalRepository extends CrudRepository<Animal, Long> {
    List<Animal> findByNameAndToBeDeletedIsFalse(String name);

    Animal findByIdAndToBeDeletedIsFalse(long id);

    Page<Animal> findAllByToBeDeletedIsFalse(Pageable pageable);
}
