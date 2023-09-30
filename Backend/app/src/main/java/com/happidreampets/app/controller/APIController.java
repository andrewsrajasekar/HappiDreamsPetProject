package com.happidreampets.app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.SizeVariantConstants;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.constants.WeightVariantConstants;
import com.happidreampets.app.constants.TopCategoriesConstants;
import com.happidreampets.app.constants.TopCategoriesConstants.CapitalizationCase;
import com.happidreampets.app.constants.TopProductsConstants;
import com.happidreampets.app.constants.UserAddressConstants;
import com.happidreampets.app.constants.AnimalConstants;
import com.happidreampets.app.constants.CartConstants;
import com.happidreampets.app.constants.CategoryConstants;
import com.happidreampets.app.constants.ColorVariantConstants;
import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.database.crud.AnimalCRUD;
import com.happidreampets.app.database.crud.CartCRUD;
import com.happidreampets.app.database.crud.CategoryCRUD;
import com.happidreampets.app.database.crud.ColorVariantCRUD;
import com.happidreampets.app.database.crud.OrderHistoryCRUD;
import com.happidreampets.app.database.crud.ProductCRUD;
import com.happidreampets.app.database.crud.SizeVariantCRUD;
import com.happidreampets.app.database.crud.TopCategoriesCRUD;
import com.happidreampets.app.database.crud.TopProductsCRUD;
import com.happidreampets.app.database.crud.UserAddressCRUD;
import com.happidreampets.app.database.crud.UserCRUD;
import com.happidreampets.app.database.crud.WeightVariantCRUD;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.Product.PRODUCTCOLUMN;
import com.happidreampets.app.constants.ControllerConstants.LoggerCase;
import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.ControllerConstants.MessageCase;
import com.happidreampets.app.constants.ControllerConstants.OtherCase;
import com.happidreampets.app.constants.ControllerConstants.SnakeCase;
import com.happidreampets.app.constants.OrderHistoryConstants;
import com.happidreampets.app.constants.ControllerConstants.ExceptionMessageCase;

import jakarta.ws.rs.core.MediaType;

@RestController
public class APIController {
    private static final Logger LOG = Logger.getLogger(APIController.class.getName());

    public enum ERROR_CODES {
        INVALID_INPUT("INVALID_INPUT"),
        INVALID_DATA("INVALID_DATA"),
        INVALID_HEADER("INVALID_HEADER"),
        MANDATORY_MISSING("MANDATORY_MISSING"),
        INVALID_PATH_VARIABLE("INVALID_PATH_VARIABLE"),
        INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
        MISSING_REQUIRED_FIELDS("MISSING_REQUIRED_FIELDS"),
        DUPLICATE_RESOURCE("DUPLICATE_RESOURCE"),
        RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
        MAXIMUM_RESOURCE_CREATED("MAXIMUM_RESOURCE_CREATED"),
        UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS"),
        USER_NOT_CONFIRMED("USER_NOT_CONFIRMED"),
        INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
        INVALID_AUTH_TOKEN("INVALID_AUTH_TOKEN"),
        EXPIRED_AUTH_TOKEN("EXPIRED_AUTH_TOKEN"),
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

    @Value("${top.categories.limit}")
    private Integer topCategoriesLimit;

    @Value("${top.categories.products.limit}")
    private Integer topCategoriesProductsLimit;

    @Value("${top.products.limit}")
    private Integer topProductsLimit;

    @Value("${products.image.size}")
    private Integer productImageSize;

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
    private UserAddressCRUD userAddressCRUD;

    @Autowired
    private WeightVariantCRUD weighVariantCRUD;

    private User currentUser;

    private Boolean isGuestUser;

    private Boolean isInternalCall;

    public Boolean getIsInternalCall() {
        return isInternalCall;
    }

    public void setIsInternalCall(Boolean isInternalCall) {
        this.isInternalCall = isInternalCall;
    }

    public Boolean getIsGuestUser() {
        return isGuestUser;
    }

