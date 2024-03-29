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
import com.happidreampets.app.database.model.WeightVariant;
import com.happidreampets.app.database.repository.WeightVariantRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

import jakarta.transaction.Transactional;

@Component
public class WeightVariantCRUD {

    @Autowired
    private WeightVariantRepository weightVariantRepository;

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private JSONObject getDataInRequiredFormat(Iterable<WeightVariant> data) {
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
                List<WeightVariant> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<WeightVariant> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    public JSONObject getWeightVariantDetailsInJSONWithExcludeProductList(Long variantId,
            List<Long> excludedProductIds) {
        JSONObject weightVariantData = new JSONObject();
        List<WeightVariant> weightVariantList = weightVariantRepository.findAllByVariantIdAndNotInProductIds(variantId,
                excludedProductIds);
        weightVariantData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(weightVariantList).get(ProductConstants.LowerCase.DATA));
        return weightVariantData;
    }

    public List<WeightVariant> getWeightVariantDetailsWithExcludeProductList(Long variantId,
            List<Long> excludedProductIds) {
        return weightVariantRepository.findAllByVariantIdAndNotInProductIds(variantId,
                excludedProductIds);
    }

    public List<WeightVariant> getWeightVariantDetails(Long variantId) {
        return weightVariantRepository.findAllByVariantId(variantId);
    }

    public WeightVariant createWeightVariantWithoutVariantId(Product product) throws Exception {
        List<WeightVariant> existingWeightVariant = weightVariantRepository.findByProduct(product);
        if (!existingWeightVariant.isEmpty()) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_ALREADY_PRESENT);
        }
        WeightVariant weightVariant = new WeightVariant();
        weightVariant.setProduct(product);
        weightVariant.setVariantId(
                (weightVariantRepository.findMaxVariantId() == null ? 0 : weightVariantRepository.findMaxVariantId())
                        + 1);
        weightVariant.setAddedTime(System.currentTimeMillis());

        return weightVariantRepository.save(weightVariant);
    }

    public WeightVariant createWeightVariant(Product product, Long variantId) throws Exception {
        WeightVariant existingWeightVariant = weightVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingWeightVariant != null) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_ALREADY_PRESENT);
        }
        WeightVariant weightVariant = new WeightVariant();
        weightVariant.setProduct(product);
        weightVariant.setVariantId(variantId);
        weightVariant.setAddedTime(System.currentTimeMillis());

        return weightVariantRepository.save(weightVariant);
    }

    @Transactional
    public Boolean deleteWeightVariant(Product product, Long variantId) throws Exception {
        WeightVariant existingWeightVariant = weightVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingWeightVariant == null) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_NOT_FOUND);
        }
        weightVariantRepository.deleteByProductAndVariantId(product, variantId);
        return true;
    }
}
