package com.happidreampets.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ProductConstants.SnakeCase;
import com.happidreampets.app.database.crud.ProductCRUD;
import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.Product.VARIANT_TYPE;
import com.happidreampets.app.database.model.Product.WEIGHT_UNITS;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;
import com.happidreampets.app.utils.AccessLevel;
import com.happidreampets.app.utils.JSONUtils;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/animal/{animalId}/category/{categoryId}")
public class ProductController extends APIController {
    private Animal currentAnimal;
    private Category currentCategory;
    private Product currentProduct;

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

    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void setCurrentProduct(Product currentProduct) {
        this.currentProduct = currentProduct;
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
        return failureResponse.throwMandatoryMissing();
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public ResponseEntity<String> getProducts(
            @RequestParam(value = ProductConstants.SnakeCase.PRICE_MIN, required = false) Long minPrice,
            @RequestParam(value = ProductConstants.SnakeCase.PRICE_MAX, required = false) Long maxPrice,
            @RequestParam(value = ProductConstants.SnakeCase.SORT_ORDER, required = false) String sort_order,
            @RequestParam(value = ProductConstants.SnakeCase.SORT_BY, required = false) String sort_by,
            @RequestParam(value = ProductConstants.LowerCase.PAGE, defaultValue = "1", required = true) Integer page,
            @RequestParam(value = ProductConstants.SnakeCase.PER_PAGE, defaultValue = "6", required = true) Integer per_page,
            @RequestParam(value = ProductConstants.SnakeCase.SKIP_VISIBILITY, defaultValue = "false", required = false) Boolean skip_visibility) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ParameterCheck paramCheck = new ParameterCheck();
            paramCheck.checkSortByAndSortOrderProductParameter(sort_order, sort_by);
            if (minPrice != null && minPrice <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_PRICE_MIN_VALUE);
            }
            if (maxPrice != null && maxPrice <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_PRICE_MAX_VALUE);
            }
            if (page <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.PAGE_GREATER_THAN_ZERO);
            }
            if (per_page <= 0) {
                throw new Exception(ProductConstants.ExceptionMessageCase.PER_PAGE_GREATER_THAN_ZERO);
            }
            if (skip_visibility) {
                if (!getUserCRUD().isUserAdmin(getCurrentUser())) {
                    skip_visibility = false;
                }
            }

            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(page - 1);
            dbFilter.setLimitIndex(per_page);
            if (sort_order != null && sort_by != null) {
                dbFilter.setSortColumn(paramCheck.getProductColumnBasedOnSortBy(sort_by));
                dbFilter.setSortDirection(paramCheck.getSortOrder(sort_order));
            }
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.setDbFilter(dbFilter);
            JSONObject data = productCRUD.getProductDetailsForUI(getCurrentCategory(), minPrice, maxPrice,
                    skip_visibility);
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
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/products/all", method = RequestMethod.GET)
    public ResponseEntity<String> getAllProducts(
            @RequestParam(value = ProductConstants.SnakeCase.SKIP_VISIBILITY, defaultValue = "false", required = false) Boolean skip_visibility) {
        SuccessResponse successResponse = new SuccessResponse();
        try {

            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.setDbFilter(dbFilter);
            JSONObject data = productCRUD.getAllProductDetailsForVariation(getCurrentCategory(),
                    skip_visibility);
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
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER, AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}", method = RequestMethod.GET)
    public ResponseEntity<String> getProduct(
            @RequestParam(value = ProductConstants.SnakeCase.SKIP_VISIBILITY, defaultValue = "false", required = false) Boolean skip_visibility) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            if (skip_visibility) {
                if (!getUserCRUD().isUserAdmin(getCurrentUser())) {
                    skip_visibility = false;
                }
            }

            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(0);
            dbFilter.setLimitIndex(0);
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.setDbFilter(dbFilter);
            JSONObject data = productCRUD.getProductForUI(currentProduct.getId(), getCurrentCategory(),
                    skip_visibility);
            successResponse = new SuccessResponse();
            if (!data.has(ProductConstants.LowerCase.DATA)) {
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product", method = RequestMethod.POST)
    public ResponseEntity<String> createProduct(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            ProductCRUD productCRUD = getProductCRUD();
            JSONObject validationResult = productCRUD.checkBodyOfCreateProduct(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ProductConstants.ExceptionMessageCase.MISSING_PRODUCT_FIELD_FOR_CREATE);
            }
            WEIGHT_UNITS weightUnit = WEIGHT_UNITS.getEnumValueFromGivenString(
                    JSONUtils.optString(body, ProductConstants.CamelCase.WEIGHTUNITS, null));
            Product product = productCRUD.createProduct(getCurrentCategory(),
                    body.get(ProductConstants.LowerCase.NAME).toString(),
                    body.get(ProductConstants.LowerCase.DESCRIPTION).toString(),
                    JSONUtils.optString(body, ProductConstants.SnakeCase.RICH_TEXT_DETAILS, null),
                    body.get(ProductConstants.LowerCase.DETAILS).toString(),
                    JSONUtils.optString(body, ProductConstants.LowerCase.COLOR, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.SIZE, null),
                    weightUnit,
                    JSONUtils.optInteger(body, ProductConstants.LowerCase.WEIGHT, -1),
                    JSONUtils.optLong(body, ProductConstants.LowerCase.STOCKS, null),
                    JSONUtils.optLong(body, ProductConstants.LowerCase.PRICE, null));
            successResponse = new SuccessResponse();
            if (product == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.CREATED);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, product.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                productControllerExceptions.setData(errorData);
            }
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateProduct(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            ProductCRUD productCRUD = getProductCRUD();
            JSONObject validationResult = productCRUD.checkBodyOfUpdateProduct(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ProductConstants.ExceptionMessageCase.MISSING_PRODUCT_FIELD_FOR_UPDATE);
            }
            Boolean isColorUpdated = body.has(ProductConstants.LowerCase.COLOR);
            Boolean isSizeUpdated = body.has(ProductConstants.LowerCase.SIZE);
            Boolean isWeightUpdated = body.has(ProductConstants.CamelCase.WEIGHTUNITS)
                    && body.has(ProductConstants.LowerCase.WEIGHT);

            WEIGHT_UNITS weightUnit = null;
            if (isWeightUpdated) {
                weightUnit = WEIGHT_UNITS.getEnumValueFromGivenString(
                        JSONUtils.optString(body, ProductConstants.CamelCase.WEIGHTUNITS, null));
            }

            Product product = productCRUD.updateProduct(currentProduct.getId(),
                    JSONUtils.optString(body, ProductConstants.LowerCase.NAME, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.DESCRIPTION, null),
                    JSONUtils.optString(body, ProductConstants.SnakeCase.RICH_TEXT_DETAILS, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.DETAILS, null),
                    isColorUpdated,
                    JSONUtils.optString(body, ProductConstants.LowerCase.COLOR, null),
                    isSizeUpdated,
                    JSONUtils.optString(body, ProductConstants.LowerCase.SIZE, null),
                    isWeightUpdated,
                    weightUnit,
                    JSONUtils.optInteger(body, ProductConstants.LowerCase.WEIGHT, null),
                    JSONUtils.optLong(body, ProductConstants.LowerCase.STOCKS, null),
                    JSONUtils.optLong(body, ProductConstants.LowerCase.PRICE, null), null, null, null, null);
            successResponse = new SuccessResponse();
            if (product == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, product.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                productControllerExceptions.setData(errorData);
            }
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}/image", method = RequestMethod.PUT)
    public ResponseEntity<String> addProductImage(
            @RequestParam(ProductConstants.SnakeCase.IMAGE_TYPE) String imageType,
            @RequestPart(required = false) MultipartFile productImage,
            @RequestBody(required = false) List<HashMap<String, String>> requestBody) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ParameterCheck paramCheck = new ParameterCheck();
            paramCheck.checkParameterForMultipleUrlImage(imageType, productImage, requestBody);
            Boolean isFile = paramCheck.isFile(imageType);
            ProductCRUD productCRUD = getProductCRUD();
            if (isFile) {
                String originalFilename = productImage.getOriginalFilename();
                if (!productCRUD.isValidFileExtension(originalFilename)) {
                    throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_IMAGE_FORMAT);
                }
                productCRUD.addImageToProduct(currentProduct, productImage.getInputStream(),
                        productCRUD.getExtension(originalFilename));
            } else {
                productCRUD.addImageUrlToProduct(currentProduct, paramCheck.extractImageUrlFromHashmap(requestBody));
            }

            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentProduct.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}/images", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteProductImages(@RequestBody HashMap<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            ProductCRUD productCRUD = getProductCRUD();
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            JSONObject validationResult = productCRUD.checkBodyOfDeleteImages(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_IMAGE_IDS_FOR_DELETE);
            }
            List<Long> imageIds = JSONUtils.convertJSONToListLong(body, SnakeCase.IMAGE_IDS);
            productCRUD.checkImageIds(currentProduct, imageIds);
            for (Long imageId : imageIds) {
                productCRUD.deleteImageFromProduct(currentProduct, imageId);
            }

            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentProduct.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                productControllerExceptions.setData(errorData);
            }
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}/image/{imageId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteProductImage(@PathVariable("imageId") Long imageId) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.deleteImageFromProduct(currentProduct, imageId);

            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentProduct.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}/image/{imageId}/thumbnail", method = RequestMethod.PUT)
    public ResponseEntity<String> updateProductImageAsThumbnail(@PathVariable("imageId") Long imageId) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ProductCRUD productCRUD = getProductCRUD();

            productCRUD.makeImageAsThumbnail(currentProduct, imageId);

            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentProduct.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteProduct() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ProductCRUD productCRUD = getProductCRUD();
            Boolean isDeleted = productCRUD.deleteProductById(currentProduct.getId());
            successResponse = new SuccessResponse();
            if (!isDeleted) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, currentProduct.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER, AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}/variation", method = RequestMethod.GET)
    public ResponseEntity<String> getProductVariations() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.setDbFilter(dbFilter);
            JSONObject data = productCRUD.getProductVariantDetailsForUI(currentProduct.getId(), getCurrentCategory());
            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setResponseData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}/variation", method = RequestMethod.POST)
    public ResponseEntity<String> createProductVariation(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);
            ProductCRUD productCRUD = getProductCRUD();
            JSONObject validationResult = productCRUD.checkBodyOfCreateVariation(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_BODY_FOR_CREATE_VARIATION);
            }
            VARIANT_TYPE variantType = VARIANT_TYPE
                    .getEnumValueFromGivenString(bodyData.get(ProductConstants.SnakeCase.VARIANT_TYPE).toString());

            productCRUD.createProductVariant(currentProduct,
                    (Product) validationResult.get(ProductConstants.LowerCase.PRODUCT), variantType);
            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                productControllerExceptions.setData(errorData);
            }
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/{productId}/variation", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteProductVariation(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);
            ProductCRUD productCRUD = getProductCRUD();
            JSONObject validationResult = productCRUD.checkBodyOfDeleteVariation(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_BODY_FOR_DELETE_VARIATION);
            }
            VARIANT_TYPE variantType = VARIANT_TYPE
                    .getEnumValueFromGivenString(bodyData.get(ProductConstants.SnakeCase.VARIANT_TYPE).toString());

            productCRUD.deleteProductVariant(currentProduct, variantType);
            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                productControllerExceptions.setData(errorData);
            }
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/product/variation/list", method = RequestMethod.GET)
    public ResponseEntity<String> getProductsWithoutVariation(
            @RequestParam(value = ProductConstants.SnakeCase.VARIANT_TYPE, required = true) String variationType) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            VARIANT_TYPE variantType = VARIANT_TYPE.getEnumValueFromGivenString(variationType);

            if (variantType == null) {
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_VARIANT_TYPE);
            }

            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.setDbFilter(dbFilter);
            JSONObject data = productCRUD.getAllAvailableProductDetailsForGivenVariation(getCurrentCategory(),
                    variantType,
                    false);
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
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }
}
