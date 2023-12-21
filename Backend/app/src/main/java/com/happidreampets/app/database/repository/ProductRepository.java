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

       @Query("SELECT DISTINCT new com.happidreampets.app.database.model.Product(p.id, p.name) " +
                     "FROM Product p " +
                     "LEFT JOIN WeightVariant w ON p.id = w.product.id " +
                     "WHERE (w.product.id IS NULL OR " +
                     "       w.variantId IN (SELECT wv.variantId FROM WeightVariant wv GROUP BY wv.variantId HAVING COUNT(wv.id) < :maxWeightVariant)) "
                     +
                     "AND p.weight IS NOT NULL " +
                     "AND (p.weightUnits IS NOT NULL AND p.weightUnits != com.happidreampets.app.database.model.Product$WEIGHT_UNITS.NONE) "
                     +
                     "AND p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       List<Product> findProductsNotInWeightVariantOrUnderLimit(
                     @Param("maxWeightVariant") int maxWeightVariant,
                     @Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT DISTINCT new com.happidreampets.app.database.model.Product(p.id, p.name) " +
                     "FROM Product p " +
                     "LEFT JOIN SizeVariant s ON p.id = s.product.id " +
                     "WHERE (s.product.id IS NULL OR " +
                     "       s.variantId IN (SELECT sv.variantId FROM SizeVariant sv GROUP BY sv.variantId HAVING COUNT(sv.id) < :maxSizeVariant)) "
                     +
                     "AND p.size IS NOT NULL " +
                     "AND p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       List<Product> findProductsNotInSizeVariantOrUnderLimit(
                     @Param("maxSizeVariant") int maxSizeVariant,
                     @Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);

       @Query("SELECT DISTINCT new com.happidreampets.app.database.model.Product(p.id, p.name) " +
                     "FROM Product p " +
                     "LEFT JOIN ColorVariant c ON p.id = c.product.id " +
                     "WHERE (c.product.id IS NULL OR " +
                     "       c.variantId IN (SELECT cv.variantId FROM ColorVariant cv GROUP BY cv.variantId HAVING COUNT(cv.id) < :maxColorVariant)) "
                     +
                     "AND p.color IS NOT NULL " +
                     "AND p.category = :category " +
                     "AND p.toBeDeleted = false " +
                     "AND (:skipVisibility = true OR p.isVisible = true)")
       List<Product> findProductsNotInColorVariantOrUnderLimit(
                     @Param("maxColorVariant") int maxColorVariant,
                     @Param("category") Category category,
                     @Param("skipVisibility") boolean skipVisibility);
}
