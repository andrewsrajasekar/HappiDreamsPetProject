package com.happidreampets.app.database.crud;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happidreampets.app.constants.AnimalConstants;
import com.happidreampets.app.constants.CategoryConstants;
import com.happidreampets.app.constants.ColorVariantConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.SizeVariantConstants;
import com.happidreampets.app.constants.WeightVariantConstants;
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
import com.happidreampets.app.database.model.ColorVariant;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.Product.PRODUCTCOLUMN;
import com.happidreampets.app.database.model.Product.VARIANT_TYPE;
import com.happidreampets.app.database.model.ProductImage;
import com.happidreampets.app.database.model.SizeVariant;
import com.happidreampets.app.database.model.WeightVariant;
import com.happidreampets.app.database.model.Product.WEIGHT_UNITS;
import com.happidreampets.app.database.repository.ProductRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;
import com.happidreampets.app.utils.JSONUtils;

import jakarta.transaction.Transactional;

@Component
public class ProductCRUD {

    @Value("${products.image.size}")
    Integer productImagesSize;

    @Value("${weight.variant.association.limit}")
    Integer weightVariantAssociationLimit;

    @Value("${size.variant.association.limit}")
    Integer sizeVariantAssociationLimit;

    @Value("${color.variant.association.limit}")
    Integer colorVariantAssociationLimit;

    public static final String PRODUCTS_IMAGE_LOCATION_FULL = System.getProperty(OtherCase.USER_DOT_DIR)
            + "/src/main/resources" + SpecialCharacter.SLASH + LowerCase.STATIC + SpecialCharacter.SLASH
            + LowerCase.PRODUCTS;
    public static final String PRODUCTS_IMAGE_LOCATION_BEFORE_STATIC = System.getProperty(OtherCase.USER_DOT_DIR)
            + "/src/main/resources";
    public static final String PRODUCTS_IMAGE_LOCATION_FROM_STATIC = LowerCase.STATIC + SpecialCharacter.SLASH
            + LowerCase.PRODUCTS;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WeightVariantCRUD weightVariantCRUD;

    @Autowired
    private ColorVariantCRUD colorVariantCRUD;

    @Autowired
    private SizeVariantCRUD sizeVariantCRUD;

    @Autowired
    private AnimalCRUD animalCRUD;

    @Autowired
    private CategoryCRUD categoryCRUD;

    private ObjectMapper objectMapper;

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    public ProductCRUD(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
                    responseArray.put(row.toJSON(Boolean.TRUE, Boolean.TRUE));
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
        pageData.put(ProductConstants.SnakeCase.TOTAL_RECORDS, productPage.getTotalElements());
        return pageData;
    }

    public JSONObject getProductDetailsForUI(Category category, Long minPrice, Long maxPrice, Boolean skipVisibility) {
        JSONObject productData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<Product> productPage = null;
        if (minPrice != null || maxPrice != null) {
            productPage = productRepository.findAllProductsForUIByCategoryByMinAndMaxPrice(pageable, category,
                    minPrice != null ? minPrice : Long.MIN_VALUE, maxPrice != null ? maxPrice : Long.MAX_VALUE,
                    skipVisibility);
        } else {
            productPage = productRepository.findAllProductsForUIByCategory(pageable, category, skipVisibility);
        }
        Iterable<Product> productIterable = productPage.getContent();
        productData.put(LowerCase.DATA, getDataInRequiredFormat(productIterable).get(LowerCase.DATA));
        productData.put(LowerCase.INFO, getPageData(productPage));
        return productData;
    }

    public JSONObject getAllAvailableProductDetailsForGivenVariation(Category category,
            VARIANT_TYPE variantType,
            Boolean skipVisibility) {
        JSONObject productData = new JSONObject();
        List<Product> productList = null;
        if (variantType == VARIANT_TYPE.COLOR) {
            productList = productRepository.findProductsNotInColorVariantOrUnderLimit(colorVariantAssociationLimit,
                    category,
                    skipVisibility);
        } else if (variantType == VARIANT_TYPE.SIZE) {
            productList = productRepository.findProductsNotInSizeVariantOrUnderLimit(sizeVariantAssociationLimit,
                    category,
                    skipVisibility);
        } else if (variantType == VARIANT_TYPE.WEIGHT) {
            productList = productRepository.findProductsNotInWeightVariantOrUnderLimit(weightVariantAssociationLimit,
                    category,
                    skipVisibility);
        }
        productData.put(LowerCase.DATA, getDataInRequiredFormat(productList).get(LowerCase.DATA));
        return productData;
    }

