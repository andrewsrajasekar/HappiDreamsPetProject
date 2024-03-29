package com.happidreampets.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.AnimalConstants;
import com.happidreampets.app.constants.CategoryConstants;
import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.TopCategoriesConstants;
import com.happidreampets.app.constants.TopProductsConstants;
import com.happidreampets.app.constants.TopProductsConstants.SnakeCase;
import com.happidreampets.app.database.crud.AnimalCRUD;
import com.happidreampets.app.database.crud.CategoryCRUD;
import com.happidreampets.app.database.crud.PromotionsCRUD;
import com.happidreampets.app.database.crud.TopCategoriesCRUD;
import com.happidreampets.app.database.crud.TopProductsCRUD;
import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Animal.ANIMALCOLUMN;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.Promotions;
import com.happidreampets.app.database.model.Category.CATEGORYCOLUMN;
import com.happidreampets.app.database.model.TopProducts;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;
import com.happidreampets.app.utils.AccessLevel;
import com.happidreampets.app.utils.JSONUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("")
public class OtherDataController extends APIController {

    private Animal currentAnimal;
    private Category currentCategory;
    private Product currentProduct;

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
    }

    public Animal getCurrentAnimal() {
        return currentAnimal;
    }

    public void setCurrentAnimal(Animal currentAnimal) {
        this.currentAnimal = currentAnimal;
    }

    public Category getCurrentCategory() {
        return currentCategory;
    }

    public void setCurrentCategory(Category currentCategory) {
        this.currentCategory = currentCategory;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        FailureResponse failureResponse = new FailureResponse();
        failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
        failureResponse.setData(new JSONObject().put(ControllerConstants.LowerCase.ERROR, "Invalid request body"));
        return failureResponse.throwMandatoryMissing();
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException ex) {
        FailureResponse failureResponse = new FailureResponse();
        failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
        failureResponse.setData(new JSONObject().put(LowerCase.ERROR, "Invalid request format. Please upload a file."));
        return failureResponse.getResponse();
    }

    @RequestMapping(value = "/promotions", method = RequestMethod.GET)
    public ResponseEntity<String> getPromotions() {
        try {
            PromotionsCRUD promotionsCRUD = getPromotionsCRUD();
            JSONObject data = promotionsCRUD.getAllPromotionDetails(Boolean.TRUE);
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                successResponse.setApiResponseStatus(HttpStatus.NO_CONTENT);
            } else {
                successResponse.setResponseData(data);
            }
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/promotions", method = RequestMethod.POST)
    public ResponseEntity<String> createPromotions(@RequestPart(required = true) MultipartFile promotionImage) {
        try {
            PromotionsCRUD promotionsCRUD = getPromotionsCRUD();
            String originalFilename = promotionImage.getOriginalFilename();
            if (!promotionsCRUD.isValidFileExtension(originalFilename)) {
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_IMAGE_FORMAT);
            }
            Promotions promotion = promotionsCRUD.createPromotionAndInsertImage(promotionImage.getInputStream(),
                    promotionsCRUD.getExtension(originalFilename));
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, promotion.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/promotion/{promotionId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePromotion(@PathVariable("promotionId") Long promotionId) {
        try {
            PromotionsCRUD promotionsCRUD = getPromotionsCRUD();
            Boolean isDeleted = promotionsCRUD.deletePromotion(promotionId);
            SuccessResponse successResponse = new SuccessResponse();
            if (!isDeleted) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, promotionId));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/top-products", method = RequestMethod.GET)
    public ResponseEntity<String> getTopProducts() {
        try {
            TopProductsCRUD topProductsCRUD = getTopProductsCRUD();
            JSONObject data = topProductsCRUD.getTopProductsDetails();
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                successResponse.setApiResponseStatus(HttpStatus.NO_CONTENT);
            } else {
                successResponse.setResponseData(data);
            }
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/top-product/{topProductId}", method = RequestMethod.GET)
    public ResponseEntity<String> getTopProduct(@PathVariable("topProductId") Long topProductId) {
        try {
            TopProductsCRUD topProductsCRUD = getTopProductsCRUD();
            JSONObject data = topProductsCRUD.getTopProductDetailsInJSON(topProductId);
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            if (data == null) {
                throw new Exception(TopProductsConstants.ExceptionMessageCase.INVALID_TOP_PRODUCT_ID);
            } else {
                successResponse.setData(data);
            }
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/top-product", method = RequestMethod.POST)
    public ResponseEntity<String> addATopProduct(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);
            TopProductsCRUD topProductsCRUD = getTopProductsCRUD();
            JSONObject validationResult = topProductsCRUD.validateBodyDataForSingleCreate(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        TopProductsConstants.ExceptionMessageCase.MISSING_FIELD_FOR_TOPPRODUCT_CREATE_SINGLE);
            }

            TopProducts data = topProductsCRUD.createTopProducts(
                    (Product) validationResult.get(ProductConstants.LowerCase.PRODUCT),
                    Integer.parseInt(body.get(SnakeCase.ORDER_NUMBER).toString()));

            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            if (data == null) {
                throw new Exception();
            } else {
                successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, data.getId()));
            }
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                otherDataControllerExceptions.setData(errorData);
            }
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/top-products", method = RequestMethod.POST)
    public ResponseEntity<String> clearAllAndAddTopProducts(@RequestBody List<Map<String, Object>> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            TopProductsCRUD topProductsCRUD = getTopProductsCRUD();
            JSONArray body = JSONUtils
                    .convertListToJSONArray(bodyData);

            JSONObject validationResult = topProductsCRUD.validateBodyDataForBulkCreate(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        TopProductsConstants.ExceptionMessageCase.MISSING_FIELD_FOR_TOPPRODUCT_CREATE_BULK);
            }

            topProductsCRUD.clearAndCreateTopProducts(body);

            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            successResponse.setData(new JSONObject());
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                otherDataControllerExceptions.setData(errorData);
            }
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/top-product/{topProductId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTopProduct(@PathVariable("topProductId") Long topProductId) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            TopProductsCRUD topProductsCRUD = getTopProductsCRUD();

            topProductsCRUD.deleteTopProductById(topProductId);

            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, topProductId));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/top-categories", method = RequestMethod.GET)
    public ResponseEntity<String> getTopCategories() {
        try {
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            TopCategoriesCRUD topCategoriesCRUD = getTopCategoriesCRUD();
            topCategoriesCRUD.setDbFilter(dbFilter);
            JSONObject data = topCategoriesCRUD.getTopCategoriesDetails();
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                successResponse.setApiResponseStatus(HttpStatus.NO_CONTENT);
            } else {
                successResponse.setResponseData(data);
            }
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/top-category/{categoryId}", method = RequestMethod.GET)
    public ResponseEntity<String> getTopCategoryByCategory() {
        try {
            TopCategoriesCRUD topCategoriesCRUD = getTopCategoriesCRUD();
            JSONObject data = topCategoriesCRUD.getTopCategoryAsJSON(getCurrentCategory());
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            if (data == null) {
                throw new Exception(TopCategoriesConstants.ExceptionMessageCase.CATEGORY_NOT_IN_TOP_CATEGORY);
            } else {
                successResponse.setData(data);
            }
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/top-category", method = RequestMethod.POST)
    public ResponseEntity<String> addATopCategory(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);
            TopCategoriesCRUD topCategoriesCRUD = getTopCategoriesCRUD();
            topCategoriesCRUD.setFromController(true);
            JSONObject validationResult = topCategoriesCRUD.validateBodyDataForCreate(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        TopCategoriesConstants.ExceptionMessageCase.MISSING_FIELD_IN_ADD_CATEGORY_PRODUCTS_TO_TOP_CATEGORY);
            }

            List<Long> productIds = topCategoriesCRUD.checkAndFetchBodyToProductIdList(body);

            topCategoriesCRUD.bulkCreateForCategory(getCategoryCRUD().getCategoryDetail(
                    Long.valueOf("" + body.get(AnimalConstants.SnakeCase.ANIMAL_ID)),
                    Long.valueOf("" + body.get(CategoryConstants.SnakeCase.CATEGORY_ID))), productIds);

            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            return successResponse.getResponse();
        } catch (Exception ex) {
            if (ex.getMessage() != null) {
                if (ex.getMessage().split(ControllerConstants.SpecialCharacter.UNDERSCORE).length >= 2 &&
                        ex.getMessage().split(ControllerConstants.SpecialCharacter.UNDERSCORE)[1]
                                .equals(ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION)) {
                    errorData = new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD,
                                    ProductConstants.LowerCase.PRODUCTS)
                            .put(LowerCase.MESSAGE,
                                    ex.getMessage().split(ControllerConstants.SpecialCharacter.UNDERSCORE
                                            + ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION)[0]);
                    ex = new Exception(ProductConstants.ExceptionMessageCase.PRODUCT_OF_ID_ARG0_ALREADY_EXISTS);
                }
            }
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                otherDataControllerExceptions.setData(errorData);
            }
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/top-category/{categoryId}/product/{productId}", method = RequestMethod.POST)
    public ResponseEntity<String> addATopCategoryProducts(@PathVariable("productId") Long productId) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            TopCategoriesCRUD topCategoryCRUD = getTopCategoriesCRUD();

            topCategoryCRUD.singleCreateForCategory(currentCategory, productId);

            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/top-category/{categoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTopCategory() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            TopCategoriesCRUD topCategoryCRUD = getTopCategoriesCRUD();

            topCategoryCRUD.deleteTopCategoryBasedOnCategory(getCurrentCategory());

            successResponse.setApiResponseStatus(HttpStatus.OK);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/top-category/{categoryId}/product/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTopCategoryBasedOnProduct() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            TopCategoriesCRUD topCategoryCRUD = getTopCategoriesCRUD();

            topCategoryCRUD.deleteTopCategoryBasedOnProduct(getCurrentProduct());

            successResponse.setApiResponseStatus(HttpStatus.OK);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/animals", method = RequestMethod.GET)
    public ResponseEntity<String> getAnimalsData(
            @RequestParam(value = ProductConstants.LowerCase.PAGE, defaultValue = "1") Integer page,
            @RequestParam(value = ProductConstants.SnakeCase.PER_PAGE, defaultValue = "6") Integer per_page) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            if (page <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.PAGE_GREATER_THAN_ZERO);
            }
            if (per_page <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.PER_PAGE_GREATER_THAN_ZERO);
            }
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(page - 1);
            dbFilter.setLimitIndex(per_page);
            dbFilter.setSortColumn(ANIMALCOLUMN.NAME);
            AnimalCRUD animalCRUD = getAnimalCRUD();
            animalCRUD.setDbFilter(dbFilter);
            JSONObject data = animalCRUD.getAnimals();
            successResponse = new SuccessResponse();
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                successResponse.setApiResponseStatus(HttpStatus.NO_CONTENT);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animals/all", method = RequestMethod.GET)
    public ResponseEntity<String> getAllAnimalsData() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(-1);
            dbFilter.setLimitIndex(-1);
            dbFilter.setSortColumn(ANIMALCOLUMN.NAME);
            AnimalCRUD animalCRUD = getAnimalCRUD();
            animalCRUD.setDbFilter(dbFilter);
            JSONObject data = animalCRUD.getAllAnimals();
            successResponse = new SuccessResponse();
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                successResponse.setApiResponseStatus(HttpStatus.NO_CONTENT);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/animal/{animalId}", method = RequestMethod.GET)
    public ResponseEntity<String> getAnimal() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            JSONObject data = currentAnimal.toJSON(Boolean.TRUE, Boolean.TRUE);
            successResponse = new SuccessResponse();
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                throw new Exception(AnimalConstants.ExceptionMessageCase.INVALID_ANIMAL_ID);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal", method = RequestMethod.POST)
    public ResponseEntity<String> createAnimal(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            AnimalCRUD animalCRUD = getAnimalCRUD();
            JSONObject validationResult = animalCRUD.checkBodyOfCreateAnimal(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(AnimalConstants.ExceptionMessageCase.MISSING_ANIMAL_FIELD_FOR_CREATE);
            }
            Animal animal = animalCRUD.createAnimal(body.get(ProductConstants.LowerCase.NAME).toString(),
                    body.get(ProductConstants.LowerCase.DESCRIPTION).toString());
            successResponse = new SuccessResponse();
            if (animal == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.CREATED);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, animal.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                otherDataControllerExceptions.setData(errorData);
            }
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateAnimal(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            AnimalCRUD animalCRUD = getAnimalCRUD();

            Animal animal = animalCRUD.updateAnimal(currentAnimal.getId(),
                    JSONUtils.optString(body, ProductConstants.LowerCase.NAME, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.DESCRIPTION, null));

            successResponse = new SuccessResponse();
            if (animal == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, animal.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAnimal() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            AnimalCRUD animalCRUD = getAnimalCRUD();

            Boolean isDeleted = animalCRUD.deleteAnimalById(currentAnimal.getId());

            successResponse = new SuccessResponse();
            if (!isDeleted) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentAnimal.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/image", method = RequestMethod.PUT)
    public ResponseEntity<String> addAnimalImage(
            @RequestParam(ProductConstants.SnakeCase.IMAGE_TYPE) String imageType,
            @RequestPart(required = false) MultipartFile animalImage,
            @RequestBody(required = false) HashMap<String, String> requestBody) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ParameterCheck paramCheck = new ParameterCheck();
            paramCheck.checkParameterForSingleUrlImage(imageType, animalImage, requestBody);
            Boolean isFile = paramCheck.isFile(imageType);
            AnimalCRUD animalCRUD = getAnimalCRUD();
            if (isFile) {
                String originalFilename = animalImage.getOriginalFilename();
                if (!animalCRUD.isValidFileExtension(originalFilename)) {
                    throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_IMAGE_FORMAT);
                }
                animalCRUD.addImageToAnimal(currentAnimal, animalImage.getInputStream(),
                        animalCRUD.getExtension(originalFilename));
            } else {
                animalCRUD.addImageUrlToAnimal(currentAnimal, requestBody.get("url"));
            }
            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentAnimal.getId()));
            return successResponse.getResponse();

        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/image", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAnimalImage() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            AnimalCRUD animalCRUD = getAnimalCRUD();
            animalCRUD.deleteImageFromAnimal(currentAnimal);

            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentAnimal.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/animal/{animalId}/categories", method = RequestMethod.GET)
    public ResponseEntity<String> getCategoriesData(
            @RequestParam(value = ProductConstants.LowerCase.PAGE, defaultValue = "1") Integer page,
            @RequestParam(value = ProductConstants.SnakeCase.PER_PAGE, defaultValue = "6") Integer per_page) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            if (page <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.PAGE_GREATER_THAN_ZERO);
            }
            if (per_page <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.PER_PAGE_GREATER_THAN_ZERO);
            }
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(page - 1);
            dbFilter.setLimitIndex(per_page);
            dbFilter.setSortColumn(CATEGORYCOLUMN.NAME);
            CategoryCRUD categoryCRUD = getCategoryCRUD();
            categoryCRUD.setDbFilter(dbFilter);
            JSONObject data = categoryCRUD.getCategoryDetails(getCurrentAnimal());
            successResponse = new SuccessResponse();
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                successResponse.setApiResponseStatus(HttpStatus.NO_CONTENT);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/categories/all", method = RequestMethod.GET)
    public ResponseEntity<String> getAllCategoriesData() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(-1);
            dbFilter.setLimitIndex(-1);
            dbFilter.setSortColumn(ANIMALCOLUMN.NAME);
            CategoryCRUD categoryCRUD = getCategoryCRUD();
            categoryCRUD.setDbFilter(dbFilter);
            JSONObject data = categoryCRUD.getAllCategories(getCurrentAnimal());
            successResponse = new SuccessResponse();
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                successResponse.setApiResponseStatus(HttpStatus.NO_CONTENT);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/animal/{animalId}/category/{categoryId}", method = RequestMethod.GET)
    public ResponseEntity<String> getCategory() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(0);
            dbFilter.setLimitIndex(0);
            CategoryCRUD categoryCRUD = getCategoryCRUD();
            categoryCRUD.setDbFilter(dbFilter);
            JSONObject data = currentCategory.toJSON(Boolean.TRUE, Boolean.TRUE);
            successResponse = new SuccessResponse();
            if (data.has(ProductConstants.LowerCase.DATA)
                    && data.getJSONArray(ProductConstants.LowerCase.DATA).isEmpty()) {
                throw new Exception(CategoryConstants.ExceptionMessageCase.INVALID_CATEGORY_ID);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/category", method = RequestMethod.POST)
    public ResponseEntity<String> createCategory(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            CategoryCRUD categoryCRUD = getCategoryCRUD();
            JSONObject validationResult = categoryCRUD.checkBodyOfCreateCategory(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ProductConstants.ExceptionMessageCase.MISSING_PRODUCT_FIELD_FOR_CREATE);
            }
            Category category = categoryCRUD.createCategory(body.get(ProductConstants.LowerCase.NAME).toString(),
                    body.get(ProductConstants.LowerCase.DESCRIPTION).toString(), currentAnimal);
            successResponse = new SuccessResponse();
            if (category == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.CREATED);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, category.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                otherDataControllerExceptions.setData(errorData);
            }
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/category/{categoryId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateCategory(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            CategoryCRUD categoryCRUD = getCategoryCRUD();

            Category category = categoryCRUD.updateCategory(currentCategory.getId(),
                    JSONUtils.optString(body, ProductConstants.LowerCase.NAME, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.DESCRIPTION, null),
                    null);

            successResponse = new SuccessResponse();
            if (category == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, category.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/category/{categoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCategory() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            CategoryCRUD categoryCRUD = getCategoryCRUD();

            Boolean isDeleted = categoryCRUD.deleteCategoryById(currentCategory.getId());

            successResponse = new SuccessResponse();
            if (!isDeleted) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentCategory.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/category/{categoryId}/image", method = RequestMethod.PUT)
    public ResponseEntity<String> addCategoryImage(
            @RequestParam(ProductConstants.SnakeCase.IMAGE_TYPE) String imageType,
            @RequestPart(required = false) MultipartFile categoryImage,
            @RequestBody(required = false) HashMap<String, String> requestBody) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ParameterCheck paramCheck = new ParameterCheck();
            paramCheck.checkParameterForSingleUrlImage(imageType, categoryImage, requestBody);
            Boolean isFile = paramCheck.isFile(imageType);
            CategoryCRUD categoryCRUD = getCategoryCRUD();
            if (isFile) {
                String originalFilename = categoryImage.getOriginalFilename();
                if (!categoryCRUD.isValidFileExtension(originalFilename)) {
                    throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_IMAGE_FORMAT);
                }
                categoryCRUD.addImageToCategory(currentCategory, categoryImage.getInputStream(),
                        categoryCRUD.getExtension(originalFilename));
            } else {
                categoryCRUD.addImageUrlToCategory(currentCategory, requestBody.get("url"));
            }

            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentCategory.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/animal/{animalId}/category/{categoryId}/image", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCategoryImage() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            CategoryCRUD categoryCRUD = getCategoryCRUD();
            categoryCRUD.deleteImageFromCategory(currentCategory);

            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentCategory.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            OtherDataControllerExceptions otherDataControllerExceptions = new OtherDataControllerExceptions();
            otherDataControllerExceptions.setException(ex);
            return otherDataControllerExceptions.returnResponseBasedOnException();
        }
    }
}
