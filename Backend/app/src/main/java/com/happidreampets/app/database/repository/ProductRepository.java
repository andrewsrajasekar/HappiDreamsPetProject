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

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findByNameAndToBeDeletedIsFalseAndIsVisibleIsTrue(String name);
    List<Product> findByNameAndToBeDeletedIsFalse(String name);
    Product findByIdAndToBeDeletedIsFalseAndIsVisibleIsTrue(long id);
    Product findByIdAndToBeDeletedIsFalse(long id);
    Product findByIdAndCategory(Long id, Category category);
    Page<Product> findAllByToBeDeletedIsFalseAndIsVisibleIsTrue(Pageable pageable);
    Page<Product> findAllByCategoryAndToBeDeletedIsFalseAndIsVisibleIsTrue(Pageable pageable, Category category);

    @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name, CASE WHEN p.color <> null THEN p.color ELSE null END, CASE WHEN p.size <> null THEN p.size ELSE null END, CASE WHEN p.weightUnits <> com.happidreampets.app.database.model.Product$WEIGHT_UNITS.NONE THEN p.weightUnits ELSE null END, CASE WHEN p.weight > 0 THEN p.weight ELSE null END, p.price, p.stocks, p.category, p.thumbnailImageUrl) " +
           "FROM Product p " +
           "WHERE p.category = :category " +
           "AND p.toBeDeleted = false " +
           "AND p.isVisible = true")
    Page<Product> findAllProductsForUIByCategory(Pageable pageable, @Param("category") Category category);

    @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name, CASE WHEN p.color <> null THEN p.color ELSE null END, CASE WHEN p.size <> null THEN p.size ELSE null END, CASE WHEN p.weightUnits <> com.happidreampets.app.database.model.Product$WEIGHT_UNITS.NONE THEN p.weightUnits ELSE null END, CASE WHEN p.weight > 0 THEN p.weight ELSE null END, p.price, p.stocks, p.category, p.thumbnailImageUrl, p.imageUrls ) " +
           "FROM Product p " +
           "WHERE p.id = :id " +
           "AND p.category = :category " +
           "AND p.toBeDeleted = false " +
           "AND p.isVisible = true")
    Product findProductForUIByCategory(@Param("id") Long id, @Param("category") Category category);


    
}