    public JSONObject getAllProductDetailsForVariation(Category category, Boolean skipVisibility) {
        JSONObject productData = new JSONObject();
        Iterable<Product> productIterable = productRepository.findAllProductsByCategory(category, skipVisibility);
        productData.put(LowerCase.DATA, getDataInRequiredFormat(productIterable).get(LowerCase.DATA));
        return productData;
    }

    public JSONObject getProductVariantDetailsForUI(Long id, Category category) {
        Product product = productRepository.findByIdAndCategoryAndToBeDeletedIsFalse(id, category);
        List<Long> productIds = new ArrayList<>();
        productIds.add(product.getId());

        JSONObject variationInfo = new JSONObject();
        variationInfo.put(WeightVariantConstants.SnakeCase.WEIGHT_VARIANT_INFO, new JSONArray());
        variationInfo.put(SizeVariantConstants.SnakeCase.SIZE_VARIANT_INFO, new JSONArray());
        variationInfo.put(ColorVariantConstants.SnakeCase.COLOR_VARIANT_INFO, new JSONArray());
        if (product.getVariantWeightId() != null) {
            variationInfo.put(WeightVariantConstants.SnakeCase.WEIGHT_VARIANT_INFO,
                    getVariationToProducts(weightVariantCRUD
                            .getWeightVariantDetailsWithExcludeProductList(product.getVariantWeightId(), productIds))
                            .get(ProductConstants.LowerCase.DATA));
        }
        if (product.getVariantSizeId() != null) {
            variationInfo.put(SizeVariantConstants.SnakeCase.SIZE_VARIANT_INFO,
                    getVariationToProducts(sizeVariantCRUD
                            .getSizeVariantDetailsWithExcludeProductList(product.getVariantSizeId(), productIds))
                            .get(ProductConstants.LowerCase.DATA));
        }
        if (product.getVariantColorId() != null) {
            variationInfo.put(ColorVariantConstants.SnakeCase.COLOR_VARIANT_INFO,
                    getVariationToProducts(colorVariantCRUD
                            .getColorVariantProductDetailsWithExcludeProductList(product.getVariantColorId(),
                                    productIds))
                            .get(ProductConstants.LowerCase.DATA));
        }
        return variationInfo;
    }

    private <T> JSONObject getVariationToProducts(List<T> variantsData) {
        List<Product> products = variantsData.stream()
                .map(variant -> {
                    if (variant instanceof SizeVariant) {
                        return ((SizeVariant) variant).getProduct();
                    } else if (variant instanceof WeightVariant) {
                        return ((WeightVariant) variant).getProduct();
                    } else if (variant instanceof ColorVariant) {
                        return ((ColorVariant) variant).getProduct();
                    }
                    return null;
                })
                .filter(product -> product != null)
                .collect(Collectors.toList());

        getDbFilter().setFormat(DATAFORMAT.JSON);
        return getDataInRequiredFormat(products);
    }

    public JSONObject getProductForUI(Long id, Category category, Boolean skipVisibility) {
        JSONObject productData = new JSONObject();
        Product product = productRepository.findProductForUIByCategory(id, category, skipVisibility);
        if (getDbFilter() != null && getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
            productData.put(LowerCase.DATA, product != null ? product.toJSON(Boolean.TRUE, Boolean.TRUE) : product);
        } else {
            productData.put(LowerCase.DATA, product);
        }
        return productData;
    }

    public Boolean checkProductExistsForCategory(Long id, Category category, Boolean skipVisibility) {
        JSONObject product = getProductForUI(id, category, skipVisibility);
        if (product.has(LowerCase.DATA)) {
            if (product.get(LowerCase.DATA) != null) {
                return true;
            }
        }
        return false;
    }

    public Product getProduct(Long id, Category category) {
        Product product = productRepository.findByIdAndCategoryAndToBeDeletedIsFalse(id, category);
        return product;
    }