    public void setIsGuestUser(Boolean isGuestUser) {
        this.isGuestUser = isGuestUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

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

    protected UserAddressCRUD getUserAddressCRUD() {
        return userAddressCRUD;
    }

    protected WeightVariantCRUD getWeighVariantCRUD() {
        return weighVariantCRUD;
    }

    public class SuccessResponse {
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

    public class FailureResponse {
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

        protected ResponseEntity<String> throwMandatoryMissing() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.MANDATORY_MISSING);
            responseData.put(LowerCase.MESSAGE, "Mandatory Missing");
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

        protected ResponseEntity<String> throwInvalidHeader() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_HEADER);
            responseData.put(LowerCase.MESSAGE, "Invalid header");
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

        protected ResponseEntity<String> throwInvalidCredentials() {
            JSONObject responseData = new JSONObject();
            responseData.put(LowerCase.STATUS, status);
            responseData.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_CREDENTIALS);
            responseData.put(LowerCase.MESSAGE, "Invalid credentials");
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
                case ControllerConstants.ExceptionMessageCase.INVALID_FILE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject().put(LowerCase.ERROR, "Upload a valid file"));
                    return failureResponse.throwInvalidBodyInput();
                case ControllerConstants.ExceptionMessageCase.REQUEST_BODY_IS_MISSING:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject().put(LowerCase.ERROR, "Request body is missing"));
                    return failureResponse.throwInvalidBodyInput();
                case ControllerConstants.ExceptionMessageCase.INVALID_IMAGE_TYPE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(new JSONObject().put(LowerCase.ERROR, "Image Type Parameter Value is invalid"));
                    return failureResponse.throwInvalidInput();
                case ProductConstants.ExceptionMessageCase.INVALID_PRICE_MIN_VALUE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(new JSONObject().put(LowerCase.PARAMETER, ProductConstants.SnakeCase.PRICE_MIN)
                                    .put(LowerCase.MESSAGE, ProductConstants.MessageCase.INVALID_PRICE_MIN_VALUE));
                    return failureResponse.throwInvalidInput();
                case ProductConstants.ExceptionMessageCase.INVALID_PRICE_MAX_VALUE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(new JSONObject().put(LowerCase.PARAMETER, ProductConstants.SnakeCase.PRICE_MAX)
                                    .put(LowerCase.MESSAGE, ProductConstants.MessageCase.INVALID_PRICE_MAX_VALUE));
                    return failureResponse.throwInvalidInput();
                case ProductConstants.ExceptionMessageCase.INVALID_SORT_ORDER_VALUE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(new JSONObject().put(LowerCase.PARAMETER, ProductConstants.SnakeCase.SORT_ORDER)
                                    .put(LowerCase.MESSAGE, ProductConstants.MessageCase.INVALID_SORT_ORDER));
                    return failureResponse.throwInvalidInput();
                case ProductConstants.ExceptionMessageCase.INVALID_SORT_BY_VALUE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(new JSONObject().put(LowerCase.PARAMETER, ProductConstants.SnakeCase.SORT_BY)
                                    .put(LowerCase.MESSAGE, ProductConstants.MessageCase.INVALID_SORT_BY));
                    return failureResponse.throwInvalidInput();
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
                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.SnakeCase.PRODUCT_ID)
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
                case ProductConstants.ExceptionMessageCase.MAXIMUM_IMAGES_FOR_PRODUCT_WILL_BE_REACHED_FOR_IMAGEURL:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "Maximum Number of Images that can be created for a Product Will be Reached"));
                    return failureResponse.throwMaximumResourceCreated();
                case ProductConstants.ExceptionMessageCase.MAXIMUM_IMAGES_FOR_PRODUCT_REACHED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "Maximum Number of Images that can be created for a Product Reached"));
                    return failureResponse.throwMaximumResourceCreated();
                case ProductConstants.ExceptionMessageCase.IMAGE_ID_IN_GIVEN_LIST_NOT_FOUND_FOR_THE_PRODUCT:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setCode(ERROR_CODES.RESOURCE_NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.SnakeCase.IMAGE_ID)
                            .put(LowerCase.MESSAGE,
                                    "Invalid Image Id is found in the list the given Product"));
                    failureResponse.setMessage("Invalid Image Id");
                    return failureResponse.getResponse();
                case ProductConstants.ExceptionMessageCase.NO_IMAGE_ASSOCIATED_WITH_PRODUCT:
                case ProductConstants.ExceptionMessageCase.IMAGE_ID_NOT_FOUND_FOR_THE_PRODUCT:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setCode(ERROR_CODES.RESOURCE_NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.SnakeCase.IMAGE_ID)
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
                case ProductConstants.ExceptionMessageCase.INVALID_IMAGE_IDS_FOR_DELETE:
                case ProductConstants.ExceptionMessageCase.INVALID_BODY_FOR_CREATE_VARIATION:
                case ProductConstants.ExceptionMessageCase.INVALID_BODY_FOR_DELETE_VARIATION:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case WeightVariantConstants.ExceptionMessageCase.ALREADY_WEIGHT_VARIANT_ADDED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Weight Variant Relation Already Done"));
                    return failureResponse.throwInvalidBodyInput();
                case WeightVariantConstants.ExceptionMessageCase.WEIGHT_VARIANT_ADDED_TO_OTHER_PRODUCT:
                    failureResponse.setApiResponseStatus(HttpStatus.FORBIDDEN);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Weight Variant Relation Already Exists With other product"));
                    return failureResponse.throwInvalidBodyInput();
                case SizeVariantConstants.ExceptionMessageCase.ALREADY_SIZE_VARIANT_ADDED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Size Variant Relation Already Done"));
                    return failureResponse.throwInvalidBodyInput();
                case SizeVariantConstants.ExceptionMessageCase.SIZE_VARIANT_ADDED_TO_OTHER_PRODUCT:
                    failureResponse.setApiResponseStatus(HttpStatus.FORBIDDEN);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Size Variant Relation Already Exists With other product"));
                    return failureResponse.throwInvalidBodyInput();
                case ColorVariantConstants.ExceptionMessageCase.ALREADY_COLOR_VARIANT_ADDED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Color Variant Relation Already Done"));
                    return failureResponse.throwInvalidBodyInput();
                case ColorVariantConstants.ExceptionMessageCase.COLOR_VARIANT_ADDED_TO_OTHER_PRODUCT:
                    failureResponse.setApiResponseStatus(HttpStatus.FORBIDDEN);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Color Variant Relation Already Exists With other product"));
                    return failureResponse.throwInvalidBodyInput();
                case ProductConstants.ExceptionMessageCase.VARIANT_NOT_PRESENT:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.VARIANT_TYPE)
                                            .put(LowerCase.MESSAGE,
                                                    "No Given Variant has been associated with other product"));
                    return failureResponse.throwInvalidBodyInput();
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
                            .put(ProductConstants.LowerCase.FIELD, AnimalConstants.SnakeCase.ANIMAL_ID)
                            .put(LowerCase.MESSAGE, "Animal Id is Invalid"));
                    return failureResponse.throwInvalidPathVariable();
                case TopCategoriesConstants.ExceptionMessageCase.GIVEN_ID_NOT_FOUND:
                case TopProductsConstants.ExceptionMessageCase.INVALID_TOP_PRODUCT_ID:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, TopProductsConstants.SnakeCase.TOP_PRODUCT_ID)
                            .put(LowerCase.MESSAGE, "Top Product Id is Invalid"));
                    return failureResponse.throwInvalidPathVariable();
                case TopProductsConstants.ExceptionMessageCase.MISSING_FIELD_FOR_TOPPRODUCT_CREATE_SINGLE:
                case TopProductsConstants.ExceptionMessageCase.MISSING_FIELD_FOR_TOPPRODUCT_CREATE_BULK:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case TopProductsConstants.MessageCase.PRODUCTS_EXISTING_IN_TOPPRODUCTS_WHICH_IS_MAXMIMUM:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    TopProductsConstants.CapitalizationCase.TOP_PRODUCTS)
                                            .put(LowerCase.MESSAGE,
                                                    TopCategoriesConstants.CapitalizationCase.ALREADY
                                                            + CartConstants.LowerCase.GAP + topProductsLimit
                                                            + CartConstants.LowerCase.GAP
                                                            + TopProductsConstants.MessageCase.PRODUCTS_EXISTING_IN_TOPPRODUCTS_WHICH_IS_MAXMIMUM));
                    return failureResponse.throwMaximumResourceCreated();
                case ProductConstants.ExceptionMessageCase.PRODUCT_ALREADY_EXISTS:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Product Already Exists"));
                    return failureResponse.throwInvalidBodyInput();
                case TopCategoriesConstants.ExceptionMessageCase.CATEGORY_NOT_IN_TOP_CATEGORY:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    CategoryConstants.SnakeCase.CATEGORY_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Category not Found in Top Category"));
                    return failureResponse.throwInvalidPathVariable();
                case TopCategoriesConstants.ExceptionMessageCase.MISSING_FIELD_IN_ADD_CATEGORY_PRODUCTS_TO_TOP_CATEGORY:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case TopCategoriesConstants.MessageCase.CATEGORIES_EXISTING_IN_TOPCATEGORIES_WHICH_IS_MAXIMUM:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    TopProductsConstants.CapitalizationCase.TOP_PRODUCTS)
                                            .put(LowerCase.MESSAGE, CapitalizationCase.ALREADY
                                                    + CartConstants.LowerCase.GAP + topCategoriesLimit
                                                    + CartConstants.LowerCase.GAP
                                                    + TopCategoriesConstants.MessageCase.CATEGORIES_EXISTING_IN_TOPCATEGORIES_WHICH_IS_MAXIMUM));
                    return failureResponse.throwMaximumResourceCreated();
                case TopCategoriesConstants.MessageCase.PRODUCTS_IN_GIVEN_CATEGORY_IN_TOPCATEGORIES_WHICH_WILL_BE_EXCEEDED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    TopProductsConstants.CapitalizationCase.TOP_PRODUCTS)
                                            .put(LowerCase.MESSAGE, TopCategoriesConstants.MessageCase.MAXIMUM_OF
                                                    + topCategoriesProductsLimit
                                                    + CartConstants.LowerCase.GAP
                                                    + TopCategoriesConstants.MessageCase.PRODUCTS_IN_GIVEN_CATEGORY_IN_TOPCATEGORIES_WHICH_WILL_BE_EXCEEDED));
                    return failureResponse.throwMaximumResourceCreated();
                case ProductConstants.ExceptionMessageCase.PRODUCT_OF_ID_ARG0_ALREADY_EXISTS:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case ControllerConstants.ExceptionMessageCase.INVALID_FILE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject().put(LowerCase.ERROR, "Upload a valid file"));
                    return failureResponse.throwInvalidBodyInput();
                case ControllerConstants.ExceptionMessageCase.REQUEST_BODY_IS_MISSING:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject().put(LowerCase.ERROR, "Request body is missing"));
                    return failureResponse.throwInvalidBodyInput();
                case ControllerConstants.ExceptionMessageCase.INVALID_IMAGE_TYPE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(new JSONObject().put(LowerCase.ERROR, "Image Type Parameter Value is invalid"));
                    return failureResponse.throwInvalidInput();
                case CategoryConstants.ExceptionMessageCase.CATEGORY_NOT_FOUND:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    CategoryConstants.SnakeCase.CATEGORY_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Category not found in Top Categories"));
                    return failureResponse.throwInvalidPathVariable();
                case ProductConstants.ExceptionMessageCase.PRODUCT_NOT_FOUND:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Product not found in given Category in Top Categories"));
                    return failureResponse.throwInvalidPathVariable();
                case AnimalConstants.ExceptionMessageCase.MISSING_ANIMAL_FIELD_FOR_CREATE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case AnimalConstants.ExceptionMessageCase.ANIMAL_NOT_FOUND:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    AnimalConstants.SnakeCase.ANIMAL_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Invalid Animal Id"));
                    return failureResponse.throwInvalidPathVariable();
                case ProductConstants.ExceptionMessageCase.INVALID_IMAGE_FORMAT:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "Only JPG, JPEG, and PNG files are allowed."));
                    return failureResponse.throwInvalidBodyInput();
                case AnimalConstants.ExceptionMessageCase.MAXIMUM_IMAGES_FOR_ANIMAL_REACHED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "Maximum Number of Images that can be created for a Animal Reached"));
                    return failureResponse.throwMaximumResourceCreated();
                case AnimalConstants.ExceptionMessageCase.NO_IMAGE_PRESENT_FOR_ANIMAL:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "No Images have been associated to this animal"));
                    return failureResponse.throwInvalidBodyInput();
                case CategoryConstants.ExceptionMessageCase.INVALID_CATEGORY_ID:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, CategoryConstants.SnakeCase.CATEGORY_ID)
                            .put(LowerCase.MESSAGE, "Category Id is Invalid"));
                    return failureResponse.throwNotFoundForIds();
                case CategoryConstants.ExceptionMessageCase.MISSING_CATEGORY_FIELD_FOR_CREATE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case CategoryConstants.ExceptionMessageCase.MAXIMUM_IMAGES_FOR_CATEGORY_REACHED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "Maximum Number of Images that can be created for a Category Reached"));
                    return failureResponse.throwMaximumResourceCreated();
                case CategoryConstants.ExceptionMessageCase.NO_IMAGE_PRESENT_FOR_CATEGORY:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD, ProductConstants.LowerCase.IMAGE)
                                            .put(LowerCase.MESSAGE,
                                                    "No Images have been associated to this Category"));
                    return failureResponse.throwInvalidBodyInput();
                case ExceptionMessageCase.INTERNAL_SERVER_ERROR:
                    return failureResponse.getResponse();
                default:
                    return failureResponse.getResponse();
            }
        }
    }

    public class UserControllerExceptions {
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
                case UserConstants.ExceptionMessageCase.MISSING_USER_FIELD_FOR_AUTHENTICATION:
                case UserConstants.ExceptionMessageCase.MISSING_USER_FIELD_FOR_CREATE_USER:
                case UserConstants.ExceptionMessageCase.MISSING_USER_FIELD_FOR_UPDATE_USER:
                case UserConstants.ExceptionMessageCase.MISSING_USER_FIELD_FOR_CONFIRM_USER:
                case UserConstants.ExceptionMessageCase.MISSING_USER_FIELD_FOR_TRIGGER_CHANGE_PASSWORD_OTP:
                case UserAddressConstants.ExceptionMessageCase.MISSING_USER_ADDRESS_FIELD_FOR_CREATE_USER_ADDRESS:
                case UserAddressConstants.ExceptionMessageCase.MISSING_USER_ADDRESS_FIELD_FOR_UPDATE_USER_ADDRESS:
                case CartConstants.ExceptionMessageCase.MISSING_CART_FIELD_FOR_ADD_CART_PRODUCTS:
                case CartConstants.ExceptionMessageCase.MISSING_CART_FIELD_FOR_DELETE_CART_PRODUCTS:
                case CartConstants.ExceptionMessageCase.MISSING_CART_FIELD_FOR_UPDATE_CART_PRODUCTS:
                case OrderHistoryConstants.ExceptionMessageCase.MISSING_ORDER_HISTORY_FIELD_FOR_CREATE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case UserConstants.ExceptionMessageCase.USER_NOT_CONFIRMED:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setCode(ERROR_CODES.USER_NOT_CONFIRMED);
                    failureResponse.setMessage("User not Confirmed. Kindly Confirm the User to proceed.");
                    failureResponse.setData(getData());
                    return failureResponse.getResponse();
                case UserConstants.ExceptionMessageCase.EMAIL_ALREADY_EXISTS:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, UserConstants.LowerCase.EMAIL)
                            .put(LowerCase.MESSAGE, "Email Address Already Exist"));
                    return failureResponse.throwInvalidBodyInput();
                case UserConstants.ExceptionMessageCase.INVALID_CONFIRMATION_CODE:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, UserConstants.SnakeCase.CONFIRMATION_CODE)
                            .put(LowerCase.MESSAGE, "Invalid Confirmation Code"));
                    return failureResponse.throwInvalidBodyInput();
                case UserConstants.ExceptionMessageCase.INVALID_USER_ID_IN_BODY:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, UserConstants.SnakeCase.USER_ID)
                            .put(LowerCase.MESSAGE, "User Id is Invalid"));
                    return failureResponse.throwInvalidBodyInput();
                case UserConstants.ExceptionMessageCase.INVALID_USER_ID:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, UserConstants.SnakeCase.USER_ID)
                            .put(LowerCase.MESSAGE, "User Id is Invalid"));
                    return failureResponse.throwNotFoundForIds();
                case UserConstants.ExceptionMessageCase.INVALID_CREDENTIALS:
                    failureResponse.setApiResponseStatus(HttpStatus.UNAUTHORIZED);
                    failureResponse.setData(new JSONObject()
                            .put(ControllerConstants.LowerCase.ERROR,
                                    ControllerConstants.SnakeCase.AUTHENTICATION_ERROR)
                            .put(ControllerConstants.LowerCase.REASON,
                                    ControllerConstants.SnakeCase.INVALID_CREDENTIALS));
                    return failureResponse.throwInvalidCredentials();
                case ControllerConstants.ExceptionMessageCase.AUTHENTICATION_TOKEN_DOES_NOT_EXIST:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setCode(ERROR_CODES.FORBIDDEN_OPERATION);
                    failureResponse.setMessage("User is not Logged In");
                    failureResponse.setData(new JSONObject());
                    return failureResponse.getResponse();
                case UserAddressConstants.MessageCase.ADDRESS_COUNT_FOR_USER_IS_MAXIMUM:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    UserAddressConstants.LowerCase.ADDRESS)
                                            .put(LowerCase.MESSAGE,
                                                    "Maximum Number of Address that can be created for a User has been Reached"));
                    return failureResponse.throwMaximumResourceCreated();
                case UserAddressConstants.ExceptionMessageCase.INVALID_USER_ADDRESS_ID:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD, UserAddressConstants.SnakeCase.ADDRESS_ID)
                            .put(LowerCase.MESSAGE, "Address Id is Invalid"));
                    return failureResponse.throwNotFoundForIds();
                case UserAddressConstants.ExceptionMessageCase.ADDRESS_ID_ALREADY_SETTED_AS_DEFAULT:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    UserAddressConstants.SnakeCase.ADDRESS_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Address Id is already setted up as default Address"));
                    return failureResponse.throwInvalidPathVariable();
                case CartConstants.ExceptionMessageCase.CART_ALREADY_EXISTS_FOR_THIS_USER:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Product Already Exists in Cart"));
                    return failureResponse.throwInvalidBodyInput();
                case UserConstants.CapitalizationCase.USER + CartConstants.LowerCase.GAP
                        + CartConstants.MessageCase.SHOULD_BE_PRESENT:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    UserConstants.SnakeCase.USER_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Invalid User Id"));
                    return failureResponse.throwInvalidHeader();
                case ProductConstants.ExceptionMessageCase.PRODUCT_NOT_FOUND:
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Invalid Product Id"));
                    return failureResponse.throwInvalidBodyInput();
                case CartConstants.ExceptionMessageCase.CART_NOT_FOUND_FOR_USER:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse
                            .setData(
                                    new JSONObject()
                                            .put(ProductConstants.LowerCase.FIELD,
                                                    ProductConstants.SnakeCase.PRODUCT_ID)
                                            .put(LowerCase.MESSAGE,
                                                    "Product Not Found in Cart"));
                    return failureResponse.throwInvalidBodyInput();
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
                case OrderHistoryConstants.ExceptionMessageCase.INVALID_PRODUCT_ID_IN_BULK_PRODUCTS:
                    failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                    failureResponse.setData(getData());
                    return failureResponse.throwInvalidBodyInput();
                case ExceptionMessageCase.INTERNAL_SERVER_ERROR:
                    return failureResponse.getResponse();
                default:
                    return failureResponse.getResponse();
            }
        }
    }

    public class ParameterCheck {
        public void checkParameterForSingleUrlImage(String imageType, MultipartFile animalImage,
                HashMap<String, String> requestBody) throws Exception {
            if ("file".equals(imageType)) {
                if (animalImage == null || animalImage.isEmpty()) {
                    throw new Exception(ExceptionMessageCase.INVALID_FILE);
                }

            } else if ("external_url".equals(imageType)) {
                if (requestBody == null || requestBody.isEmpty()) {
                    throw new Exception(ExceptionMessageCase.REQUEST_BODY_IS_MISSING);
                }
                if (!requestBody.containsKey("url")) {
                    throw new Exception(ExceptionMessageCase.REQUEST_BODY_IS_MISSING);
                }
            } else {
                throw new Exception(ExceptionMessageCase.INVALID_IMAGE_TYPE);
            }
        }

        public void checkParameterForMultipleUrlImage(String imageType, MultipartFile animalImage,
                List<HashMap<String, String>> requestBody) throws Exception {
            if ("file".equals(imageType)) {
                if (animalImage == null || animalImage.isEmpty()) {
                    throw new Exception(ExceptionMessageCase.INVALID_FILE);
                }

            } else if ("external_url".equals(imageType)) {
                if (requestBody == null || requestBody.isEmpty()) {
                    throw new Exception(ExceptionMessageCase.REQUEST_BODY_IS_MISSING);
                }
                Iterator<HashMap<String, String>> requestBodyIter = requestBody.iterator();
                while (requestBodyIter.hasNext()) {
                    if (!requestBodyIter.next().containsKey("url")) {
                        throw new Exception(ExceptionMessageCase.REQUEST_BODY_IS_MISSING);
                    }
                }
            } else {
                throw new Exception(ExceptionMessageCase.INVALID_IMAGE_TYPE);
            }
        }

        public List<String> extractImageUrlFromHashmap(List<HashMap<String, String>> requestBody) {
            List<String> imageUrls = new ArrayList<>();
            for (HashMap<String, String> url : requestBody) {
                imageUrls.add(url.get("url"));
            }
            return imageUrls;
        }

        public Boolean isFile(String imageType) {
            return "file".equals(imageType);
        }

        public Boolean isUrl(String imageType) {
            return "external_url".equals(imageType);
        }

        public void checkSortByAndSortOrderProductParameter(String sortOrder, String sortBy) throws Exception {
            if (sortBy != null || sortOrder != null) {
                if (!(sortOrder != null && (sortOrder.equalsIgnoreCase("asc") || sortOrder.equalsIgnoreCase("desc")))) {
                    throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_SORT_ORDER_VALUE);
                }
                if (!(sortBy != null && (PRODUCTCOLUMN.NAME.getColumnName().equalsIgnoreCase(sortBy)
                        || PRODUCTCOLUMN.PRICE.getColumnName().equalsIgnoreCase(sortBy)))) {
                    throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_SORT_BY_VALUE);
                }
            }
        }

        public Sort.Direction getSortOrder(String sortOrder) {
            return sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC
                    : sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : null;
        }

        public PRODUCTCOLUMN getProductColumnBasedOnSortBy(String sortBy) {
            if (sortBy != null) {
                return PRODUCTCOLUMN.NAME.getColumnName().equalsIgnoreCase(sortBy) ? PRODUCTCOLUMN.NAME
                        : PRODUCTCOLUMN.PRICE.getColumnName().equalsIgnoreCase(sortBy) ? PRODUCTCOLUMN.PRICE : null;
            }
            return null;
        }
    }
}
