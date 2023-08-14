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

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ColorVariantConstants.ExceptionMessageCase;
import com.happidreampets.app.database.model.ColorVariant;
import com.happidreampets.app.database.model.ColorVariant.COLORVARIANTCOLUMN;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.repository.ColorVariantRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

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

    private COLORVARIANTCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(COLORVARIANTCOLUMN.class, dbFilter.getSortColumn().toString())) {
                COLORVARIANTCOLUMN enumValue = COLORVARIANTCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
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

    private JSONObject getPageData(Page<ColorVariant> colorVariantPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, colorVariantPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, colorVariantPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, colorVariantPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, colorVariantPage.hasNext());
        return pageData;
    }

    public JSONObject getColorVariantDetails() {
        JSONObject colorVariantData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<ColorVariant> colorVariantPage = colorVariantRepository.findAll(pageable);
        Iterable<ColorVariant> colorVariantIterable = colorVariantPage.getContent();
        colorVariantData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(colorVariantIterable).get(ProductConstants.LowerCase.DATA));
        colorVariantData.put(ProductConstants.LowerCase.INFO, getPageData(colorVariantPage));
        return colorVariantData;
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

    public Boolean deleteColorVariant(Product product, Long variantId) throws Exception {
        ColorVariant existingColorVariant = colorVariantRepository.findByProductAndVariantId(product, variantId);
        if (existingColorVariant == null) {
            throw new Exception(ExceptionMessageCase.VARIANT_NOT_FOUND);
        }
        colorVariantRepository.deleteByProductAndVariantId(product, variantId);
        return true;
    }
}
