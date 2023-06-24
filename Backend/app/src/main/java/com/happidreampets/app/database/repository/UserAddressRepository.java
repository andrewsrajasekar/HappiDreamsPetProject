package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.UserAddress;

@Repository
public interface UserAddressRepository extends CrudRepository<UserAddress, Long> {
    UserAddress findById(long id);

    Page<UserAddress> findByUserIdAndToBeDeletedIsFalse(long userId, Pageable pageable);

    List<UserAddress> findByUserIdAndToBeDeletedIsFalse(long userId);

    UserAddress findByIdAndUserIdAndToBeDeletedIsFalse(long id, long userId);

    Page<UserAddress> findAll(Pageable pageable);
}
