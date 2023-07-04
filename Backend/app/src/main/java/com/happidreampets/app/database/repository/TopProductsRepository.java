package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.TopProducts;

@Repository
public interface TopProductsRepository extends CrudRepository<TopProducts, Long> {
    TopProducts findByProduct(Product product);
    TopProducts findById(long id);

    @Query("SELECT tp FROM TopProducts tp ORDER BY tp.order_number ASC")
    List<TopProducts> findAllSortedDataWithOrderNumberAsc();
    
    @Query("SELECT tp FROM TopProducts tp ORDER BY tp.order_number DESC")
    List<TopProducts> findAllSortedDataWithOrderNumberDesc();
    Page<TopProducts> findAll(Pageable pageable);
}
