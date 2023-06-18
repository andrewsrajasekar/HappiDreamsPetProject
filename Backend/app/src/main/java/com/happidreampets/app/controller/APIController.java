package com.happidreampets.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.AnimalConstants;
import com.happidreampets.app.database.crud.AnimalCRUD;
import com.happidreampets.app.database.crud.CartCRUD;
import com.happidreampets.app.database.crud.CategoryCRUD;
import com.happidreampets.app.database.crud.ColorVariantCRUD;
import com.happidreampets.app.database.crud.OrderHistoryCRUD;
import com.happidreampets.app.database.crud.ProductCRUD;
import com.happidreampets.app.database.crud.SizeVariantCRUD;
import com.happidreampets.app.database.crud.TopCategoriesCRUD;
import com.happidreampets.app.database.crud.TopProductsCRUD;
import com.happidreampets.app.database.crud.UserCRUD;
import com.happidreampets.app.database.crud.WeightVariantCRUD;
import com.happidreampets.app.constants.ControllerConstants.LoggerCase;
import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.ControllerConstants.MessageCase;
import com.happidreampets.app.constants.ControllerConstants.OtherCase;
import com.happidreampets.app.constants.ControllerConstants.SnakeCase;
import com.happidreampets.app.constants.ControllerConstants.ExceptionMessageCase;

import jakarta.ws.rs.core.MediaType;

public class APIController {
    private static final Logger LOG = Logger.getLogger(ProductController.class.getName());

    public enum ERROR_CODES {
        INVALID_INPUT("INVALID_INPUT"),
        INVALID_DATA("INVALID_DATA"),
        MANDATORY_MISSING("MANDATORY_MISSING"),
        INVALID_PATH_VARIABLE("INVALID_PATH_VARIABLE"),
        INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
        MISSING_REQUIRED_FIELDS("MISSING_REQUIRED_FIELDS"),
        DUPLICATE_RESOURCE("DUPLICATE_RESOURCE"),
        RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
        MAXIMUM_RESOURCE_CREATED("MAXIMUM_RESOURCE_CREATED"),
        UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS"),
        FORBIDDEN_OPERATION("FORBIDDEN_OPERATION"),
        REQUEST_TIMEOUT("REQUEST_TIMEOUT"),
        RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED"),
        SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE");

        private String errorName;

        private ERROR_CODES(String errorName) {
            this.errorName = errorName;
        }

        @Override
        public String toString() {
            return this.errorName;
        }
    }

    @Autowired
    private AnimalCRUD animalCRUD;

    @Autowired
    private CartCRUD cartCRUD;

    @Autowired
    private CategoryCRUD categoryCRUD;

    @Autowired
    private ColorVariantCRUD colorVariantCRUD;

    @Autowired
    private OrderHistoryCRUD orderHistoryCRUD;

    @Autowired
    private ProductCRUD productCRUD;

    @Autowired
    private SizeVariantCRUD sizeVariantCRUD;

    @Autowired
    private TopCategoriesCRUD topCategoriesCRUD;

    @Autowired
    private TopProductsCRUD topProductsCRUD;

    @Autowired
    private UserCRUD userCRUD;

    @Autowired
    private WeightVariantCRUD weighVariantCRUD;

    protected AnimalCRUD getAnimalCRUD() {
        return animalCRUD;
    }

    protected CartCRUD getCartCRUD() {
        return cartCRUD;
    }

    protected CategoryCRUD getCategoryCRUD() {
        return categoryCRUD;
    }

    protected ColorVariantCRUD getColorVariantCRUD() {
        return colorVariantCRUD;
    }

    protected OrderHistoryCRUD getOrderHistoryCRUD() {
        return orderHistoryCRUD;
    }

    protected ProductCRUD getProductCRUD() {
        return productCRUD;
    }

    protected SizeVariantCRUD getSizeVariantCRUD() {
        return sizeVariantCRUD;
    }

    protected TopCategoriesCRUD getTopCategoriesCRUD() {
        return topCategoriesCRUD;
    }

    protected TopProductsCRUD getTopProductsCRUD() {
        return topProductsCRUD;
    }

    protected UserCRUD getUserCRUD() {
        return userCRUD;
    }

    protected WeightVariantCRUD getWeighVariantCRUD() {
        return weighVariantCRUD;
    }

    protected class SuccessResponse {
        private String status = ProductConstants.LowerCase.SUCCESS;
        private JSONObject data;
        private JSONObject responseData = null;
        private HashMap<String, String> headers;
        private MultiValueMap<String, String> apiHeaders = new LinkedMultiValueMap<>();
        private HttpStatus apiResponseStatus = HttpStatus.OK;

