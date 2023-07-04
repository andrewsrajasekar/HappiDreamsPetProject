package com.happidreampets.app.database.crud;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

import com.happidreampets.app.constants.ProductConstants.CamelCase;
import com.happidreampets.app.constants.ProductConstants.LoggerCase;
import com.happidreampets.app.constants.ProductConstants.LowerCase;
import com.happidreampets.app.constants.ProductConstants.MessageCase;
import com.happidreampets.app.constants.ProductConstants.OtherCase;
import com.happidreampets.app.constants.ProductConstants.SnakeCase;
import com.happidreampets.app.constants.ProductConstants.SpecialCharacter;
import com.happidreampets.app.constants.ProductConstants.ExceptionMessageCase;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.database.model.Category;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.Product.PRODUCTCOLUMN;
import com.happidreampets.app.database.model.ProductImage;
import com.happidreampets.app.database.model.Product.WEIGHT_UNITS;
import com.happidreampets.app.database.repository.ProductRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

@Component
public class ProductCRUD {

    @Value("${products.image.size}")
    Integer productImagesSize;

    public static final String PRODUCTS_IMAGE_LOCATION_FULL = System.getProperty(OtherCase.USER_DOT_DIR)
            + "/src/main/resources" + SpecialCharacter.SLASH + LowerCase.STATIC + SpecialCharacter.SLASH
            + LowerCase.PRODUCTS;
    public static final String PRODUCTS_IMAGE_LOCATION_BEFORE_STATIC = System.getProperty(OtherCase.USER_DOT_DIR)
            + "/src/main/resources";
    public static final String PRODUCTS_IMAGE_LOCATION_FROM_STATIC = LowerCase.STATIC + SpecialCharacter.SLASH
            + LowerCase.PRODUCTS;

