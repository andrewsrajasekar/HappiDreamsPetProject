package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.happidreampets.app.database.model.WeightVariant;
import com.happidreampets.app.database.model.Product;

@Repository
public interface WeightVariantRepository extends CrudRepository<WeightVariant, Long> {
    List<WeightVariant> findByProduct(Product product);

    List<WeightVariant> findByVariantId(Long variantId);

    @Query("SELECT cv FROM WeightVariant cv WHERE cv.product = :product AND cv.variantId = :variantId")
    WeightVariant findByProductAndVariantId(@Param("product") Product product, @Param("variantId") Long variantId);

    void deleteByProductAndVariantId(Product product, Long variantId);

    WeightVariant findById(long id);

    List<WeightVariant> findAllByVariantId(Long variantId);

    @Query("SELECT MAX(wv.variantId) FROM WeightVariant wv")
    Long findMaxVariantId();
}