        public HttpStatus getApiResponseStatus() {
            return apiResponseStatus;
        }

        public void setApiResponseStatus(HttpStatus apiResponseStatus) {
            if (apiResponseStatus.value() >= 200 && apiResponseStatus.value() <= 299) {
                this.apiResponseStatus = apiResponseStatus;
            }
        }

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(HashMap<String, String> headers) {
            this.headers = headers;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                apiHeaders.add(entry.getKey(), entry.getValue());
            }
        }

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }

        public JSONObject getResponseData() {
            return responseData;
        }

        public void setResponseData(JSONObject responseData) {
            this.responseData = responseData;
        }

        protected ResponseEntity<String> getResponse() {
            JSONObject response = new JSONObject();
            response.put(LowerCase.STATUS, status);
            response.put(ProductConstants.LowerCase.DATA, data);
            apiHeaders.set(OtherCase.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(
                    getResponseData() != null ? getResponseData().toString() : response.toString(), apiHeaders,
                    apiResponseStatus);
        }
    }

    protected class FailureResponse {
        private ERROR_CODES code = ERROR_CODES.INTERNAL_SERVER_ERROR;
        private String message = MessageCase.INTERNAL_SERVER_ERROR_OCCURED;
        private String status = LowerCase.ERROR;
        private JSONObject data = new JSONObject();
        private HashMap<String, String> headers;
        private MultiValueMap<String, String> apiHeaders = new LinkedMultiValueMap<>();
        private HttpStatus apiResponseStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        public FailureResponse() {
        }

        public HttpStatus getApiResponseStatus() {
            return apiResponseStatus;
        }

        public void setApiResponseStatus(HttpStatus apiResponseStatus) {
            if (apiResponseStatus.value() >= 300 && apiResponseStatus.value() <= 599) {
                this.apiResponseStatus = apiResponseStatus;
            }
        }

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(HashMap<String, String> headers) {
            this.headers = headers;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                apiHeaders.add(entry.getKey(), entry.getValue());
            }
        }

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }

        public ERROR_CODES getCode() {
            return code;
        }

        public void setCode(ERROR_CODES code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        protected ResponseEntity<String> getResponse() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, code.toString());
            responseData.put(LowerCase.MESSAGE, message);
            responseData.put(LowerCase.ERRORS, data);
            apiHeaders.add(OtherCase.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(responseData.toString(), apiHeaders, apiResponseStatus);
        }

        protected ResponseEntity<String> throwInvalidInput() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_INPUT);
            responseData.put(LowerCase.MESSAGE, "Invalid input provided. Please check the request parameters.");
            responseData.put(LowerCase.ERRORS, data);
            apiHeaders.add(OtherCase.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(responseData.toString(), apiHeaders, apiResponseStatus);
        }

        protected ResponseEntity<String> throwInvalidPathVariable() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_PATH_VARIABLE);
            responseData.put(LowerCase.MESSAGE, "Invalid path variable");
            responseData.put(LowerCase.ERRORS, data);
            apiHeaders.add(OtherCase.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(responseData.toString(), apiHeaders, apiResponseStatus);
        }

        protected ResponseEntity<String> throwInvalidBodyInput() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_DATA);
            responseData.put(LowerCase.MESSAGE, "Invalid data input");
            responseData.put(LowerCase.ERRORS, data);
            apiHeaders.add(OtherCase.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(responseData.toString(), apiHeaders, apiResponseStatus);
        }

        protected ResponseEntity<String> throwNotFoundForIds() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.RESOURCE_NOT_FOUND);
            responseData.put(LowerCase.MESSAGE, "Invalid Id");
            responseData.put(LowerCase.ERRORS, data);
            apiHeaders.add(OtherCase.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(responseData.toString(), apiHeaders, apiResponseStatus);
        }

        protected ResponseEntity<String> throwMaximumResourceCreated() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.MAXIMUM_RESOURCE_CREATED);
            responseData.put(LowerCase.MESSAGE,
                    "Maximum number of resource created/allowed has been exceeded. Cannot create new record.");
            responseData.put(LowerCase.ERRORS, data);
            apiHeaders.add(OtherCase.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            return new ResponseEntity<String>(responseData.toString(), apiHeaders, apiResponseStatus);
        }
    }

    public class ProductControllerExceptions {
        private Exception exception;
        private JSONObject data;

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public ResponseEntity<String> returnResponseBasedOnException() {
            FailureResponse failureResponse = new FailureResponse();
            if (getException().getMessage() == null) {
                return failureResponse.getResponse();
            }
            LOG.log(Level.SEVERE, LoggerCase.EXCEPTION + getException().getMessage());
            switch (exception.getMessage()) {
                case ProductConstants.ExceptionMessageCase.PAGE_GREATER_THAN_ZERO:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject().put(LowerCase.PARAMETER, ProductConstants.LowerCase.PAGE)
                            .put(LowerCase.MESSAGE, MessageCase.MUST_BE_GREATER_THAN_0));
                    return failureResponse.throwInvalidInput();
                case ProductConstants.ExceptionMessageCase.PER_PAGE_GREATER_THAN_ZERO:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(
                            new JSONObject().put(LowerCase.PARAMETER, ProductConstants.SnakeCase.PER_PAGE).put(
                                    LowerCase.MESSAGE,
                                    MessageCase.MUST_BE_GREATER_THAN_0));
                    return failureResponse.throwInvalidInput();
                case ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.CapitalizationCase.PRODUCT)
                            .put(LowerCase.MESSAGE, "Product Id is Invalid"));
                    return failureResponse.throwNotFoundForIds();
                case ProductConstants.ExceptionMessageCase.MISSING_PRODUCT_FIELD_FOR_CREATE:
                case ProductConstants.ExceptionMessageCase.MISSING_PRODUCT_FIELD_FOR_UPDATE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case ProductConstants.ExceptionMessageCase.INVALID_IMAGE_FORMAT:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "Only JPG, JPEG, and PNG files are allowed."));
                    return failureResponse.throwInvalidBodyInput();
                case ProductConstants.ExceptionMessageCase.MAXIMUM_IMAGES_FOR_PRODUCT_REACHED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "Maximum Number of Images that can be created for a Product Reached"));
                    return failureResponse.throwMaximumResourceCreated();
                case ProductConstants.ExceptionMessageCase.NO_IMAGE_ASSOCIATED_WITH_PRODUCT:
                case ProductConstants.ExceptionMessageCase.IMAGE_ID_NOT_FOUND_FOR_THE_PRODUCT:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setCode(ERROR_CODES.RESOURCE_NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                            .put(LowerCase.MESSAGE,
                                    "Given Image Id is not found for the Product"));
                    failureResponse.setMessage("Invalid Image Id");
                    return failureResponse.getResponse();
                case ProductConstants.ExceptionMessageCase.PRODUCT_NOT_FOUND:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Invalid Product Id"));
                    return failureResponse.throwInvalidPathVariable();
                case ExceptionMessageCase.INTERNAL_SERVER_ERROR:
                    return failureResponse.getResponse();
                default:
                    return failureResponse.getResponse();
            }
        }
    }

    public class OtherDataControllerExceptions {
        private Exception exception;
        private JSONObject data;

        public JSONObject getData() {
            return data;
        }

        public void setData(JSONObject data) {
            this.data = data;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public ResponseEntity<String> returnResponseBasedOnException() {
            FailureResponse failureResponse = new FailureResponse();
            if (getException().getMessage() == null) {
                return failureResponse.getResponse();
            }
            LOG.log(Level.SEVERE, LoggerCase.EXCEPTION + getException().getMessage());
            switch (exception.getMessage()) {
                case ProductConstants.ExceptionMessageCase.PAGE_GREATER_THAN_ZERO:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject().put(LowerCase.PARAMETER, ProductConstants.LowerCase.PAGE)
                            .put(LowerCase.MESSAGE, MessageCase.MUST_BE_GREATER_THAN_0));
                    return failureResponse.throwInvalidInput();
                case ProductConstants.ExceptionMessageCase.PER_PAGE_GREATER_THAN_ZERO:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(
                            new JSONObject().put(LowerCase.PARAMETER, ProductConstants.SnakeCase.PER_PAGE).put(
                                    LowerCase.MESSAGE,
                                    MessageCase.MUST_BE_GREATER_THAN_0));
                    return failureResponse.throwInvalidInput();
                case AnimalConstants.ExceptionMessageCase.INVALID_ANIMAL_ID:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.CapitalizationCase.PRODUCT)
                            .put(LowerCase.MESSAGE, "Animal Id is Invalid"));
                    return failureResponse.throwNotFoundForIds();
                case ExceptionMessageCase.INTERNAL_SERVER_ERROR:
                    return failureResponse.getResponse();
                default:
                    return failureResponse.getResponse();
            }
        }
    }
}
