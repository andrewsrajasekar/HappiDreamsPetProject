package com.happidreampets.app.database.crud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.AnimalConstants;
import com.happidreampets.app.constants.CartConstants;
import com.happidreampets.app.constants.CategoryConstants;
import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.TopCategoriesConstants;
import com.happidreampets.app.constants.TopCategoriesConstants.CapitalizationCase;
import com.happidreampets.app.constants.TopCategoriesConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.TopCategoriesConstants.MessageCase;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.TopCategories;
import com.happidreampets.app.database.repository.TopCategoriesRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

import jakarta.transaction.Transactional;

@Component
public class TopCategoriesCRUD {

    @Autowired
    private TopCategoriesRepository topCategoriesRepository;

    @Autowired
    private ProductCRUD productCRUD;

    @Value("${top.categories.limit}")
    private Integer topCategoriesLimit;

    @Value("${top.categories.products.limit}")
    private Integer topCategoriesProductsLimit;

    private Boolean fromController = Boolean.FALSE;

    public Boolean getFromController() {
        return fromController;
    }

    public void setFromController(Boolean fromController) {
        this.fromController = fromController;
    }

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private JSONObject getDataInRequiredFormat(Iterable<TopCategories> data) {
        JSONObject responseData = new JSONObject();
        responseData.put(ProductConstants.LowerCase.DATA, JSONObject.NULL);
        List<TopCategories> responseList = new ArrayList<>();
        data.forEach(responseList::add);
        if (getDbFilter() != null) {
            if (getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
                responseData.put(ProductConstants.LowerCase.DATA, modifyTopCategoriesFromListAsJSON(responseList));
            } else if (getDbFilter().getFormat().equals(DATAFORMAT.POJO)) {
                responseData.put(ProductConstants.LowerCase.DATA, modifyTopCategoriesFromList(responseList));
            }
        } else {
            responseData.put(ProductConstants.LowerCase.DATA, modifyTopCategoriesFromList(responseList));
        }

        return responseData;
    }

