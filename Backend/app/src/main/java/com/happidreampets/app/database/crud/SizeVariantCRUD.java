package com.happidreampets.app.database.crud;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.ColorVariantConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.SizeVariant;
import com.happidreampets.app.database.repository.SizeVariantRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

import jakarta.transaction.Transactional;

@Component
public class SizeVariantCRUD {

    @Autowired
    private SizeVariantRepository sizeVariantRepository;

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private JSONObject getDataInRequiredFormat(Iterable<SizeVariant> data) {
        JSONObject responseData = new JSONObject();
        responseData.put(ProductConstants.LowerCase.DATA, JSONObject.NULL);
        if (getDbFilter() != null) {
            if (getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
                JSONArray responseArray = new JSONArray();
                data.forEach(row -> {
                    responseArray.put(row.toJSON());
                });
                responseData.put(ProductConstants.LowerCase.DATA, responseArray);
            } else if (getDbFilter().getFormat().equals(DATAFORMAT.POJO)) {
                List<SizeVariant> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<SizeVariant> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    public JSONObject getSizeVariantDetailsInJSONWithExcludeProductList(Long variantId, List<Long> excludedProductIds) {
        JSONObject sizeVariantData = new JSONObject();
        List<SizeVariant> sizeVariantList = sizeVariantRepository.findAllByVariantIdAndNotInProductIds(variantId,
                excludedProductIds);
        sizeVariantData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(sizeVariantList).get(ProductConstants.LowerCase.DATA));
        return sizeVariantData;
    }

    public List<SizeVariant> getSizeVariantDetailsWithExcludeProductList(Long variantId,
            List<Long> excludedProductIds) {
        return sizeVariantRepository.findAllByVariantIdAndNotInProductIds(variantId, excludedProductIds);
    }

    public List<SizeVariant> getSizeVariantDetails(Long variantId) {
        return sizeVariantRepository.findAllByVariantId(variantId);
    }

    public SizeVariant createSizeVariant(Product product, Long variantId) throws Exception {
        SizeVariant existingSizeVariant = sizeVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingSizeVariant != null) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_ALREADY_PRESENT);
        }
        SizeVariant sizeVariant = new SizeVariant();
        sizeVariant.setProduct(product);
        sizeVariant.setVariantId(variantId);
        sizeVariant.setAddedTime(System.currentTimeMillis());

        return sizeVariantRepository.save(sizeVariant);
    }

    public SizeVariant createSizeVariantWithoutVariantId(Product product) throws Exception {
        List<SizeVariant> existingSizeVariant = sizeVariantRepository.findByProduct(product);
        if (!existingSizeVariant.isEmpty()) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_ALREADY_PRESENT);
        }
        SizeVariant sizeVariant = new SizeVariant();
        sizeVariant.setProduct(product);
        sizeVariant.setVariantId(
                (sizeVariantRepository.findMaxVariantId() == null ? 0 : sizeVariantRepository.findMaxVariantId()) + 1);
        sizeVariant.setAddedTime(System.currentTimeMillis());

        return sizeVariantRepository.save(sizeVariant);
    }

    @Transactional
    public Boolean deleteSizeVariant(Product product, Long variantId) throws Exception {
        SizeVariant existingSizeVariant = sizeVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingSizeVariant == null) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_NOT_FOUND);
        }
        sizeVariantRepository.deleteByProductAndVariantId(product, variantId);
        return true;
    }
}
