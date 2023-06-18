package com.happidreampets.app.database.crud;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.ColorVariantConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.WeightVariant;
import com.happidreampets.app.database.model.WeightVariant.WEIGHTVARIANTCOLUMN;
import com.happidreampets.app.database.repository.WeightVariantRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

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

    private WEIGHTVARIANTCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(WEIGHTVARIANTCOLUMN.class, dbFilter.getSortColumn().toString())) {
                WEIGHTVARIANTCOLUMN enumValue = WEIGHTVARIANTCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
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

    private JSONObject getPageData(Page<WeightVariant> weightVariantPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, weightVariantPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, weightVariantPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, weightVariantPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, weightVariantPage.hasNext());
        return pageData;
    }

    public JSONObject getWeightVariantDetails() {
        JSONObject weightVariantData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<WeightVariant> weightVariantPage = weightVariantRepository.findAll(pageable);
        Iterable<WeightVariant> weightVariantIterable = weightVariantPage.getContent();
        weightVariantData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(weightVariantIterable).get(ProductConstants.LowerCase.DATA));
        weightVariantData.put(ProductConstants.LowerCase.INFO, getPageData(weightVariantPage));
        return weightVariantData;
    }

    public WeightVariant createWeightVariant(Product product, Long variantId) throws Exception {
        WeightVariant existingWeightVariant = weightVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingWeightVariant != null) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_ALREADY_PRESENT);
        }
        WeightVariant weightVariant = new WeightVariant();
        weightVariant.setProduct(product);
        weightVariant.setVariantId(variantId);

        return weightVariantRepository.save(weightVariant);
    }

    public Boolean deleteWeightVariant(Product product, Long variantId) throws Exception {
        WeightVariant existingWeightVariant = weightVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingWeightVariant == null) {
            throw new Exception(ColorVariantConstants.ExceptionMessageCase.VARIANT_NOT_FOUND);
        }
        weightVariantRepository.deleteByProductAndVariantId(product, variantId);
        return true;
    }
}