    public JSONObject getTopCategoriesDetails() {
        JSONObject topCategoriesData = new JSONObject();
        Iterable<TopCategories> topCategoriesIterable = topCategoriesRepository.findAll();
        topCategoriesData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(topCategoriesIterable).get(ProductConstants.LowerCase.DATA));
        return topCategoriesData;
    }

    public HashMap<String, Object> getTopCategory(Category category) {
        List<TopCategories> topCategoriesData = topCategoriesRepository.findByCategory(category);
        return !topCategoriesData.isEmpty() ? modifyTopCategoriesFromList(topCategoriesData).get(0) : null;
    }

    public JSONObject getTopCategoryAsJSON(Category category) {
        List<TopCategories> topCategoriesData = topCategoriesRepository.findByCategory(category);
        return !topCategoriesData.isEmpty() ? (JSONObject) modifyTopCategoriesFromListAsJSON(topCategoriesData).get(0)
                : null;
    }

    private List<HashMap<String, Object>> modifyTopCategoriesFromList(List<TopCategories> data) {
        List<HashMap<String, Object>> responseData = new ArrayList<>();
        JSONObject categoryIdVsCategory = new JSONObject();
        JSONObject categoryIdVsProducts = new JSONObject();
        for (TopCategories topCategory : data) {
            if (!categoryIdVsCategory.has(topCategory.getCategory().getId().toString())) {
                categoryIdVsCategory.put(topCategory.getCategory().getId().toString(), topCategory.getCategory());
            }
            JSONArray products = new JSONArray();
            if (categoryIdVsProducts.has(topCategory.getCategory().getId().toString())) {
                products = categoryIdVsProducts.getJSONArray(topCategory.getCategory().getId().toString());
            }
            products.put(topCategory.getProduct());
            categoryIdVsProducts.put(topCategory.getCategory().getId().toString(), products);
        }
        for (String categoryId : categoryIdVsProducts.keySet()) {
            HashMap<String, Object> response = new HashMap<>();
            response.put(CategoryConstants.LowerCase.CATEGORY, categoryIdVsCategory.get(categoryId));
            response.put(ProductConstants.LowerCase.PRODUCTS, categoryIdVsProducts.getJSONArray(categoryId));
            responseData.add(response);
        }
        return responseData;
    }

    private JSONArray modifyTopCategoriesFromListAsJSON(List<TopCategories> data) {
        JSONArray responseData = new JSONArray();
        JSONObject categoryIdVsCategory = new JSONObject();
        JSONObject categoryIdVsProducts = new JSONObject();
        for (TopCategories topCategory : data) {
            if (!categoryIdVsCategory.has(topCategory.getCategory().getId().toString())) {
                categoryIdVsCategory.put(topCategory.getCategory().getId().toString(),
                        topCategory.getCategory().toJSON(true, true));
            }
            JSONArray products = new JSONArray();
            if (categoryIdVsProducts.has(topCategory.getCategory().getId().toString())) {
                products = categoryIdVsProducts.getJSONArray(topCategory.getCategory().getId().toString());
            }
            products.put(topCategory.getProduct().toJSON(true, true));
            categoryIdVsProducts.put(topCategory.getCategory().getId().toString(), products);
        }
        for (String categoryId : categoryIdVsProducts.keySet()) {
            JSONObject response = new JSONObject();
            response.put(CategoryConstants.LowerCase.CATEGORY, categoryIdVsCategory.get(categoryId));
            response.put(ProductConstants.LowerCase.PRODUCTS, categoryIdVsProducts.getJSONArray(categoryId));
            responseData.put(response);
        }
        return responseData;
    }

    public void checkTopCategoryProductCreate(Category category, List<Long> productIds) throws Exception {
        List<TopCategories> existingCategoryProductsData = topCategoriesRepository
                .findAllSortedCategoryDataWithOrderNumberAsc(category);
        if (existingCategoryProductsData.isEmpty()) {
            Long categoriesExisting = topCategoriesRepository.countDistinctCategories();
            if (categoriesExisting >= topCategoriesLimit) {
                throw new Exception(getFromController()
                        ? MessageCase.CATEGORIES_EXISTING_IN_TOPCATEGORIES_WHICH_IS_MAXIMUM
                        : CapitalizationCase.ALREADY
                                + CartConstants.LowerCase.GAP + topCategoriesLimit
                                + CartConstants.LowerCase.GAP
                                + TopCategoriesConstants.MessageCase.CATEGORIES_EXISTING_IN_TOPCATEGORIES_WHICH_IS_MAXIMUM);
            }
        }
        if (existingCategoryProductsData.size() + productIds.size() > topCategoriesProductsLimit) {
            throw new Exception(getFromController()
                    ? MessageCase.PRODUCTS_IN_GIVEN_CATEGORY_IN_TOPCATEGORIES_WHICH_WILL_BE_EXCEEDED
                    : TopCategoriesConstants.MessageCase.MAXIMUM_OF
                            + topCategoriesProductsLimit
                            + CartConstants.LowerCase.GAP
                            + TopCategoriesConstants.MessageCase.PRODUCTS_IN_GIVEN_CATEGORY_IN_TOPCATEGORIES_WHICH_WILL_BE_EXCEEDED);
        }
        for (TopCategories topCategories : existingCategoryProductsData) {
            if (productIds.contains(topCategories.getProduct().getId())) {
                throw new Exception(getFromController()
                        ? ProductConstants.ExceptionMessageCase.PRODUCT_OF_ID_ARG0_ALREADY_EXISTS
                                .replace(ProductConstants.LoggerCase.ARG0,
                                        topCategories.getProduct().getId().toString())
                                + ControllerConstants.SpecialCharacter.UNDERSCORE
                                + ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION
                        : ProductConstants.ExceptionMessageCase.PRODUCT_OF_ID_ARG0_ALREADY_EXISTS
                                .replace(ProductConstants.LoggerCase.ARG0,
                                        topCategories.getProduct().getId().toString()));
            }
        }
    }

    public List<TopCategories> bulkCreateForCategory(Category category, List<Long> productIds) throws Exception {
        checkTopCategoryProductCreate(category, productIds);
        for (Long productId : productIds) {
            Product product = productCRUD.getProduct(productId);
            if (product == null) {
                throw new Exception(getFromController()
                        ? ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID_HYPHEN_ARG0
                                .replace(ProductConstants.LoggerCase.ARG0, productId.toString())
                                + ControllerConstants.SpecialCharacter.UNDERSCORE
                                + ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION
                        : ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID_HYPHEN_ARG0
                                .replace(ProductConstants.LoggerCase.ARG0, productId.toString()));
            }
        }
        List<TopCategories> result = new ArrayList<>();
        for (Long productId : productIds) {
            Product product = productCRUD.getProduct(productId);
            TopCategories data = createTopCategoryProduct(category, product, -1);
            result.add(data);
        }
        return result;
    }

    public TopCategories singleCreateForCategory(Category category, Long productId)
            throws Exception {
        return createTopCategoryProduct(category, productCRUD.getProduct(productId), -1);
    }

    public TopCategories createTopCategoryProduct(Category category, Product product, Integer orderNumber)
            throws Exception {
        List<TopCategories> existingCategoryProductsData = topCategoriesRepository
                .findAllSortedCategoryDataWithOrderNumberAsc(category);
        if (existingCategoryProductsData.isEmpty()) {
            Long categoriesExisting = topCategoriesRepository.countDistinctCategories();
            if (categoriesExisting >= topCategoriesLimit) {
                throw new Exception(getFromController()
                        ? MessageCase.CATEGORIES_EXISTING_IN_TOPCATEGORIES_WHICH_IS_MAXIMUM
                        : CapitalizationCase.ALREADY
                                + CartConstants.LowerCase.GAP + topCategoriesLimit
                                + CartConstants.LowerCase.GAP
                                + TopCategoriesConstants.MessageCase.CATEGORIES_EXISTING_IN_TOPCATEGORIES_WHICH_IS_MAXIMUM);
            }
        }
        if (existingCategoryProductsData.size() >= topCategoriesProductsLimit) {
            throw new Exception(getFromController()
                    ? MessageCase.PRODUCTS_IN_GIVEN_CATEGORY_IN_TOPCATEGORIES_WHICH_WILL_BE_EXCEEDED
                    : TopCategoriesConstants.MessageCase.MAXIMUM_OF
                            + topCategoriesProductsLimit
                            + CartConstants.LowerCase.GAP
                            + TopCategoriesConstants.MessageCase.PRODUCTS_IN_GIVEN_CATEGORY_IN_TOPCATEGORIES_WHICH_WILL_BE_EXCEEDED);
        }
        for (TopCategories topCategories : existingCategoryProductsData) {
            if (topCategories.getProduct().getId().equals(product.getId())) {
                throw new Exception(getFromController()
                        ? ProductConstants.ExceptionMessageCase.PRODUCT_OF_ID_ARG0_ALREADY_EXISTS
                                .replace(ProductConstants.LoggerCase.ARG0,
                                        topCategories.getProduct().getId().toString())
                                + ControllerConstants.SpecialCharacter.UNDERSCORE
                                + ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION
                        : ProductConstants.ExceptionMessageCase.PRODUCT_OF_ID_ARG0_ALREADY_EXISTS
                                .replace(ProductConstants.LoggerCase.ARG0,
                                        topCategories.getProduct().getId().toString()));
            }
        }
        TopCategories newTopCategory = new TopCategories();
        newTopCategory.setCategory(category);
        newTopCategory.setProduct(product);
        newTopCategory.setOrder_number(orderNumber);
        Boolean isOrderNumberExist = false;
        Integer highestOrderNumber = 0;
        for (TopCategories topCategories : existingCategoryProductsData) {
            if (topCategories.getOrder_number() == orderNumber) {
                isOrderNumberExist = true;
            }
            if (topCategories.getOrder_number() > highestOrderNumber) {
                highestOrderNumber = topCategories.getOrder_number();
            }
        }
        if (isOrderNumberExist) {
            for (TopCategories topCategories : existingCategoryProductsData) {
                if (topCategories.getOrder_number() < orderNumber) {
                    topCategoriesRepository.save(topCategories);
                } else if (topCategories.getOrder_number() == orderNumber) {
                    topCategoriesRepository.save(newTopCategory);
                    topCategories.setOrder_number(topCategories.getOrder_number() + 1);
                    topCategoriesRepository.save(topCategories);
                } else {
                    topCategories.setOrder_number(topCategories.getOrder_number() + 1);
                    topCategoriesRepository.save(topCategories);
                }
            }
        } else {
            newTopCategory.setOrder_number(highestOrderNumber + 1);
            topCategoriesRepository.save(newTopCategory);
        }
        return newTopCategory;
    }

    public List<TopCategories> updateTopCategoryProductOrder(Long id, Integer orderNumber) throws Exception {
        TopCategories topCategoryRow = topCategoriesRepository.findById(id).orElse(null);
        if (topCategoryRow == null) {
            throw new Exception(ExceptionMessageCase.GIVEN_ID_NOT_FOUND);
        }
        List<TopCategories> existingCategoryProductsData = topCategoriesRepository
                .findAllSortedCategoryDataWithOrderNumberAsc(topCategoryRow.getCategory());
        topCategoryRow.setOrder_number(orderNumber);
        Boolean isOrderNumberExist = false;
        Integer highestOrderNumber = 0;
        for (TopCategories topCategories : existingCategoryProductsData) {
            if (topCategories.getOrder_number() == orderNumber) {
                isOrderNumberExist = true;
            }
            if (topCategories.getOrder_number() > highestOrderNumber) {
                highestOrderNumber = topCategories.getOrder_number();
            }
        }
        if (isOrderNumberExist) {
            for (TopCategories topCategories : existingCategoryProductsData) {
                if (topCategories.getOrder_number() < orderNumber) {
                    topCategoriesRepository.save(topCategories);
                } else if (topCategories.getOrder_number() == orderNumber) {
                    topCategoriesRepository.save(topCategoryRow);
                    topCategories.setOrder_number(topCategories.getOrder_number() + 1);
                    topCategoriesRepository.save(topCategories);
                } else {
                    topCategories.setOrder_number(topCategories.getOrder_number() + 1);
                    topCategoriesRepository.save(topCategories);
                }
            }
        } else {
            topCategoryRow.setOrder_number(highestOrderNumber + 1);
            topCategoriesRepository.save(topCategoryRow);
        }
        return topCategoriesRepository.findAllSortedCategoryDataWithOrderNumberAsc(topCategoryRow.getCategory());
    }

    public List<TopCategories> updateTopCategoryProductOrderForCategory(Category category,
            List<TopCategories> topCategoriesList) throws Exception {
        Collections.sort(topCategoriesList, (obj1, obj2) -> obj1.getOrder_number().compareTo(obj2.getOrder_number()));
        for (int i = 0; i < topCategoriesList.size(); i++) {
            TopCategories obj = topCategoriesList.get(i);
            obj.setOrder_number(i + 1);
        }
        deleteTopCategoryBasedOnCategory(category);
        topCategoriesList.clear();
        Iterable<TopCategories> savedTopCategoriesIterable = topCategoriesRepository.saveAll(topCategoriesList);
        savedTopCategoriesIterable.forEach(topCategoriesList::add);

        return topCategoriesList;
    }

    @Transactional
    public Boolean deleteTopCategoryBasedOnCategory(Category category) throws Exception {
        List<TopCategories> existingCategoryProductsData = topCategoriesRepository
                .findAllSortedCategoryDataWithOrderNumberAsc(category);
        if (existingCategoryProductsData.isEmpty()) {
            throw new Exception(CategoryConstants.ExceptionMessageCase.CATEGORY_NOT_FOUND);
        }
        topCategoriesRepository.deleteByCategory(category);
        return true;
    }

    public Boolean deleteTopCategoryBasedOnProduct(Product product) throws Exception {
        TopCategories existingCategoryProductsData = topCategoriesRepository.findByProduct(product);
        if (existingCategoryProductsData == null) {
            throw new Exception(ProductConstants.ExceptionMessageCase.PRODUCT_NOT_FOUND);
        }
        topCategoriesRepository.delete(existingCategoryProductsData);
        return true;
    }

    public JSONObject validateBodyDataForCreate(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(AnimalConstants.SnakeCase.ANIMAL_ID)) {
            missingField = AnimalConstants.SnakeCase.ANIMAL_ID;
            message = message.replace(ProductConstants.LoggerCase.ARG0, AnimalConstants.SnakeCase.ANIMAL_ID);
        } else if (!body.has(CategoryConstants.SnakeCase.CATEGORY_ID)) {
            missingField = CategoryConstants.SnakeCase.CATEGORY_ID;
            message = message.replace(ProductConstants.LoggerCase.ARG0, CategoryConstants.SnakeCase.CATEGORY_ID);
        } else if (!body.has(ProductConstants.LowerCase.PRODUCTS)) {
            missingField = ProductConstants.LowerCase.PRODUCTS;
            message = message.replace(ProductConstants.LoggerCase.ARG0, ProductConstants.LowerCase.PRODUCTS);
        } else {
            Boolean isError = Boolean.FALSE;
            if (body.get(ProductConstants.LowerCase.PRODUCTS) instanceof List) {
                List<?> productList = (List<?>) body.get(ProductConstants.LowerCase.PRODUCTS);
                int index = 1;
                for (Object item : productList) {
                    if (!(item instanceof Long || item instanceof Integer)) {
                        code = null;
                        message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                        message += " in the Position " + index + " of the products array";
                        missingField = ProductConstants.LowerCase.PRODUCTS;
                        isError = Boolean.TRUE;
                        break;
                    }
                    index++;
                }
            } else if (body.get(ProductConstants.LowerCase.PRODUCTS) instanceof JSONArray) {
                JSONArray productList = (JSONArray) body.get(ProductConstants.LowerCase.PRODUCTS);
                int index = 1;
                for (Object item : productList) {
                    if (!(item instanceof Long || item instanceof Integer)) {
                        code = null;
                        message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                        message += " in the Position " + index + " of the products array";
                        missingField = ProductConstants.LowerCase.PRODUCTS;
                        isError = Boolean.TRUE;
                        break;
                    }
                    index++;
                }
            } else {
                code = null;
                message = ProductConstants.MessageCase.THE_VALUE_SHOULD_BE_AN_ARRAY_OF_INTEGERS;
                missingField = ProductConstants.LowerCase.PRODUCTS;
                isError = Boolean.TRUE;
            }
            if (!isError) {
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

    public List<Long> checkAndFetchBodyToProductIdList(JSONObject body) {
        List<Long> productIds = new ArrayList<>();
        if (body.get(ProductConstants.LowerCase.PRODUCTS) instanceof List) {
            List<?> productList = (List<?>) body.get(ProductConstants.LowerCase.PRODUCTS);
            for (Object item : productList) {
                if (item instanceof Long || item instanceof Integer) {
                    productIds.add(Long.valueOf(item.toString()));
                }
            }
        } else if (body.get(ProductConstants.LowerCase.PRODUCTS) instanceof JSONArray) {
            JSONArray productList = (JSONArray) body.get(ProductConstants.LowerCase.PRODUCTS);
            for (Object item : productList) {
                if (item instanceof Long || item instanceof Integer) {
                    productIds.add(Long.valueOf(item.toString()));
                }
            }
        }
        return productIds.isEmpty() ? null : productIds;
    }

}
