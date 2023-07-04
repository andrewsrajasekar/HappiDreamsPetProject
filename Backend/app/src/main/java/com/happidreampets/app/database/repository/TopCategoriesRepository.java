package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.TopCategories;

@Repository
public interface TopCategoriesRepository extends CrudRepository<TopCategories, Long> {
    List<TopCategories> findByCategory(Category category);

    @Query("SELECT tc FROM TopCategories tc WHERE tc.product = :product")
    TopCategories findByProduct(@Param("product") Product product);

    TopCategories findById(long id);

    @Query("SELECT COUNT(DISTINCT tc.category) FROM TopCategories tc")
    long countDistinctCategories();

    @Query("SELECT tc FROM TopCategories tc WHERE tc.category = :category ORDER BY tc.order_number ASC")
    List<TopCategories> findAllSortedCategoryDataWithOrderNumberAsc(@Param("category") Category category);
    
    @Query("SELECT tc FROM TopCategories tc WHERE tc.category = :category ORDER BY tc.order_number DESC")
    List<TopCategories> findAllSortedCategoryDataWithOrderNumberDesc(@Param("category") Category category);

    void deleteByCategory(Category category);

    Page<TopCategories> findAll(Pageable pageable);
}
