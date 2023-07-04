package com.happidreampets.app.database.crud;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.ProductConstants.LoggerCase;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.constants.CartConstants.CapitalizationCase;
import com.happidreampets.app.constants.CartConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.CartConstants.LowerCase;
import com.happidreampets.app.constants.CartConstants.MessageCase;
import com.happidreampets.app.database.model.Cart;
import com.happidreampets.app.database.model.Cart.CARTCOLUMN;
import com.happidreampets.app.database.model.Product;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.repository.CartRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;
import com.happidreampets.app.utils.Utils;

@Component
public class CartCRUD {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductCRUD productCRUD;

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private CARTCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(CARTCOLUMN.class, dbFilter.getSortColumn().toString())) {
                CARTCOLUMN enumValue = CARTCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<Cart> data) {
        JSONObject responseData = new JSONObject();
        responseData.put(ProductConstants.LowerCase.DATA, JSONObject.NULL);
        if (getDbFilter() != null) {
            if (getDbFilter().getFormat().equals(DATAFORMAT.JSON)) {
                JSONArray responseArray = new JSONArray();
                data.forEach(row -> {
                    responseArray.put(row.toJSON());
                });
                responseData.put(ProductConstants.LowerCase.DATA, responseArray);
            } else if (getDbFilter().getFormat().equals(DATAFORMAT.POJO)) {
                List<Cart> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<Cart> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<Cart> cartPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, cartPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, cartPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, cartPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, cartPage.hasNext());
        return pageData;
    }

    public JSONObject getCartDetails(User user) {
        JSONObject cartData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<Cart> cartPage = cartRepository.findAllByUser(user, pageable);
        Iterable<Cart> cartIterable = cartPage.getContent();
        cartData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(cartIterable).get(ProductConstants.LowerCase.DATA));
        cartData.put(ProductConstants.LowerCase.INFO, getPageData(cartPage));
        return cartData;
    }

    public JSONObject getAllCartDetails(User user) {
        JSONObject cartData = new JSONObject();
        Iterable<Cart> cartIterable = cartRepository.findAllByUser(user);

        cartData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(cartIterable).get(ProductConstants.LowerCase.DATA));
        return cartData;
    }

    public Cart getCartDetails(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    public Cart createCart(User user, Product product, Long quantity) throws Exception {
        List<Cart> existingCart = cartRepository.findByUser(user);
        if (!existingCart.isEmpty()) {
            throw new Exception(ExceptionMessageCase.CART_ALREADY_EXISTS_FOR_THIS_USER);
        }
        Cart cart = new Cart();
        if (user == null || product == null || quantity == null) {
            throw new Exception(
                    (user == null ? UserConstants.CapitalizationCase.USER
                            : (product == null ? ProductConstants.CapitalizationCase.PRODUCT
                                    : CapitalizationCase.QUANTITY))
                            + LowerCase.GAP + MessageCase.SHOULD_BE_PRESENT);
        }
        cart.setProduct(product);
        cart.setUser(user);
        cart.setQuantity(quantity);
        cart.setAddedTime(System.currentTimeMillis());
        return cartRepository.save(cart);
    }

    public Cart updateCartProductQuantity(User user, Product product, Long quantity) throws Exception {
        Cart cart = cartRepository.findByUserAndProductId(user, product.getId());
        if (cart == null) {
            throw new Exception(ProductConstants.ExceptionMessageCase.PRODUCT_NOT_FOUND);
        }
        if (quantity == null) {
            throw new Exception(CapitalizationCase.QUANTITY + LowerCase.GAP + MessageCase.SHOULD_BE_PRESENT);
        }
        cart.setQuantity(quantity);
        cart.setAddedTime(System.currentTimeMillis());
        return cartRepository.save(cart);
    }

    public Boolean deleteCartById(Long id) throws Exception {
        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) {
            throw new Exception(ExceptionMessageCase.CART_NOT_FOUND);
        }
        cartRepository.delete(cart);
        return true;
    }

    public Boolean removeUserProductsInCart(User user, Product product) throws Exception {
        List<Cart> carts = cartRepository.findByUser(user);
        if (carts.isEmpty()) {
            throw new Exception(ExceptionMessageCase.CART_NOT_FOUND_FOR_USER);
        }
        for (Cart cart : carts) {
            if (cart.getProduct().getId() == product.getId()) {
                cartRepository.delete(cart);
            }
        }
        return true;
    }

    public Boolean deleteUserCart(User user) throws Exception {
        List<Cart> carts = cartRepository.findByUser(user);
        if (carts.isEmpty()) {
            throw new Exception(ExceptionMessageCase.CART_NOT_FOUND_FOR_USER);
        }
        for (Cart cart : carts) {
            cartRepository.delete(cart);
        }
        return true;
    }

    public JSONObject checkBodyOfDeleteProductsInCart(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(ProductConstants.SnakeCase.PRODUCT_ID)) {
            missingField = ProductConstants.SnakeCase.PRODUCT_ID;
            message = message.replace(LoggerCase.ARG0, ProductConstants.SnakeCase.PRODUCT_ID);
        } else {
            if (!Utils.isStringLong(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())) {
                code = null;
                message = ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID;
                missingField = ProductConstants.SnakeCase.PRODUCT_ID;
            } else {
                Long productId = Long.valueOf(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString());
                Product product = productCRUD.getProduct(productId);
                if (product == null) {
                    code = null;
                    message = ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID;
                    missingField = ProductConstants.SnakeCase.PRODUCT_ID;
                } else {
                    isSuccess = Boolean.TRUE;
                }

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

    public JSONObject checkBodyOfAddProductsInCart(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(ProductConstants.SnakeCase.PRODUCT_ID)) {
            missingField = ProductConstants.SnakeCase.PRODUCT_ID;
            message = message.replace(LoggerCase.ARG0, ProductConstants.SnakeCase.PRODUCT_ID);
        } else if (!body.has(LowerCase.QUANTITY)) {
            missingField = LowerCase.QUANTITY;
            message = message.replace(LoggerCase.ARG0, LowerCase.QUANTITY);
        } else {
            if (!Utils.isStringLong(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())) {
                code = null;
                message = ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID;
                missingField = ProductConstants.SnakeCase.PRODUCT_ID;
            } else {
                Long productId = Long.valueOf(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString());
                Product product = productCRUD.getProduct(productId);
                if (product == null) {
                    code = null;
                    message = ProductConstants.ExceptionMessageCase.INVALID_PRODUCT_ID;
                    missingField = ProductConstants.SnakeCase.PRODUCT_ID;
                } else {
                    Long quantity = Long.valueOf(body.get(LowerCase.QUANTITY).toString());
                    if (product.getStocks() < quantity) {
                        code = null;
                        message = ExceptionMessageCase.QUANTITY_VALUE_IS_TOO_HIGH;
                        missingField = LowerCase.QUANTITY;
                    } else if (quantity <= 0) {
                        code = null;
                        message = ExceptionMessageCase.QUANTITY_VALUE_IS_TOO_LOW;
                        missingField = LowerCase.QUANTITY;
                    } else {
                        isSuccess = Boolean.TRUE;
                    }

                }

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
}
