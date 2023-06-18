package com.happidreampets.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProductControllerInterceptor extends APIController implements HandlerInterceptor {
    @Autowired
    private ProductController productController;

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
        Boolean isPathVariablePassed = checkPathVariables(request, response, handler);
        return isPathVariablePassed;
    }

    private Boolean checkPathVariables(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        Map<String, Object> pathVariables = getPathVariables(request);

        Object animalIdObj = pathVariables.getOrDefault("animalId", null);
        Object categoryIdObj = pathVariables.getOrDefault("categoryId", null);
        FailureResponse failureResponse = new FailureResponse();

        if (animalIdObj == null) {
            failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
            failureResponse
                    .setData(new JSONObject().put(ProductConstants.LowerCase.FIELD, "animal_id").put(LowerCase.MESSAGE,
                            "Missing Animal Id"));
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
            response.getWriter().flush();
            return false;
        }

        if (categoryIdObj == null) {
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

        Long animalId = Long.valueOf(animalIdObj.toString());
        Long categoryId = Long.valueOf(categoryIdObj.toString());
        Animal animal = getAnimalCRUD().getAnimal(animalId);
        Category category = getCategoryCRUD().getCategoryDetail(categoryId);
        if (animal == null) {
            failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
            failureResponse
                    .setData(new JSONObject().put(ProductConstants.LowerCase.FIELD, "animal").put(LowerCase.MESSAGE,
                            "Animal Id is Invalid"));
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
            response.getWriter().flush();
            return false;
        }
        if (category == null) {
            failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
            failureResponse.setData(
                    new JSONObject().put(ProductConstants.LowerCase.FIELD, "category").put(LowerCase.MESSAGE,
                            "Category Id is Invalid"));
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
            response.getWriter().flush();
            return false;
        }

        Object productIdObj = pathVariables.getOrDefault("productId", null);
        if (productIdObj != null) {
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
            productController.setCurrentProduct(product);
        }

        productController.setCurrentAnimal(animal);
        productController.setCurrentCategory(category);

        return true;
    }

}
