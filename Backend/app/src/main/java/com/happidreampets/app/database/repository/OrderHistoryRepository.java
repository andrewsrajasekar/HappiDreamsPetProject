package com.happidreampets.app.database.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.OrderHistory;
import com.happidreampets.app.database.model.User;

@Repository
public interface OrderHistoryRepository extends CrudRepository<OrderHistory, Long> {
    List<OrderHistory> findByAddedTime(Long time);
    List<OrderHistory> findByUser(User user);
    OrderHistory findById(long id);
    Page<OrderHistory> findAll(Pageable pageable);
}
