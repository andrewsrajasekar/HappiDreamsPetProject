package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.ColorVariant;
import com.happidreampets.app.database.model.Product;

@Repository
public interface ColorVariantRepository extends CrudRepository<ColorVariant, Long> {
    List<ColorVariant> findByProduct(Product product);

    List<ColorVariant> findByVariantId(Long variant_id);

    @Query("SELECT cv FROM ColorVariant cv WHERE cv.product = :product AND cv.variantId = :variantId")
    ColorVariant findByProductAndVariantId(@Param("product") Product product, @Param("variantId") Long variantId);

    @Modifying
    @Query("DELETE FROM ColorVariant cv WHERE cv.product = :product AND cv.variantId = :variantId")
    void deleteByProductAndVariantId(@Param("product") Product product, @Param("variantId") Long variantId);

    ColorVariant findById(long id);

    List<ColorVariant> findAllByVariantId(Long variantId);

    @Query("SELECT c FROM ColorVariant c WHERE c.variantId = :variantId AND c.product.id NOT IN :productIds")
    List<ColorVariant> findAllByVariantIdAndNotInProductIds(
            @Param("variantId") Long variantId,
            @Param("productIds") List<Long> productIds);

    @Query("SELECT MAX(cv.variantId) FROM ColorVariant cv")
    Long findMaxVariantId();
}