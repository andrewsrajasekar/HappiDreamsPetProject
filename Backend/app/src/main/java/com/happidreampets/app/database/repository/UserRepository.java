package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByName(String name);

    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    User findById(long id);

    Page<User> findAll(Pageable pageable);
}