    @Autowired
    private ProductRepository productRepository;

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private PRODUCTCOLUMN checkAndGetColumnName() {
        if (dbFilter != null && dbFilter.getSortColumn() != null) {
            if (EnumUtils.isValidEnum(PRODUCTCOLUMN.class, dbFilter.getSortColumn().toString())) {
                PRODUCTCOLUMN enumValue = PRODUCTCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<Product> data) {
        JSONObject responseData = new JSONObject();
        responseData.put(LowerCase.DATA, JSONObject.NULL);
        if (getDbFilter() != null) {
            if (getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
                JSONArray responseArray = new JSONArray();
                data.forEach(row -> {
                    responseArray.put(row.toJSON());
                });
                responseData.put(LowerCase.DATA, responseArray);
            } else if (getDbFilter().getFormat().equals(DATAFORMAT.POJO)) {
                List<Product> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(LowerCase.DATA, responseList);
            }
        } else {
            List<Product> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<Product> productPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(LowerCase.PAGE, productPage.getNumber() + 1);
        pageData.put(SnakeCase.PER_PAGE, productPage.getSize());
        pageData.put(LowerCase.COUNT, productPage.getContent().size());
        pageData.put(SnakeCase.MORE_RECORDS, productPage.hasNext());
        return pageData;
    }

    public JSONObject getProductDetailsForUI(Category category) {
        JSONObject productData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<Product> productPage = productRepository.findAllProductsForUIByCategory(pageable, category);
        Iterable<Product> productIterable = productPage.getContent();
        productData.put(LowerCase.DATA, getDataInRequiredFormat(productIterable).get(LowerCase.DATA));
        productData.put(LowerCase.INFO, getPageData(productPage));
        return productData;
    }

    public JSONObject getProductForUI(Long id, Category category) {
        JSONObject productData = new JSONObject();
        Product product = productRepository.findProductForUIByCategory(id, category);
        if (getDbFilter() != null && getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
            productData.put(LowerCase.DATA, product != null ? product.toJSON() : product);
        } else {
            productData.put(LowerCase.DATA, product);
        }
        return productData;
    }

    public Boolean checkProductExistsForCategory(Long id, Category category) {
        JSONObject product = getProductForUI(id, category);
        if (product.has(LowerCase.DATA)) {
            if (product.get(LowerCase.DATA) != null) {
                return true;
            }
        }
        return false;
    }

    public Product getProduct(Long id, Category category) {
        Product product = productRepository.findByIdAndCategory(id, category);
        return product;
    }

    public Product getProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    public Product createProduct(Category category, String name, String description, String details, String color,
            String size, WEIGHT_UNITS weight_units, Integer weight, Long stocks, Long price) throws Exception {
        return createProduct(category, name, description, details, color, size, weight_units, weight, stocks, price,
                true, null, null, null);
    }

    public Product createVariantProduct(Category category, String name, String description, String details,
            String color, String size, WEIGHT_UNITS weight_units, Integer weight, Long stocks, Long price,
            Boolean isVisible, Long variant_size_id, Long variant_color_id, Long variant_weight_id) throws Exception {
        return createProduct(category, name, description, details, color, size, weight_units, weight, stocks, price,
                isVisible, variant_size_id, variant_color_id, variant_weight_id);
    }

    private Product createProduct(Category category, String name, String description, String details, String color,
            String size, WEIGHT_UNITS weight_units, Integer weight, Long stocks, Long price, Boolean isVisible,
            Long variant_size_id, Long variant_color_id, Long variant_weight_id) throws Exception {
        if (category == null) {
            throw new Exception(ExceptionMessageCase.CATEGORY_CANNOT_BE_EMPTY);
        }
        if (name == null) {
            throw new Exception(ExceptionMessageCase.NAME_CANNOT_BE_EMPTY);
        }
        if (stocks == null) {
            throw new Exception(ExceptionMessageCase.STOCKS_CANNOT_BE_EMPTY);
        }
        if (price == null) {
            throw new Exception(ExceptionMessageCase.PRICE_CANNOT_BE_EMPTY);
        }
        Product product = new Product();
        product.setCategory(category);
        product.setName(name);
        product.setDescription(description);
        product.setDetails(details);
        if (color != null) {
            product.setColor(color);
        }
        if (size != null) {
            product.setSize(size);
        }
        product.setWeightUnits(WEIGHT_UNITS.NONE);
        product.setWeight(-1);
        if (!(weight_units != null && weight_units.equals(WEIGHT_UNITS.NONE))) {
            if (weight != null && weight > 0) {
                product.setWeightUnits(weight_units);
                product.setWeight(weight);
            }
        }
        product.setStocks(stocks);
        product.setPrice(price);
        product.setIsVisible(isVisible);
        if (variant_color_id != null) {
            product.setVariantColorId(variant_color_id);
        }
        if (variant_size_id != null) {
            product.setVariantSizeId(variant_size_id);
        }
        if (variant_weight_id != null) {
            product.setVariantWeightId(variant_weight_id);
        }
        product.setToBeDeleted(false);
        product.setAddedTime(System.currentTimeMillis());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, String name, String description, String details, String color, String size,
            WEIGHT_UNITS weight_units, Integer weight, Long stocks, Long price, Boolean isVisible, Long variant_size_id,
            Long variant_color_id, Long variant_weight_id) throws Exception {
        Product product = productRepository.findById(id).orElse(null);
        if (null == product) {
            throw new Exception(ExceptionMessageCase.PRODUCT_NOT_FOUND);
        }
        if (name != null) {
            product.setName(name);
        }
        if (description != null) {
            product.setDescription(description);
        }
        if (details != null) {
            product.setDetails(details);
        }
        if (color != null) {
            product.setColor(color);
        }
        if (size != null) {
            product.setSize(size);
        }
        product.setWeightUnits(WEIGHT_UNITS.NONE);
        product.setWeight(-1);
        if (!(weight_units != null && weight_units.equals(WEIGHT_UNITS.NONE))) {
            if (weight != null) {
                product.setWeightUnits(weight_units);
                product.setWeight(weight);
            }
        }
        if (stocks != null) {
            product.setStocks(stocks);
        }
        if (price != null) {
            product.setPrice(price);
        }
        if (isVisible != null) {
            product.setIsVisible(isVisible);
        }
        if (variant_color_id != null) {
            product.setVariantColorId(variant_color_id);
        }
        if (variant_size_id != null) {
            product.setVariantSizeId(variant_size_id);
        }
        if (variant_weight_id != null) {
            product.setVariantWeightId(variant_weight_id);
        }
        return productRepository.save(product);
    }

    public Boolean deleteProductByName(String name) throws Exception {
        List<Product> products = productRepository.findByNameAndToBeDeletedIsFalse(name);
        if (products.isEmpty()) {
            throw new Exception(ExceptionMessageCase.PRODUCT_NOT_FOUND);
        }
        for (Product product : products) {
            product.setToBeDeleted(true);
            product.setToBeDeletedStatusChangeTime(System.currentTimeMillis());
            productRepository.save(product);
        }
        return true;
    }

    public Boolean deleteProductById(Long id) throws Exception {
        Product product = productRepository.findByIdAndToBeDeletedIsFalse(id);
        if (null == product) {
            throw new Exception(ExceptionMessageCase.PRODUCT_NOT_FOUND);
        }
        product.setToBeDeleted(true);
        product.setToBeDeletedStatusChangeTime(System.currentTimeMillis());
        productRepository.save(product);
        return true;
    }

    public JSONObject checkBodyOfCreateProduct(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = LowerCase.EMPTY_QUOTES;
        String message = MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(LowerCase.NAME)) {
            missingField = LowerCase.NAME;
            message = message.replace(LoggerCase.ARG0, LowerCase.NAME);
        } else if (!body.has(LowerCase.DESCRIPTION)) {
            missingField = LowerCase.DESCRIPTION;
            message = message.replace(LoggerCase.ARG0, LowerCase.DESCRIPTION);
        } else if (!body.has(LowerCase.DETAILS)) {
            missingField = LowerCase.DETAILS;
            message = message.replace(LoggerCase.ARG0, LowerCase.DETAILS);
        } else if (body.has(CamelCase.WEIGHTUNITS) && !body.has(LowerCase.WEIGHT)) {
            missingField = LowerCase.WEIGHT;
            message = message.replace(LoggerCase.ARG0, LowerCase.WEIGHT);
        } else if (!body.has(LowerCase.STOCKS)) {
            missingField = LowerCase.STOCKS;
            message = message.replace(LoggerCase.ARG0, LowerCase.STOCKS);
        } else if (!body.has(LowerCase.PRICE)) {
            missingField = LowerCase.PRICE;
            message = message.replace(LoggerCase.ARG0, LowerCase.PRICE);
        } else {
            Boolean isError = false;
            if (body.get(LowerCase.STOCKS) instanceof Integer) {
                Integer stocks = Integer.parseInt(body.get(LowerCase.STOCKS).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.STOCKS;
                    isError = true;
                }
            } else if (body.get(LowerCase.STOCKS) instanceof Long) {
                Long stocks = Long.parseLong(body.get(LowerCase.STOCKS).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.STOCKS;
                    isError = true;
                }
            } else {
                code = null;
                message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = LowerCase.STOCKS;
                isError = true;
            }

            if (body.get(LowerCase.PRICE) instanceof Integer) {
                Integer stocks = Integer.parseInt(body.get(LowerCase.PRICE).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.PRICE;
                    isError = true;
                }
            } else if (body.get(LowerCase.PRICE) instanceof Long) {
                Long stocks = Long.parseLong(body.get(LowerCase.PRICE).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.PRICE;
                    isError = true;
                }
            } else {
                code = null;
                message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = LowerCase.PRICE;
                isError = true;
            }

            if (body.has(CamelCase.WEIGHTUNITS)) {
                if (body.get(LowerCase.WEIGHT) instanceof Integer) {
                    Integer stocks = Integer.parseInt(body.get(LowerCase.WEIGHT).toString());
                    if (stocks <= 0) {
                        code = null;
                        message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                        missingField = LowerCase.WEIGHT;
                        isError = true;
                    }
                } else if (body.get(LowerCase.PRICE) instanceof Long) {
                    code = null;
                    message = MessageCase.THE_VALUE_IS_TOO_HIGH;
                    missingField = LowerCase.WEIGHT;
                    isError = true;
                } else {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                    missingField = LowerCase.WEIGHT;
                    isError = true;
                }
            }
            if (!isError) {
                isSuccess = Boolean.TRUE;
            }
        }
        response.put(LowerCase.SUCCESS, isSuccess);
        if (!isSuccess) {
            response.put(LowerCase.DATA, new JSONObject().put(LowerCase.FIELD, missingField).put(LowerCase.CODE, code)
                    .put(LowerCase.MESSAGE, message));
        }
        return response;
    }

    public JSONObject checkBodyOfUpdateProduct(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = LowerCase.EMPTY_QUOTES;
        String message = MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        Boolean isError = false;
        if (body.has(LowerCase.STOCKS)) {
            if (body.get(LowerCase.STOCKS) instanceof Integer) {
                Integer stocks = Integer.parseInt(body.get(LowerCase.STOCKS).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.STOCKS;
                    isError = true;
                }
            } else if (body.get(LowerCase.STOCKS) instanceof Long) {
                Long stocks = Long.parseLong(body.get(LowerCase.STOCKS).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.STOCKS;
                    isError = true;
                }
            } else {
                code = null;
                message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = LowerCase.STOCKS;
                isError = true;
            }
        }
        if (body.has(LowerCase.PRICE)) {
            if (body.get(LowerCase.PRICE) instanceof Integer) {
                Integer stocks = Integer.parseInt(body.get(LowerCase.PRICE).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.PRICE;
                    isError = true;
                }
            } else if (body.get(LowerCase.PRICE) instanceof Long) {
                Long stocks = Long.parseLong(body.get(LowerCase.PRICE).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.PRICE;
                    isError = true;
                }
            } else {
                code = null;
                message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = LowerCase.PRICE;
                isError = true;
            }
        }

        if (body.has(CamelCase.WEIGHTUNITS)) {
            if (body.get(LowerCase.WEIGHT) instanceof Integer) {
                Integer stocks = Integer.parseInt(body.get(LowerCase.WEIGHT).toString());
                if (stocks <= 0) {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_GREATER_THAN_0;
                    missingField = LowerCase.WEIGHT;
                    isError = true;
                }
            } else if (body.get(LowerCase.PRICE) instanceof Long) {
                code = null;
                message = MessageCase.THE_VALUE_IS_TOO_HIGH;
                missingField = LowerCase.WEIGHT;
                isError = true;
            } else {
                code = null;
                message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                missingField = LowerCase.WEIGHT;
                isError = true;
            }
        }
        if (!isError) {
            isSuccess = Boolean.TRUE;
        }
        response.put(LowerCase.SUCCESS, isSuccess);
        if (!isSuccess) {
            response.put(LowerCase.DATA, new JSONObject().put(LowerCase.FIELD, missingField).put(LowerCase.CODE, code)
                    .put(LowerCase.MESSAGE, message));
        }
        return response;
    }

    public Boolean isValidFileExtension(String fileFullName) {
        String extension = fileFullName.substring(fileFullName.lastIndexOf(SpecialCharacter.DOT) + 1);
        List<String> allowedExtension = new ArrayList<>();
        allowedExtension.add(LowerCase.JPG);
        allowedExtension.add(LowerCase.JPEG);
        allowedExtension.add(LowerCase.PNG);
        return allowedExtension.contains(extension);
    }

    public String getExtension(String fileFullName) {
        return fileFullName.substring(fileFullName.lastIndexOf(SpecialCharacter.DOT) + 1);
    }

    public void deleteImageFromProduct(Product product, Long imageId) throws Exception {
        String PRODUCT_IMAGE_FOLDER = PRODUCTS_IMAGE_LOCATION_BEFORE_STATIC;
        List<ProductImage> existingImages = product.getImageUrls();
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }
        ProductImage currentImage = null;
        for (ProductImage data : existingImages) {
            if (data.getId() >= imageId) {
                currentImage = data;
                break;
            }
        }
        if (currentImage == null) {
            throw new Exception(ExceptionMessageCase.IMAGE_ID_NOT_FOUND_FOR_THE_PRODUCT);
        }
        Path path = Paths.get(
                PRODUCT_IMAGE_FOLDER + SpecialCharacter.SLASH + currentImage.getImageUrl());
        deleteImageFromServer(path);
        existingImages.remove(currentImage);
        product.setImageUrls(existingImages.isEmpty() ? null : existingImages);
        if (currentImage.getId().equals(product.getThumbnailImageUrl().getId())) {
            product.setThumbnailImageUrl(null);
        }
        productRepository.save(product);
    }

    public void addImageToProduct(Product product, InputStream image, String extension) throws Exception {
        String PRODUCT_IMAGE_FOLDER = PRODUCTS_IMAGE_LOCATION_FULL;
        List<ProductImage> existingImages = product.getImageUrls();
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }
        ProductImage newImage = new ProductImage();
        if (existingImages.size() >= productImagesSize) {
            throw new Exception(ExceptionMessageCase.MAXIMUM_IMAGES_FOR_PRODUCT_REACHED);
        }
        Long nextImageId = 1l;
        for (ProductImage data : existingImages) {
            if (data.getId() >= nextImageId) {
                nextImageId = data.getId() + 1;
            }
        }
        newImage.setId(nextImageId);
        Path path = Paths.get(
                PRODUCT_IMAGE_FOLDER + SpecialCharacter.SLASH + getFileName(nextImageId, product.getId(), extension));
        addImageToServer(image, path);
        newImage.setImageUrl(PRODUCTS_IMAGE_LOCATION_FROM_STATIC + SpecialCharacter.SLASH
                + getFileName(nextImageId, product.getId(), extension));
        existingImages.add(newImage);
        product.setImageUrls(existingImages);
        productRepository.save(product);
    }

    public void addImageToServer(InputStream image, Path path) throws IOException {
        Files.copy(image, path);
    }

    public void deleteImageFromServer(Path path) throws IOException {
        Files.delete(path);
    }

    public String getFileName(Long imageId, Long productId, String extension) {
        return productId + SpecialCharacter.UNDERSCORE + imageId + SpecialCharacter.DOT + extension;
    }

    public void makeImageAsThumbnail(Product product, Long imageId) throws Exception {
        List<ProductImage> existingImages = product.getImageUrls();
        if (existingImages == null) {
            throw new Exception(ExceptionMessageCase.NO_IMAGE_ASSOCIATED_WITH_PRODUCT);
        }
        ProductImage productImage = null;
        for (ProductImage image : existingImages) {
            if (imageId == image.getId()) {
                productImage = image;
            }
        }
        if (productImage == null) {
            throw new Exception(ExceptionMessageCase.IMAGE_ID_NOT_FOUND_FOR_THE_PRODUCT);
        }
        product.setThumbnailImageUrl(productImage);
        productRepository.save(product);
    }
}
