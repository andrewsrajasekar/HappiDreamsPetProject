package com.happidreampets.app.database.crud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.CartConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.TopCategoriesConstants;
import com.happidreampets.app.constants.TopProductsConstants.MessageCase;
import com.happidreampets.app.constants.TopProductsConstants.SnakeCase;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.TopProducts;
import com.happidreampets.app.database.model.TopProducts.TOPPRODUCTSCOLUMN;
import com.happidreampets.app.database.repository.TopProductsRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

@Component
public class TopProductsCRUD {

    @Autowired
    private TopProductsRepository topProductsRepository;

    @Autowired
    private ProductCRUD productCRUD;

    Integer topProductsLimit;

    public TopProductsCRUD(@Value("${top.products.limit}") Integer topProductsLimit) {
        this.topProductsLimit = topProductsLimit;
    }

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private TOPPRODUCTSCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(TOPPRODUCTSCOLUMN.class, dbFilter.getSortColumn().toString())) {
                TOPPRODUCTSCOLUMN enumValue = TOPPRODUCTSCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<TopProducts> data) {
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
                List<TopProducts> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<TopProducts> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<TopProducts> topProductsPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, topProductsPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, topProductsPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, topProductsPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, topProductsPage.hasNext());
        return pageData;
    }

    public JSONObject getTopProductsDetails() {
        JSONObject topProductsData = new JSONObject();
        Sort sort = null;
        Iterable<TopProducts> topProductsIterable = null;
        if (getDbFilter() != null) {
            if (checkAndGetColumnName() != null) {
                sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
            }
            Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
            Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
            Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort)
                    : PageRequest.of(startIndex, limit);
            Page<TopProducts> topProductsPage = topProductsRepository.findAll(pageable);
            topProductsIterable = topProductsPage.getContent();
            topProductsData.put(ProductConstants.LowerCase.INFO, getPageData(topProductsPage));
        } else {
            topProductsIterable = topProductsRepository.findAll();
        }

        topProductsData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(topProductsIterable).get(ProductConstants.LowerCase.DATA));

        return topProductsData;
    }

    public JSONObject getTopProductDetailsInJSON(Long topProductId) {
        TopProducts topProduct = topProductsRepository.findById(topProductId).orElse(null);
        return topProduct != null ? topProduct.toJSON() : null;
    }

    public TopProducts createTopProducts(Product product, Integer orderNumber) throws Exception {
        List<TopProducts> allTopProducts = topProductsRepository.findAllSortedDataWithOrderNumberAsc();
        if (allTopProducts.size() >= topProductsLimit) {
            throw new Exception(
                    TopCategoriesConstants.CapitalizationCase.ALREADY + CartConstants.LowerCase.GAP + topProductsLimit
                            + CartConstants.LowerCase.GAP
                            + MessageCase.PRODUCTS_EXISTING_IN_TOPPRODUCTS_WHICH_IS_MAXMIMUM);
        }
        TopProducts existingTopProduct = topProductsRepository.findByProduct(product);
        if (existingTopProduct != null) {
            throw new Exception(ProductConstants.ExceptionMessageCase.PRODUCT_ALREADY_EXISTS);
        }

        TopProducts newTopProduct = new TopProducts();
        newTopProduct.setProduct(product);
        newTopProduct.setOrder_number(orderNumber);
        Boolean isOrderNumberExist = false;
        Integer highestOrderNumber = 0;
        for (TopProducts topProducts : allTopProducts) {
            if (topProducts.getOrder_number() == orderNumber) {
                isOrderNumberExist = true;
            }
            if (topProducts.getOrder_number() > highestOrderNumber) {
                highestOrderNumber = topProducts.getOrder_number();
            }
        }
        if (isOrderNumberExist) {
            for (TopProducts topProducts : allTopProducts) {
                if (topProducts.getOrder_number() < orderNumber) {
                    topProductsRepository.save(topProducts);
                } else if (topProducts.getOrder_number() == orderNumber) {
                    topProductsRepository.save(newTopProduct);
                    topProducts.setOrder_number(topProducts.getOrder_number() + 1);
                    topProductsRepository.save(topProducts);
                } else {
                    topProducts.setOrder_number(topProducts.getOrder_number() + 1);
                    topProductsRepository.save(topProducts);
                }
            }
        } else {
            newTopProduct.setOrder_number(highestOrderNumber + 1);
            topProductsRepository.save(newTopProduct);
        }
        return newTopProduct;
    }

    public List<TopProducts> updateTopProductOrder(Long id, Integer orderNumber) throws Exception {
        TopProducts topProductRow = topProductsRepository.findById(id).orElse(null);
        if (topProductRow == null) {
            throw new Exception(TopCategoriesConstants.ExceptionMessageCase.GIVEN_ID_NOT_FOUND);
        }
        topProductRow.setOrder_number(orderNumber);

        List<TopProducts> allTopProducts = topProductsRepository.findAllSortedDataWithOrderNumberAsc();
        Boolean isOrderNumberExist = false;
        Integer highestOrderNumber = 0;
        for (TopProducts topProducts : allTopProducts) {
            if (topProducts.getOrder_number() == orderNumber) {
                isOrderNumberExist = true;
            }
            if (topProducts.getOrder_number() > highestOrderNumber) {
                highestOrderNumber = topProducts.getOrder_number();
            }
        }
        if (isOrderNumberExist) {
            for (TopProducts topProducts : allTopProducts) {
                if (topProducts.getOrder_number() < orderNumber) {
                    topProductsRepository.save(topProducts);
                } else if (topProducts.getOrder_number() == orderNumber) {
                    topProductsRepository.save(topProductRow);
                    topProducts.setOrder_number(topProducts.getOrder_number() + 1);
                    topProductsRepository.save(topProducts);
                } else {
                    topProducts.setOrder_number(topProducts.getOrder_number() + 1);
                    topProductsRepository.save(topProducts);
                }
            }
        } else {
            topProductRow.setOrder_number(highestOrderNumber + 1);
            topProductsRepository.save(topProductRow);
        }
        return topProductsRepository.findAllSortedDataWithOrderNumberAsc();
    }

    public Boolean deleteTopProductById(Long id) throws Exception {
        TopProducts topProductRow = topProductsRepository.findById(id).orElse(null);
        if (topProductRow == null) {
            throw new Exception(TopCategoriesConstants.ExceptionMessageCase.GIVEN_ID_NOT_FOUND);
        }
        topProductsRepository.delete(topProductRow);
        return true;
    }

    public void clearAndCreateTopProducts(JSONArray data) throws Exception {
        topProductsRepository.deleteAll();
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            jsonObjectList.add(data.getJSONObject(i));
        }

        jsonObjectList.sort(Comparator.comparingInt(o -> o.getInt(SnakeCase.ORDER_NUMBER)));

        int index = 1;
        for (JSONObject jsonObject : jsonObjectList) {
            createTopProducts(
                    productCRUD
                            .getProduct(Long.valueOf(jsonObject.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())),
                    index);
            index++;
        }
    }

    public JSONObject validateBodyDataForSingleCreate(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(ProductConstants.SnakeCase.PRODUCT_ID)) {
            missingField = ProductConstants.SnakeCase.PRODUCT_ID;
            message = message.replace(ProductConstants.LoggerCase.ARG0, ProductConstants.SnakeCase.PRODUCT_ID);
        } else if (!body.has(SnakeCase.ORDER_NUMBER)) {
            missingField = SnakeCase.ORDER_NUMBER;
            message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.ORDER_NUMBER);
        } else {
            if (body.get(SnakeCase.ORDER_NUMBER) instanceof Long) {
                code = null;
                message = ProductConstants.MessageCase.THE_VALUE_IS_TOO_HIGH;
                missingField = SnakeCase.ORDER_NUMBER;
            } else if (!(body.get(SnakeCase.ORDER_NUMBER) instanceof Integer)) {
                code = null;
                message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = SnakeCase.ORDER_NUMBER;
            } else if (!(body.get(ProductConstants.SnakeCase.PRODUCT_ID) instanceof Long)) {
                code = null;
                message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = ProductConstants.SnakeCase.PRODUCT_ID;
            } else if (productCRUD
                    .getProduct(Long.valueOf(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())) == null) {
                code = null;
                message = ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID;
                missingField = ProductConstants.SnakeCase.PRODUCT_ID;
            } else {
                isSuccess = Boolean.TRUE;
            }

        }
        response.put(ProductConstants.LowerCase.SUCCESS, isSuccess);
        if (!isSuccess) {
            response.put(ProductConstants.LowerCase.DATA,
                    new JSONObject().put(ProductConstants.LowerCase.FIELD, missingField)
                            .put(ProductConstants.LowerCase.CODE, code)
                            .put(ProductConstants.LowerCase.MESSAGE, message));
        }
        return response;
    }

    public JSONObject validateBodyDataForBulkCreate(JSONArray bodyArray) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        Integer index = 0;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        Iterator<Object> bodyArrayIter = bodyArray.iterator();
        while (bodyArrayIter.hasNext()) {
            JSONObject body = (JSONObject) bodyArrayIter.next();
            index = index + 1;
            if (!body.has(ProductConstants.SnakeCase.PRODUCT_ID)) {
                missingField = ProductConstants.SnakeCase.PRODUCT_ID;
                message = message.replace(ProductConstants.LoggerCase.ARG0, ProductConstants.SnakeCase.PRODUCT_ID);
            } else if (!body.has(SnakeCase.ORDER_NUMBER)) {
                missingField = SnakeCase.ORDER_NUMBER;
                message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.ORDER_NUMBER);
            } else {
                if (body.get(SnakeCase.ORDER_NUMBER) instanceof Long) {
                    code = null;
                    message = ProductConstants.MessageCase.THE_VALUE_IS_TOO_HIGH;
                    missingField = SnakeCase.ORDER_NUMBER;
                } else if (!(body.get(SnakeCase.ORDER_NUMBER) instanceof Integer)) {
                    code = null;
                    message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                    missingField = SnakeCase.ORDER_NUMBER;
                } else if (!(body.get(ProductConstants.SnakeCase.PRODUCT_ID) instanceof Long)) {
                    code = null;
                    message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                    missingField = ProductConstants.SnakeCase.PRODUCT_ID;
                } else if (productCRUD
                        .getProduct(Long.valueOf(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())) == null) {
                    code = null;
                    message = ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID;
                    missingField = ProductConstants.SnakeCase.PRODUCT_ID;
                } else {
                    isSuccess = Boolean.TRUE;
                }
            }
            if (!isSuccess) {
                message += " in the Position " + index + " of the body";
                break;
            }
        }
        response.put(ProductConstants.LowerCase.SUCCESS, isSuccess);
        if (!isSuccess) {
            response.put(ProductConstants.LowerCase.DATA,
                    new JSONObject().put(ProductConstants.LowerCase.FIELD, missingField)
                            .put(ProductConstants.LowerCase.CODE, code)
                            .put(ProductConstants.LowerCase.MESSAGE, message));
        }
        return response;
    }

}
