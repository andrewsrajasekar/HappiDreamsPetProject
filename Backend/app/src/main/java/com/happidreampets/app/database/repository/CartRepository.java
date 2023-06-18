package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.Cart;
import com.happidreampets.app.database.model.User;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    Cart findById(long id);
    Page<Cart> findAll(Pageable pageable);
}
