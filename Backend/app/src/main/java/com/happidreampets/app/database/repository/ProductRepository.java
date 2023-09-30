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

       Product findByIdAndCategoryAndToBeDeletedIsFalse(Long id, Category category);

       Page<Product> findAllByToBeDeletedIsFalseAndIsVisibleIsTrue(Pageable pageable);

       Page<Product> findAllByCategoryAndToBeDeletedIsFalseAndIsVisibleIsTrue(Pageable pageable, Category category);

       @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name, CASE WHEN p.color <> null THEN p.color ELSE null END, CASE WHEN p.size <> null THEN p.size ELSE null END, CASE WHEN p.weightUnits <> com.happidreampets.app.database.model.Product$WEIGHT_UNITS.NONE THEN p.weightUnits ELSE null END, CASE WHEN p.weight > 0 THEN p.weight ELSE null END, p.stocks, p.price, p.category, p.thumbnailImageUrl) "
                     +
                     "FROM Product p " +
                     "WHERE p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       Page<Product> findAllProductsForUIByCategory(Pageable pageable, @Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name, CASE WHEN p.color <> null THEN p.color ELSE null END, CASE WHEN p.size <> null THEN p.size ELSE null END, CASE WHEN p.weightUnits <> com.happidreampets.app.database.model.Product$WEIGHT_UNITS.NONE THEN p.weightUnits ELSE null END, CASE WHEN p.weight > 0 THEN p.weight ELSE null END, p.stocks, p.price, p.category, p.thumbnailImageUrl) "
                     +
                     "FROM Product p " +
                     "WHERE p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       List<Product> findAllProductsByCategory(@Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name, CASE WHEN p.color <> null THEN p.color ELSE null END, CASE WHEN p.size <> null THEN p.size ELSE null END, CASE WHEN p.weightUnits <> com.happidreampets.app.database.model.Product$WEIGHT_UNITS.NONE THEN p.weightUnits ELSE null END, CASE WHEN p.weight > 0 THEN p.weight ELSE null END, p.stocks, p.price, p.category, p.thumbnailImageUrl) "
                     +
                     "FROM Product p " +
                     "WHERE p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true) " +
                     "AND p.price >= :minPrice " +
                     "AND p.price <= :maxPrice")
       Page<Product> findAllProductsForUIByCategoryByMinAndMaxPrice(Pageable pageable,
                     @Param("category") Category category, @Param("minPrice") Long minPrice,
                     @Param("maxPrice") Long maxPrice, @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name, p.description, p.details, p.richtextDetails, CASE WHEN p.color <> null THEN p.color ELSE null END, CASE WHEN p.size <> null THEN p.size ELSE null END, CASE WHEN p.weightUnits <> com.happidreampets.app.database.model.Product$WEIGHT_UNITS.NONE THEN p.weightUnits ELSE null END, CASE WHEN p.weight > 0 THEN p.weight ELSE null END, p.stocks, p.price, p.category, p.thumbnailImageUrl, p.images ) "
                     +
                     "FROM Product p " +
                     "WHERE p.id = :id " +
                     "AND p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       Product findProductForUIByCategory(@Param("id") Long id, @Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name )" +
                     "FROM Product p " +
                     "WHERE p.id NOT IN (SELECT w.product.id FROM WeightVariant w) " +
                     "AND p.weight <> null " +
                     "AND p.weightUnits <> null " +
                     "AND p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       List<Product> findProductsNotInWeightVariant(@Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name )" +
                     "FROM Product p " +
                     "WHERE p.id NOT IN (SELECT s.product.id FROM SizeVariant s) " +
                     "AND p.size <> null " +
                     "AND p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       List<Product> findProductsNotInSizeVariant(@Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT new com.happidreampets.app.database.model.Product(p.id, p.name )" +
                     "FROM Product p " +
                     "WHERE p.id NOT IN (SELECT c.product.id FROM ColorVariant c) " +
                     "AND p.color <> null " +
                     "AND p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       List<Product> findProductsNotInColorVariant(@Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);
}
