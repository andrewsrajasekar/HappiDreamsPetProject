package com.happidreampets.app.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.InternalAuthenticationToken;
import com.happidreampets.app.database.model.User;

@Repository
public interface InternalAuthenticationTokenRepository extends CrudRepository<InternalAuthenticationToken, Long> {
    InternalAuthenticationToken findByUser(User user);

    InternalAuthenticationToken findById(long id);
}