    public Product getProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        return product;
    }

    public Product createProduct(Category category, String name, String description, String richTextDetails,
            String details, String color,
            String size, WEIGHT_UNITS weight_units, Integer weight, Long stocks, Long price) throws Exception {
        return createProduct(category, name, description, richTextDetails, details, color, size, weight_units, weight,
                stocks, price,
                true, null, null, null);
    }

    private Product createProduct(Category category, String name, String description, String richTextDetails,
            String details, String color,
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
        if (richTextDetails != null) {
            product.setRichtextDetails(objectMapper.writeValueAsString(richTextDetails));
        }
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

    public void createProductVariant(Product currentProduct, Product variantProduct, VARIANT_TYPE variantType)
            throws Exception {
        if (variantType.equals(VARIANT_TYPE.WEIGHT)) {
            if (!(currentProduct.getWeight() != null && currentProduct.getWeight() > 0
                    && currentProduct.getWeightUnits() != null
                    && currentProduct.getWeightUnits() != WEIGHT_UNITS.NONE)) {
                throw new Exception(WeightVariantConstants.ExceptionMessageCase.NO_WEIGHT_DATA_PRESENT_IN_THE_PRODUCT);
            }
            if (currentProduct.getVariantWeightId() != null && variantProduct.getVariantWeightId() != null) {
                if (currentProduct.getVariantWeightId() == variantProduct.getVariantWeightId()) {
                    throw new Exception(WeightVariantConstants.ExceptionMessageCase.ALREADY_WEIGHT_VARIANT_ADDED);
                } else {
                    throw new Exception(
                            WeightVariantConstants.ExceptionMessageCase.WEIGHT_VARIANT_ADDED_TO_OTHER_PRODUCT);
                }
            } else {
                Long variantId = 0l;
                if (currentProduct.getVariantWeightId() != null) {
                    if (weightVariantCRUD
                            .getWeightVariantDetails(currentProduct.getVariantWeightId())
                            .size() >= weightVariantAssociationLimit) {
                        throw new Exception(
                                WeightVariantConstants.ExceptionMessageCase.MAX_WEIGHT_VARIANT_ASSOCIATED);
                    }
                    variantId = weightVariantCRUD
                            .createWeightVariant(variantProduct, currentProduct.getVariantWeightId()).getVariantId();
                    variantProduct.setVariantWeightId(variantId);
                    productRepository.save(variantProduct);
                } else if (variantProduct.getVariantWeightId() != null) {
                    if (weightVariantCRUD
                            .getWeightVariantDetails(variantProduct.getVariantWeightId())
                            .size() >= weightVariantAssociationLimit) {
                        throw new Exception(
                                WeightVariantConstants.ExceptionMessageCase.MAX_WEIGHT_VARIANT_ASSOCIATED);
                    }
                    variantId = weightVariantCRUD
                            .createWeightVariant(currentProduct, variantProduct.getVariantWeightId()).getVariantId();
                    currentProduct.setVariantWeightId(variantId);
                    productRepository.save(currentProduct);
                } else {
                    variantId = weightVariantCRUD.createWeightVariantWithoutVariantId(currentProduct).getVariantId();
                    currentProduct.setVariantWeightId(variantId);
                    productRepository.save(currentProduct);
                    variantId = weightVariantCRUD
                            .createWeightVariant(variantProduct, currentProduct.getVariantWeightId()).getVariantId();
                    variantProduct.setVariantWeightId(variantId);
                    productRepository.save(variantProduct);
                }
            }
        } else if (variantType.equals(VARIANT_TYPE.SIZE)) {
            if (!(currentProduct.getSize() != null && currentProduct.getSize().trim().length() > 0)) {
                throw new Exception(SizeVariantConstants.ExceptionMessageCase.NO_SIZE_DATA_PRESENT_IN_THE_PRODUCT);
            }
            if (currentProduct.getVariantSizeId() != null && variantProduct.getVariantSizeId() != null) {
                if (currentProduct.getVariantSizeId() == variantProduct.getVariantSizeId()) {
                    throw new Exception(SizeVariantConstants.ExceptionMessageCase.ALREADY_SIZE_VARIANT_ADDED);
                } else {
                    throw new Exception(
                            SizeVariantConstants.ExceptionMessageCase.SIZE_VARIANT_ADDED_TO_OTHER_PRODUCT);
                }
            } else {
                Long variantId = 0l;
                if (currentProduct.getVariantSizeId() != null) {
                    if (sizeVariantCRUD
                            .getSizeVariantDetails(currentProduct.getVariantSizeId())
                            .size() >= sizeVariantAssociationLimit) {
                        throw new Exception(
                                SizeVariantConstants.ExceptionMessageCase.MAX_SIZE_VARIANT_ASSOCIATED);
                    }
                    variantId = sizeVariantCRUD
                            .createSizeVariant(variantProduct, currentProduct.getVariantSizeId()).getVariantId();
                    variantProduct.setVariantSizeId(variantId);
                    productRepository.save(variantProduct);
                } else if (variantProduct.getVariantSizeId() != null) {
                    if (sizeVariantCRUD
                            .getSizeVariantDetails(variantProduct.getVariantSizeId())
                            .size() >= sizeVariantAssociationLimit) {
                        throw new Exception(
                                SizeVariantConstants.ExceptionMessageCase.MAX_SIZE_VARIANT_ASSOCIATED);
                    }
                    variantId = sizeVariantCRUD
                            .createSizeVariant(currentProduct, variantProduct.getVariantSizeId()).getVariantId();
                    currentProduct.setVariantSizeId(variantId);
                    productRepository.save(currentProduct);
                } else {
                    variantId = sizeVariantCRUD.createSizeVariantWithoutVariantId(currentProduct).getVariantId();
                    currentProduct.setVariantSizeId(variantId);
                    productRepository.save(currentProduct);
                    variantId = sizeVariantCRUD
                            .createSizeVariant(variantProduct, currentProduct.getVariantSizeId()).getVariantId();
                    variantProduct.setVariantSizeId(variantId);
                    productRepository.save(variantProduct);
                }
            }
        } else if (variantType.equals(VARIANT_TYPE.COLOR)) {
            if (!(currentProduct.getColor() != null && currentProduct.getColor().trim().length() > 0)) {
                throw new Exception(ColorVariantConstants.ExceptionMessageCase.NO_COLOR_DATA_PRESENT_IN_THE_PRODUCT);
            }
            if (currentProduct.getVariantColorId() != null && variantProduct.getVariantColorId() != null) {
                if (currentProduct.getVariantColorId() == variantProduct.getVariantColorId()) {
                    throw new Exception(ColorVariantConstants.ExceptionMessageCase.ALREADY_COLOR_VARIANT_ADDED);
                } else {
                    throw new Exception(
                            ColorVariantConstants.ExceptionMessageCase.COLOR_VARIANT_ADDED_TO_OTHER_PRODUCT);
                }
            } else {
                Long variantId = 0l;
                if (currentProduct.getVariantColorId() != null) {
                    if (colorVariantCRUD
                            .getColorVariantProductDetails(currentProduct.getVariantColorId())
                            .size() >= colorVariantAssociationLimit) {
                        throw new Exception(
                                ColorVariantConstants.ExceptionMessageCase.MAX_COLOR_VARIANT_ASSOCIATED);
                    }
                    variantId = colorVariantCRUD
                            .createColorVariant(variantProduct, currentProduct.getVariantColorId()).getVariantId();
                    variantProduct.setVariantColorId(variantId);
                    productRepository.save(variantProduct);
                } else if (variantProduct.getVariantColorId() != null) {
                    if (colorVariantCRUD
                            .getColorVariantProductDetails(variantProduct.getVariantColorId())
                            .size() >= colorVariantAssociationLimit) {
                        throw new Exception(
                                ColorVariantConstants.ExceptionMessageCase.MAX_COLOR_VARIANT_ASSOCIATED);
                    }
                    variantId = colorVariantCRUD
                            .createColorVariant(currentProduct, variantProduct.getVariantColorId()).getVariantId();
                    currentProduct.setVariantColorId(variantId);
                    productRepository.save(currentProduct);
                } else {
                    variantId = colorVariantCRUD.createSizeVariantWithoutVariantId(currentProduct).getVariantId();
                    currentProduct.setVariantColorId(variantId);
                    productRepository.save(currentProduct);
                    variantId = colorVariantCRUD
                            .createColorVariant(variantProduct, currentProduct.getVariantColorId()).getVariantId();
                    variantProduct.setVariantColorId(variantId);
                    productRepository.save(variantProduct);
                }
            }
        }
    }

    public Product updateProduct(Long id, String name, String description, String richTextDetails, String details,
            Boolean isColorUpdated,
            String color, Boolean isSizeUpdated,
            String size, Boolean isWeightUpdated, WEIGHT_UNITS weight_units, Integer weight, Long stocks, Long price,
            Boolean isVisible, Long variant_size_id,
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
        if (richTextDetails != null) {
            product.setRichtextDetails(objectMapper.writeValueAsString(richTextDetails));
        }
        if (details != null) {
            product.setDetails(details);
        }
        if (isColorUpdated) {
            product.setColor(color);
        }
        if (isSizeUpdated) {
            product.setSize(size);
        }
        if (isWeightUpdated) {
            product.setWeightUnits(null);
            product.setWeight(null);
            product.setWeightUnits(null);
            product.setWeight(null);
            if (!(weight_units == null || weight == -1)) {
                if (weight != null) {
                    product.setWeightUnits(weight_units);
                    product.setWeight(weight);
                }
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

    @Transactional
    public void deleteProductVariant(Product currentProduct, VARIANT_TYPE variantType)
            throws Exception {
        if (variantType.equals(VARIANT_TYPE.WEIGHT)) {
            if (currentProduct.getVariantWeightId() == null) {
                throw new Exception(ProductConstants.ExceptionMessageCase.VARIANT_NOT_PRESENT);
            } else {
                Long weightVariantId = currentProduct.getVariantWeightId();
                weightVariantCRUD.deleteWeightVariant(currentProduct, weightVariantId);
                currentProduct.setVariantWeightId(null);
                productRepository.save(currentProduct);

                List<WeightVariant> weightVariantDtls = weightVariantCRUD.getWeightVariantDetails(weightVariantId);
                if (weightVariantDtls.size() == 1) {
                    weightVariantCRUD.deleteWeightVariant(weightVariantDtls.get(0).getProduct(), weightVariantId);
                    weightVariantDtls.get(0).getProduct().setVariantWeightId(null);
                    productRepository.save(currentProduct);
                }
            }
        } else if (variantType.equals(VARIANT_TYPE.SIZE)) {
            if (currentProduct.getVariantSizeId() == null) {
                throw new Exception(ProductConstants.ExceptionMessageCase.VARIANT_NOT_PRESENT);
            } else {
                Long sizeVariantId = currentProduct.getVariantSizeId();
                sizeVariantCRUD.deleteSizeVariant(currentProduct, currentProduct.getVariantSizeId());
                currentProduct.setVariantSizeId(null);
                productRepository.save(currentProduct);

                List<SizeVariant> sizeVariantDtls = sizeVariantCRUD.getSizeVariantDetails(sizeVariantId);
                if (sizeVariantDtls.size() == 1) {
                    sizeVariantCRUD.deleteSizeVariant(sizeVariantDtls.get(0).getProduct(), sizeVariantId);
                    sizeVariantDtls.get(0).getProduct().setVariantSizeId(null);
                    productRepository.save(currentProduct);
                }
            }
        } else if (variantType.equals(VARIANT_TYPE.COLOR)) {
            if (currentProduct.getVariantColorId() == null) {
                throw new Exception(ProductConstants.ExceptionMessageCase.VARIANT_NOT_PRESENT);
            } else {
                Long colorVariantId = currentProduct.getVariantColorId();
                colorVariantCRUD.deleteColorVariant(currentProduct, currentProduct.getVariantColorId());
                currentProduct.setVariantColorId(null);
                productRepository.save(currentProduct);

                List<ColorVariant> colorVariantDtls = colorVariantCRUD.getColorVariantProductDetails(colorVariantId);
                if (colorVariantDtls.size() == 1) {
                    colorVariantCRUD.deleteColorVariant(colorVariantDtls.get(0).getProduct(), colorVariantId);
                    colorVariantDtls.get(0).getProduct().setVariantColorId(null);
                    productRepository.save(currentProduct);
                }
            }
        }
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
                WEIGHT_UNITS weightUnit = WEIGHT_UNITS.getEnumValueFromGivenString(
                        JSONUtils.optString(body, ProductConstants.CamelCase.WEIGHTUNITS, null));

                if (weightUnit == null) {
                    code = null;
                    message = MessageCase.INVALID_WEIGHT_UNIT;
                    missingField = CamelCase.WEIGHTUNITS;
                    isError = true;
                }
                if (!isError) {
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

    public JSONObject checkBodyOfDeleteImages(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = LowerCase.EMPTY_QUOTES;
        String message = MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(SnakeCase.IMAGE_IDS)) {
            missingField = SnakeCase.IMAGE_IDS;
            message = message.replace(LoggerCase.ARG0, SnakeCase.IMAGE_IDS);
        } else {
            Boolean isError = false;
            if (body.get(SnakeCase.IMAGE_IDS) instanceof List) {
                List<?> imageIds = (List<?>) body.get(SnakeCase.IMAGE_IDS);
                int index = 1;
                for (Object item : imageIds) {
                    if (!(item instanceof Long)) {
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
            WEIGHT_UNITS weightUnit = WEIGHT_UNITS.getEnumValueFromGivenString(
                    JSONUtils.optString(body, ProductConstants.CamelCase.WEIGHTUNITS, null));

            if (weightUnit == null) {
                code = null;
                message = MessageCase.INVALID_WEIGHT_UNIT;
                missingField = CamelCase.WEIGHTUNITS;
                isError = true;
            }
            if (!isError) {
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

    public JSONObject checkBodyOfCreateVariation(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = LowerCase.EMPTY_QUOTES;
        String message = MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(AnimalConstants.SnakeCase.ANIMAL_ID)) {
            missingField = AnimalConstants.SnakeCase.ANIMAL_ID;
            message = message.replace(LoggerCase.ARG0, AnimalConstants.SnakeCase.ANIMAL_ID);
        } else if (!body.has(CategoryConstants.SnakeCase.CATEGORY_ID)) {
            missingField = CategoryConstants.SnakeCase.CATEGORY_ID;
            message = message.replace(LoggerCase.ARG0, CategoryConstants.SnakeCase.CATEGORY_ID);
        } else if (!body.has(SnakeCase.PRODUCT_ID)) {
            missingField = SnakeCase.PRODUCT_ID;
            message = message.replace(LoggerCase.ARG0, SnakeCase.PRODUCT_ID);
        } else if (!body.has(SnakeCase.VARIANT_TYPE)) {
            missingField = SnakeCase.VARIANT_TYPE;
            message = message.replace(LoggerCase.ARG0, SnakeCase.VARIANT_TYPE);
        } else {
            Boolean isError = false;

            if (VARIANT_TYPE.getEnumValueFromGivenString(body.get(SnakeCase.VARIANT_TYPE).toString()) == null) {
                code = null;
                message = ExceptionMessageCase.INVALID_VARIANT_TYPE;
                missingField = SnakeCase.VARIANT_TYPE;
                isError = true;
            }

            if (!isError) {
                if (body.get(AnimalConstants.SnakeCase.ANIMAL_ID) instanceof Long
                        || body.get(AnimalConstants.SnakeCase.ANIMAL_ID) instanceof Integer) {
                    Long animalId = Long.parseLong(body.get(AnimalConstants.SnakeCase.ANIMAL_ID).toString());
                    if (animalCRUD.getAnimal(animalId) == null) {
                        code = null;
                        message = AnimalConstants.ExceptionMessageCase.INVALID_ANIMAL_ID;
                        missingField = AnimalConstants.SnakeCase.ANIMAL_ID;
                        isError = true;
                    }
                } else {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                    missingField = AnimalConstants.SnakeCase.ANIMAL_ID;
                    isError = true;
                }
            }

            if (!isError) {
                if (body.get(CategoryConstants.SnakeCase.CATEGORY_ID) instanceof Long
                        || body.get(CategoryConstants.SnakeCase.CATEGORY_ID) instanceof Integer) {
                    Long categoryId = Long.parseLong(body.get(CategoryConstants.SnakeCase.CATEGORY_ID).toString());
                    if (categoryCRUD.getCategoryDetail(
                            animalCRUD.getAnimal(
                                    Long.parseLong(body.get(AnimalConstants.SnakeCase.ANIMAL_ID).toString())),
                            categoryId) == null) {
                        code = null;
                        message = CategoryConstants.ExceptionMessageCase.INVALID_CATEGORY_ID;
                        missingField = CategoryConstants.SnakeCase.CATEGORY_ID;
                        isError = true;
                    }
                } else {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                    missingField = CategoryConstants.SnakeCase.CATEGORY_ID;
                    isError = true;
                }
            }

            if (!isError) {
                if (body.get(SnakeCase.PRODUCT_ID) instanceof Long
                        || body.get(SnakeCase.PRODUCT_ID) instanceof Integer) {
                    Long productId = Long.parseLong(body.get(SnakeCase.PRODUCT_ID).toString());
                    Long categoryId = Long.parseLong(body.get(CategoryConstants.SnakeCase.CATEGORY_ID).toString());
                    Product product = getProduct(productId, categoryCRUD.getCategoryDetail(
                            animalCRUD.getAnimal(
                                    Long.parseLong(body.get(AnimalConstants.SnakeCase.ANIMAL_ID).toString())),
                            categoryId));
                    if (product == null) {
                        code = null;
                        message = ExceptionMessageCase.INVALID_PRODUCT_ID;
                        missingField = SnakeCase.PRODUCT_ID;
                        isError = true;
                    } else {
                        response.put(LowerCase.PRODUCT, product);
                    }
                } else {
                    code = null;
                    message = MessageCase.THE_VALUE_SHOULD_BE_AN_INTEGER;
                    missingField = SnakeCase.PRODUCT_ID;
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

    public JSONObject checkBodyOfDeleteVariation(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = LowerCase.EMPTY_QUOTES;
        String message = MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(SnakeCase.VARIANT_TYPE)) {
            missingField = SnakeCase.VARIANT_TYPE;
            message = message.replace(LoggerCase.ARG0, SnakeCase.VARIANT_TYPE);
        } else {
            Boolean isError = false;

            if (VARIANT_TYPE.getEnumValueFromGivenString(body.get(SnakeCase.VARIANT_TYPE).toString()) == null) {
                code = null;
                message = ExceptionMessageCase.INVALID_VARIANT_TYPE;
                missingField = SnakeCase.VARIANT_TYPE;
                isError = true;
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

    public void checkImageIds(Product product, List<Long> imageIds) throws Exception {
        List<ProductImage> existingImages = product.getImages();
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }
        List<Long> existingImageIds = new ArrayList<>();
        for (ProductImage data : existingImages) {
            existingImageIds.add(data.getId());
        }
        for (Long imageId : imageIds) {
            if (!existingImageIds.contains(imageId)) {
                throw new Exception(ExceptionMessageCase.IMAGE_ID_IN_GIVEN_LIST_NOT_FOUND_FOR_THE_PRODUCT);
            }
        }
    }

    public void deleteImageFromProduct(Product product, Long imageId) throws Exception {
        String PRODUCT_IMAGE_FOLDER = PRODUCTS_IMAGE_LOCATION_BEFORE_STATIC;
        List<ProductImage> existingImages = product.getImages();
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
        if (currentImage.getImageType().equals("file")) {
            Path path = Paths.get(
                    PRODUCT_IMAGE_FOLDER + SpecialCharacter.SLASH + currentImage.getImageUrl());
            deleteImageFromServer(path);
        }
        existingImages.remove(currentImage);
        product.setImages(existingImages.isEmpty() ? null : existingImages);
        if (product.getThumbnailImageUrl() != null) {
            if (currentImage.getId().equals(product.getThumbnailImageUrl().getId())) {
                product.setThumbnailImageUrl(null);
            }
        }
        productRepository.save(product);
    }

    public void addImageUrlToProduct(Product product, List<String> imageUrl) throws Exception {
        List<ProductImage> existingImages = product.getImages();
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }
        if (existingImages.size() >= productImagesSize) {
            throw new Exception(ExceptionMessageCase.MAXIMUM_IMAGES_FOR_PRODUCT_REACHED);
        } else {
            if (imageUrl.size() + existingImages.size() >= productImagesSize) {
                throw new Exception(ExceptionMessageCase.MAXIMUM_IMAGES_FOR_PRODUCT_WILL_BE_REACHED_FOR_IMAGEURL);
            }
        }
        Long nextImageId = 1l;
        for (ProductImage data : existingImages) {
            if (data.getId() >= nextImageId) {
                nextImageId = data.getId() + 1;
            }
        }
        for (String url : imageUrl) {
            ProductImage newImage = new ProductImage();
            newImage.setId(nextImageId);
            newImage.setImageUrl(url);
            newImage.setImageType("external_url");
            existingImages.add(newImage);
            nextImageId++;
        }
        product.setImages(existingImages);
        productRepository.save(product);
    }

    public void addImageToProduct(Product product, InputStream image, String extension) throws Exception {
        String PRODUCT_IMAGE_FOLDER = PRODUCTS_IMAGE_LOCATION_FULL;
        List<ProductImage> existingImages = product.getImages();
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
        newImage.setImageType("file");
        existingImages.add(newImage);
        product.setImages(existingImages);
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
        List<ProductImage> existingImages = product.getImages();
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
