package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.UserAddress;
import com.happidreampets.app.database.model.UserAddressNonDBModel;

import jakarta.transaction.Transactional;

@Repository
public interface UserAddressRepository extends CrudRepository<UserAddress, Long> {
    @Query("SELECT new com.happidreampets.app.database.model.UserAddressNonDBModel(ua.id, ua.address, ua.city, ua.state, ua.country, ua.pincode, ua.isDefaultAddress) FROM UserAddress ua WHERE ua.id = :id")
    UserAddressNonDBModel findByIdINonDBModel(@Param("id") long id);

    @Query("SELECT new com.happidreampets.app.database.model.UserAddressNonDBModel(ua.id, ua.address, ua.city, ua.state, ua.country, ua.pincode, ua.isDefaultAddress) FROM UserAddress ua WHERE ua.user.id = :userId AND ua.toBeDeleted = false")
    List<UserAddressNonDBModel> findByUserIdAndToBeDeletedIsFalseInNonDBModel(@Param("userId") long userId, Sort sort);

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.toBeDeleted = false")
    List<UserAddressNonDBModel> findByUserIdAndToBeDeletedIsFalse(@Param("userId") long userId, Sort sort);

    @Query("SELECT new com.happidreampets.app.database.model.UserAddressNonDBModel(ua.id, ua.address, ua.city, ua.state, ua.country, ua.pincode, ua.isDefaultAddress) FROM UserAddress ua WHERE ua.user.id = :userId AND ua.toBeDeleted = false")
    List<UserAddressNonDBModel> findByUserIdAndToBeDeletedIsFalseInNonDBModel(@Param("userId") long userId);

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.toBeDeleted = false")
    List<UserAddress> findByUserIdAndToBeDeletedIsFalse(@Param("userId") long userId);

    @Query("SELECT new com.happidreampets.app.database.model.UserAddressNonDBModel(ua.id, ua.address, ua.city, ua.state, ua.country, ua.pincode, ua.isDefaultAddress) FROM UserAddress ua WHERE ua.user.id = :userId AND ua.id = :id AND ua.toBeDeleted = false")
    UserAddressNonDBModel findByIdAndUserIdAndToBeDeletedIsFalseInNonDBModel(@Param("id") long id,
            @Param("userId") long userId);

    @Query("SELECT ua FROM UserAddress ua WHERE ua.user.id = :userId AND ua.id = :id AND ua.toBeDeleted = false")
    UserAddress findByIdAndUserIdAndToBeDeletedIsFalse(@Param("id") long id, @Param("userId") long userId);

    @Query("SELECT new com.happidreampets.app.database.model.UserAddressNonDBModel(ua.id, ua.address, ua.city, ua.state, ua.country, ua.pincode, ua.isDefaultAddress) FROM UserAddress ua WHERE ua.user.id = :userId AND ua.toBeDeleted = false")
    Page<UserAddressNonDBModel> findAllByUserIdAndToBeDeletedIsFalse(Pageable pageable, @Param("userId") long userId);

    // Custom query method to update the 'toBeDeleted' column to 'false' for a
    // specific user
    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isDefaultAddress  = false WHERE ua.user = :user")
    int updateIsDefaultAddressAsFalseColumnForUser(@Param("user") User user);
}
