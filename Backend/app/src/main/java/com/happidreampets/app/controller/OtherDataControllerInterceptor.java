package com.happidreampets.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OtherDataControllerInterceptor extends APIController implements HandlerInterceptor {

    @Autowired
    private OtherDataController otherDataController;

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPathVariables(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map) {
            return (Map<String, Object>) attribute;
        }
        return new HashMap<String, Object>();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Boolean isAnimalIdPresent = Boolean.FALSE;
        Boolean isCategoryIdPresent = Boolean.FALSE;
        Boolean isProductIdPresent = Boolean.FALSE;
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            RequestMapping mappingInfo = handlerMethod.getMethod().getAnnotation(RequestMapping.class);

            String[] urlPatterns = mappingInfo.value();

            // Process the API structure as per your requirement
            for (String pattern : urlPatterns) {
                if (pattern.contains("{animalId}")) {
                    isAnimalIdPresent = Boolean.TRUE;
                }
                if (pattern.contains("{categoryId}")) {
                    isCategoryIdPresent = Boolean.TRUE;
                }
                if (pattern.contains("{productId}")) {
                    isProductIdPresent = Boolean.TRUE;
                }
            }
        }

        Boolean isPathVariablePassed = checkPathVariables(request, response, handler, isAnimalIdPresent,
                isCategoryIdPresent, isProductIdPresent);
        return isPathVariablePassed;
    }

    private Boolean checkPathVariables(HttpServletRequest request, HttpServletResponse response, Object handler,
            Boolean isAnimalIdPresent, Boolean isCategoryIdPresent, Boolean isProductIdPresent)
            throws IOException {
        Map<String, Object> pathVariables = getPathVariables(request);

        Object animalIdObj = pathVariables.getOrDefault("animalId", null);
        Object categoryIdObj = pathVariables.getOrDefault("categoryId", null);
        FailureResponse failureResponse = new FailureResponse();

        Category category = null;
        Animal animal = null;

        if (isAnimalIdPresent) {
            if (animalIdObj == null || !Utils.isStringLong(animalIdObj.toString())) {
                failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                failureResponse
                        .setData(new JSONObject().put(ProductConstants.LowerCase.FIELD, "animal_id").put(
                                LowerCase.MESSAGE,
                                "Missing Animal Id"));
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");
                response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                response.getWriter().flush();
                return false;
            }
            Long animalId = Long.valueOf(animalIdObj.toString());
            animal = getAnimalCRUD().getAnimal(animalId);
            if (animal == null) {
                failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                failureResponse
                        .setData(new JSONObject().put(ProductConstants.LowerCase.FIELD, "animal").put(LowerCase.MESSAGE,
                                "Animal Id is Invalid"));
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentType("application/json");
                response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                response.getWriter().flush();
                return false;
            }
        }
        if (isCategoryIdPresent || isProductIdPresent) {
            if (categoryIdObj == null || !Utils.isStringLong(categoryIdObj.toString())) {
                failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                failureResponse.setData(
                        new JSONObject().put(ProductConstants.LowerCase.FIELD, "category_id").put(LowerCase.MESSAGE,
                                "Missing Category Id"));
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");
                response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                response.getWriter().flush();
                return false;
            }
            Long categoryId = Long.valueOf(categoryIdObj.toString());

            category = isAnimalIdPresent ? getCategoryCRUD().getCategoryDetail(animal, categoryId)
                    : getCategoryCRUD().getCategoryDetailBasedOnId(categoryId);

            if (isCategoryIdPresent && category == null) {
                failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                failureResponse.setData(
                        new JSONObject().put(ProductConstants.LowerCase.FIELD, "category").put(LowerCase.MESSAGE,
                                "Category Id is Invalid"));
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentType("application/json");
                response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                response.getWriter().flush();
                return false;
            }
        }

        Object productIdObj = pathVariables.getOrDefault("productId", null);

        if (isProductIdPresent) {
            if (productIdObj != null) {
                if (!Utils.isStringLong(productIdObj.toString())) {
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(new JSONObject().put(ProductConstants.LowerCase.FIELD, "product_id").put(
                                    LowerCase.MESSAGE,
                                    "Missing Product Id"));
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.setContentType("application/json");
                    response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                    response.getWriter().flush();
                    return false;
                }
                Long productId = Long.valueOf(productIdObj.toString());
                Product product = getProductCRUD().getProduct(productId, category);
                if (null == product) {
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(
                            new JSONObject().put(ProductConstants.LowerCase.FIELD, "product").put(LowerCase.MESSAGE,
                                    "Product Id is Invalid for given Category"));
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    response.setContentType("application/json");
                    response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                    response.getWriter().flush();
                    return false;
                }
                otherDataController.setCurrentProduct(product);
            }
        }

        otherDataController.setCurrentAnimal(animal);
        otherDataController.setCurrentCategory(category);

        return true;
    }

}
