package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.SizeVariant;
import com.happidreampets.app.database.model.Product;

@Repository
public interface SizeVariantRepository extends CrudRepository<SizeVariant, Long> {
    List<SizeVariant> findByProduct(Product product);

    List<SizeVariant> findByVariantId(Long variantId);

    @Query("SELECT sv FROM SizeVariant sv WHERE sv.product = :product AND sv.variantId = :variantId")
    SizeVariant findByProductAndVariantId(@Param("product") Product product, @Param("variantId") Long variantId);

    void deleteByProductAndVariantId(Product product, Long variantId);

    SizeVariant findById(long id);

    List<SizeVariant> findAllByVariantId(Long variantId);

    @Query("SELECT s FROM SizeVariant s WHERE s.variantId = :variantId AND s.product.id NOT IN :productIds")
    List<SizeVariant> findAllByVariantIdAndNotInProductIds(
            @Param("variantId") Long variantId,
            @Param("productIds") List<Long> productIds);

    @Query("SELECT MAX(sv.variantId) FROM SizeVariant sv")
    Long findMaxVariantId();
}