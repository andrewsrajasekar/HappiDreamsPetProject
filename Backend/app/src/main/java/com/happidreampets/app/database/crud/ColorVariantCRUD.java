package com.happidreampets.app.database.crud;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ColorVariantConstants;
import com.happidreampets.app.constants.ColorVariantConstants.ExceptionMessageCase;
import com.happidreampets.app.database.model.ColorVariant;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.repository.ColorVariantRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

import jakarta.transaction.Transactional;

@Component
public class ColorVariantCRUD {

    @Autowired
    private ColorVariantRepository colorVariantRepository;

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private JSONObject getDataInRequiredFormat(Iterable<ColorVariant> data) {
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
                List<ColorVariant> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<ColorVariant> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    public JSONObject getColorVariantDetailsInJSONWithExcludeProductList(Long variantId,
            List<Long> excludedProductIds) {
        JSONObject colorVariantData = new JSONObject();
        List<ColorVariant> colorVariantList = colorVariantRepository.findAllByVariantIdAndNotInProductIds(variantId,
                excludedProductIds);
        colorVariantData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(colorVariantList).get(ProductConstants.LowerCase.DATA));
        return colorVariantData;
    }

    public List<ColorVariant> getColorVariantProductDetailsWithExcludeProductList(Long variantId,
            List<Long> excludedProductIds) {
        return colorVariantRepository.findAllByVariantIdAndNotInProductIds(variantId, excludedProductIds);
    }

    public List<ColorVariant> getColorVariantProductDetails(Long variantId) {
        return colorVariantRepository.findAllByVariantId(variantId);
    }

    public ColorVariant createColorVariant(Product product, Long variantId) throws Exception {
        ColorVariant existingColorVariant = colorVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingColorVariant != null) {
            throw new Exception(ExceptionMessageCase.VARIANT_ALREADY_PRESENT);
        }
        ColorVariant colorVariant = new ColorVariant();
        colorVariant.setProduct(product);
        colorVariant.setVariantId(variantId);
        colorVariant.setAddedTime(System.currentTimeMillis());

        return colorVariantRepository.save(colorVariant);
    }

    public ColorVariant createSizeVariantWithoutVariantId(Product product) throws Exception {
        List<ColorVariant> existingColorVariant = colorVariantRepository.findByProduct(product);
        if (!existingColorVariant.isEmpty()) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_ALREADY_PRESENT);
        }
        ColorVariant colorVariant = new ColorVariant();
        colorVariant.setProduct(product);
        colorVariant.setVariantId(
                (colorVariantRepository.findMaxVariantId() == null ? 0 : colorVariantRepository.findMaxVariantId())
                        + 1);
        colorVariant.setAddedTime(System.currentTimeMillis());

        return colorVariantRepository.save(colorVariant);
    }

    @Transactional
    public Boolean deleteColorVariant(Product product, Long variantId) throws Exception {
        ColorVariant existingColorVariant = colorVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingColorVariant == null) {
            throw new Exception(ExceptionMessageCase.VARIANT_NOT_FOUND);
        }
        try {
            colorVariantRepository.deleteByProductAndVariantId(product, variantId);
        } catch (Exception ex) {
            throw ex;
        }

        return true;
    }
}
