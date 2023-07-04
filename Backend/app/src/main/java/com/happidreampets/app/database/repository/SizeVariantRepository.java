package com.happidreampets.app.database.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT cv FROM SizeVariant cv WHERE cv.product = :product AND cv.variantId = :variantId")
    SizeVariant findByProductAndVariantId(@Param("product") Product product, @Param("variantId") Long variantId);

    void deleteByProductAndVariantId(Product product, Long variantId);

    SizeVariant findById(long id);
    Page<SizeVariant> findAll(Pageable pageable);
}