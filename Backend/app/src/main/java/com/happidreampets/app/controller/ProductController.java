package com.happidreampets.app.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.database.crud.ProductCRUD;
import com.happidreampets.app.database.model.Animal;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.Product.WEIGHT_UNITS;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;
import com.happidreampets.app.utils.JSONUtils;

@RestController
@RequestMapping("animal/{animalId}/category/{categoryId}")
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

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException ex) {
        FailureResponse failureResponse = new FailureResponse();
        failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
        failureResponse.setData(new JSONObject().put(LowerCase.ERROR, "Invalid request format. Please upload a file."));
        return failureResponse.getResponse();
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public ResponseEntity<String> getProducts(
            @RequestParam(value = ProductConstants.LowerCase.PAGE, defaultValue = "1", required = true) Integer page,
            @RequestParam(value = ProductConstants.SnakeCase.PER_PAGE, defaultValue = "6", required = true) Integer per_page) {
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
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.setDbFilter(dbFilter);
            JSONObject data = productCRUD.getProductDetailsForUI(getCurrentCategory());
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

    @RequestMapping(value = "/product/{productId}", method = RequestMethod.GET)
    public ResponseEntity<String> getProduct() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(0);
            dbFilter.setLimitIndex(0);
            ProductCRUD productCRUD = getProductCRUD();
            productCRUD.setDbFilter(dbFilter);
            JSONObject data = productCRUD.getProductForUI(currentProduct.getId(), getCurrentCategory());
            successResponse = new SuccessResponse();
            if (!data.has(ProductConstants.LowerCase.DATA)) {
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID);
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(data);
            return successResponse.getResponse();
        } catch (Exception ex) {
            ProductControllerExceptions productControllerExceptions = new ProductControllerExceptions();
            productControllerExceptions.setException(ex);
            return productControllerExceptions.returnResponseBasedOnException();
        }
    }

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
            Product product = productCRUD.createProduct(getCurrentCategory(),
                    body.get(ProductConstants.LowerCase.NAME).toString(),
                    body.get(ProductConstants.LowerCase.DESCRIPTION).toString(),
                    body.get(ProductConstants.LowerCase.DETAILS).toString(),
                    JSONUtils.optString(body, ProductConstants.LowerCase.COLOR, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.SIZE, null),
                    JSONUtils.optEnum(body, ProductConstants.CamelCase.WEIGHTUNITS, WEIGHT_UNITS.class),
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
            Product product = productCRUD.updateProduct(currentProduct.getId(),
                    JSONUtils.optString(body, ProductConstants.LowerCase.NAME, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.DESCRIPTION, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.DETAILS, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.COLOR, null),
                    JSONUtils.optString(body, ProductConstants.LowerCase.SIZE, null),
                    JSONUtils.optEnum(body, ProductConstants.CamelCase.WEIGHTUNITS, WEIGHT_UNITS.class),
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

    @RequestMapping(value = "/product/{productId}/image", method = RequestMethod.POST)
    public ResponseEntity<String> addProductImage(
            @RequestParam(ProductConstants.LowerCase.FILE) MultipartFile productImage) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            ProductCRUD productCRUD = getProductCRUD();
            String originalFilename = productImage.getOriginalFilename();
            if (!productCRUD.isValidFileExtension(originalFilename)) {
                throw new Exception(ProductConstants.ExceptionMessageCase.INVALID_IMAGE_FORMAT);
            }
            productCRUD.addImageToProduct(currentProduct, productImage.getInputStream(),
                    productCRUD.getExtension(originalFilename));

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

    @RequestMapping(value = "/product/{productId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> updateProduct() {
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
}
