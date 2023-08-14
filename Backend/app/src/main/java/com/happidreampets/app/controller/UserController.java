package com.happidreampets.app.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.happidreampets.app.constants.CartConstants;
import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.OrderHistoryConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.UserAddressConstants;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.constants.UserConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.UserConstants.LowerCase;
import com.happidreampets.app.constants.UserConstants.SnakeCase;
import com.happidreampets.app.database.crud.CartCRUD;
import com.happidreampets.app.database.crud.OrderHistoryCRUD;
import com.happidreampets.app.database.crud.ProductCRUD;
import com.happidreampets.app.database.crud.TopCategoriesCRUD;
import com.happidreampets.app.database.crud.UserAddressCRUD;
import com.happidreampets.app.database.crud.UserCRUD;
import com.happidreampets.app.database.model.Cart;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.UserAddress;
import com.happidreampets.app.database.model.User.USER_ROLE;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;
import com.happidreampets.app.security.jwt.JwtUtils;
import com.happidreampets.app.utils.AccessLevel;
import com.happidreampets.app.utils.JSONUtils;

@RestController
@RequestMapping("/")
public class UserController extends APIController {

    @Autowired
    JwtUtils jwtUtils;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        FailureResponse failureResponse = new FailureResponse();
        failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
        failureResponse.setData(new JSONObject().put(ControllerConstants.LowerCase.ERROR, "Invalid request body"));
        return failureResponse.throwMandatoryMissing();
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            UserCRUD userCRUD = getUserCRUD();
            JSONObject validationResult = userCRUD.checkBodyOfAuthenticateUser(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ExceptionMessageCase.MISSING_USER_FIELD_FOR_AUTHENTICATION);
            }
            Boolean isUserValid = userCRUD.authenticateUserBasedOnEmailAndPassword(body.get(LowerCase.EMAIL).toString(),
                    body.get(LowerCase.PASSWORD).toString());

            if (!isUserValid) {
                throw new Exception(ExceptionMessageCase.INVALID_CREDENTIALS);
            }
            User user = userCRUD.getUserBasedOnEmailAndPassword(body.get(LowerCase.EMAIL).toString(),
                    body.get(LowerCase.PASSWORD).toString());

            JSONObject jwtData = jwtUtils.getJwtToken(user, true);

            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, user.getId())
                    .put(UserConstants.LowerCase.NAME, user.getName())
                    .put(UserConstants.LowerCase.EMAIL, user.getEmail())
                    .put(UserConstants.LowerCase.PASSWORD, user.getPassword())
                    .put(UserConstants.SnakeCase.ACCESS_TOKEN, jwtData.get(UserConstants.SnakeCase.ACCESS_TOKEN))
                    .put(UserConstants.SnakeCase.EXPIRATION_DATE,
                            jwtData.get(UserConstants.SnakeCase.EXPIRATION_TIME)));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            UserCRUD userCRUD = getUserCRUD();
            JSONObject validationResult = userCRUD.checkBodyOfRegisterUser(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ExceptionMessageCase.MISSING_USER_FIELD_FOR_CREATE_USER);
            }
            User user = userCRUD.createUser(body.get(LowerCase.NAME).toString(),
                    body.get(LowerCase.PASSWORD).toString(), body.get(LowerCase.EMAIL).toString(),
                    body.get(SnakeCase.PHONE_NUMBER).toString(), USER_ROLE.USER);
            successResponse = new SuccessResponse();
            if (user == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.CREATED);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, user.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER, AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            UserCRUD userCRUD = getUserCRUD();

            JSONObject validationResult = userCRUD.checkBodyOfUpdateUser(body, getCurrentUser());
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(ExceptionMessageCase.MISSING_USER_FIELD_FOR_UPDATE_USER);
            }

            User user = userCRUD.updateUser(getCurrentUser().getId(), JSONUtils.optString(body, LowerCase.NAME, null),
                    JSONUtils.optString(body, LowerCase.PASSWORD, null), null, null,
                    JSONUtils.optString(body, SnakeCase.PHONE_NUMBER, null), null);
            successResponse = new SuccessResponse();
            if (user == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(ProductConstants.LowerCase.ID, user.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER, AccessLevel.AccessEnum.ADMIN })
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<?> getuser() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.OK);
            successResponse.setData(getCurrentUser().toJSON());
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/address", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUserAddress() {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {

            UserAddressCRUD userAddressCRUD = getUserAddressCRUD();

            userAddressCRUD.setFromController(Boolean.TRUE);

            List<UserAddress> userAddress = userAddressCRUD.getUserAddresses(getCurrentUser());
            successResponse = new SuccessResponse();
            if (userAddress == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse
                    .setResponseData(new JSONObject().put(ProductConstants.LowerCase.DATA, userAddress));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/address/{addressId}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserAddress(@PathVariable("addressId") Long addressId) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {

            UserAddressCRUD userAddressCRUD = getUserAddressCRUD();

            userAddressCRUD.setFromController(Boolean.TRUE);

            UserAddress userAddress = userAddressCRUD.checkAddressIdAndThrowException(getCurrentUser(), addressId);
            successResponse = new SuccessResponse();
            if (userAddress == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse
                    .setResponseData(new JSONObject().put(ProductConstants.LowerCase.DATA, userAddress));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/address", method = RequestMethod.POST)
    public ResponseEntity<?> addUserAddress(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            UserAddressCRUD userAddressCRUD = getUserAddressCRUD();

            userAddressCRUD.setFromController(Boolean.TRUE);

            JSONObject validationResult = userAddressCRUD.checkBodyOfAddUserAddress(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        UserAddressConstants.ExceptionMessageCase.MISSING_USER_ADDRESS_FIELD_FOR_CREATE_USER_ADDRESS);
            }

            UserAddress userAddress = userAddressCRUD.createUserAddress(getCurrentUser(),
                    body.get(UserAddressConstants.LowerCase.ADDRESS).toString(),
                    body.get(UserAddressConstants.LowerCase.CITY).toString(),
                    body.get(UserAddressConstants.LowerCase.STATE).toString(),
                    body.get(UserAddressConstants.LowerCase.COUNTRY).toString(),
                    body.get(UserAddressConstants.LowerCase.PINCODE).toString());
            successResponse = new SuccessResponse();
            if (userAddress == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.CREATED);
            }
            successResponse
                    .setData(new JSONObject().put(UserAddressConstants.SnakeCase.ADDRESS_ID, userAddress.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/address/{addressId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserAddress(@RequestBody Map<String, Object> bodyData,
            @PathVariable("addressId") Long addressId) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            UserAddressCRUD userAddressCRUD = getUserAddressCRUD();

            userAddressCRUD.setFromController(Boolean.TRUE);

            userAddressCRUD.checkAddressIdAndThrowException(getCurrentUser(), addressId);

            JSONObject validationResult = userAddressCRUD.checkBodyOfUpdateUserAddress(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        UserAddressConstants.ExceptionMessageCase.MISSING_USER_ADDRESS_FIELD_FOR_UPDATE_USER_ADDRESS);
            }

            UserAddress userAddress = userAddressCRUD.updateUserAddress(getCurrentUser(), addressId,
                    JSONUtils.optString(body, UserAddressConstants.LowerCase.ADDRESS, null),
                    JSONUtils.optString(body, UserAddressConstants.LowerCase.CITY, null),
                    JSONUtils.optString(body, UserAddressConstants.LowerCase.STATE, null),
                    JSONUtils.optString(body, UserAddressConstants.LowerCase.COUNTRY, null),
                    JSONUtils.optString(body, UserAddressConstants.LowerCase.PINCODE, null));
            successResponse = new SuccessResponse();
            if (userAddress == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse
                    .setData(new JSONObject().put(UserAddressConstants.SnakeCase.ADDRESS_ID, userAddress.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/address/{addressId}/default", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserAddressAsDefault(@PathVariable("addressId") Long addressId) {
        SuccessResponse successResponse = new SuccessResponse();
        try {

            UserAddressCRUD userAddressCRUD = getUserAddressCRUD();

            UserAddress userAddress = userAddressCRUD.checkAddressIdAndThrowException(getCurrentUser(), addressId);

            userAddressCRUD.setFromController(Boolean.TRUE);

            UserCRUD userCRUD = getUserCRUD();

            User user = userCRUD.updateUserDefaultAddress(getCurrentUser().getId(), userAddress);
            successResponse = new SuccessResponse();
            if (user == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(UserAddressConstants.SnakeCase.ADDRESS_ID, user.getId()));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/address/{addressId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserAddress(@PathVariable("addressId") Long addressId) {
        SuccessResponse successResponse = new SuccessResponse();
        try {

            UserAddressCRUD userAddressCRUD = getUserAddressCRUD();

            userAddressCRUD.setFromController(Boolean.TRUE);

            userAddressCRUD.checkAddressIdAndThrowException(getCurrentUser(), addressId);

            Boolean status = userAddressCRUD.deleteUserAddress(getCurrentUser(), addressId);
            successResponse = new SuccessResponse();
            if (!status) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject().put(UserAddressConstants.SnakeCase.ADDRESS_ID, addressId));
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/cart", method = RequestMethod.GET)
    public ResponseEntity<?> getCartProducts() {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            DbFilter dbFilter = new DbFilter();
            dbFilter.setFormat(DATAFORMAT.JSON);
            dbFilter.setStartIndex(0);
            dbFilter.setLimitIndex(2000);
            CartCRUD cartCRUD = getCartCRUD();
            cartCRUD.setDbFilter(dbFilter);
            JSONObject data = cartCRUD.getAllCartDetails(getCurrentUser());
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
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/cart", method = RequestMethod.POST)
    public ResponseEntity<?> addCartProducts(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            CartCRUD cartCRUD = getCartCRUD();

            ProductCRUD productCRUD = getProductCRUD();

            JSONObject validationResult = cartCRUD.checkBodyOfAddProductsInCart(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        CartConstants.ExceptionMessageCase.MISSING_CART_FIELD_FOR_ADD_CART_PRODUCTS);
            }

            Cart data = cartCRUD.createCart(getCurrentUser(),
                    productCRUD.getProduct(Long.valueOf(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())),
                    Long.valueOf(body.get(CartConstants.LowerCase.QUANTITY).toString()));
            successResponse = new SuccessResponse();
            if (data == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.CREATED);
            }
            successResponse.setData(new JSONObject());
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/cart", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCartProducts(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            CartCRUD cartCRUD = getCartCRUD();

            ProductCRUD productCRUD = getProductCRUD();

            JSONObject validationResult = cartCRUD.checkBodyOfAddProductsInCart(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        CartConstants.ExceptionMessageCase.MISSING_CART_FIELD_FOR_UPDATE_CART_PRODUCTS);
            }

            Cart data = cartCRUD.updateCartProductQuantity(getCurrentUser(),
                    productCRUD.getProduct(Long.valueOf(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())),
                    Long.valueOf(body.get(CartConstants.LowerCase.QUANTITY).toString()));
            successResponse = new SuccessResponse();
            if (data == null) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject());
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/cart", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCartProducts(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            CartCRUD cartCRUD = getCartCRUD();

            ProductCRUD productCRUD = getProductCRUD();

            JSONObject validationResult = cartCRUD.checkBodyOfDeleteProductsInCart(body);
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(
                        CartConstants.ExceptionMessageCase.MISSING_CART_FIELD_FOR_DELETE_CART_PRODUCTS);
            }

            Boolean status = cartCRUD.removeUserProductsInCart(getCurrentUser(),
                    productCRUD.getProduct(Long.valueOf(body.get(ProductConstants.SnakeCase.PRODUCT_ID).toString())));
            successResponse = new SuccessResponse();
            if (!status) {
                throw new Exception();
            } else {
                successResponse.setApiResponseStatus(HttpStatus.OK);
            }
            successResponse.setData(new JSONObject());
            return successResponse.getResponse();
        } catch (Exception ex) {
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/orderhistory", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderHistory(
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
            OrderHistoryCRUD orderHistoryCRUD = getOrderHistoryCRUD();
            orderHistoryCRUD.setDbFilter(dbFilter);
            JSONObject data = orderHistoryCRUD.getOrderHistoryDetails(getCurrentUser());
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
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }

    @AccessLevel({ AccessLevel.AccessEnum.USER })
    @RequestMapping(value = "/user/orderhistory", method = RequestMethod.POST)
    public ResponseEntity<?> addOrderHistory(@RequestBody Map<String, Object> bodyData) {
        SuccessResponse successResponse = new SuccessResponse();
        JSONObject errorData = new JSONObject();
        try {
            JSONObject body = JSONUtils.convertMapToJSONObject(bodyData);

            OrderHistoryCRUD orderHistoryCRUD = getOrderHistoryCRUD();
            orderHistoryCRUD.setFromController(Boolean.TRUE);
            TopCategoriesCRUD topCategoriesCRUD = getTopCategoriesCRUD();

            JSONObject validationResult = orderHistoryCRUD.validateBodyDataForCreate(body, getCurrentUser());
            if (!validationResult.getBoolean(ProductConstants.LowerCase.SUCCESS)) {
                errorData = validationResult.getJSONObject(ProductConstants.LowerCase.DATA);
                throw new Exception(OrderHistoryConstants.ExceptionMessageCase.MISSING_ORDER_HISTORY_FIELD_FOR_CREATE);
            }

            List<Long> productIds = topCategoriesCRUD.checkAndFetchBodyToProductIdList(body);

            orderHistoryCRUD.bulkCreateOrderHistory(getCurrentUser(), productIds, getUserAddressCRUD().getUserAddress(
                    getCurrentUser(), Long.parseLong(body.get(UserAddressConstants.SnakeCase.ADDRESS_ID).toString())));
            successResponse = new SuccessResponse();
            successResponse.setApiResponseStatus(HttpStatus.CREATED);
            return successResponse.getResponse();
        } catch (Exception ex) {
            if (ex.getMessage() != null) {
                if (ex.getMessage().split(ControllerConstants.SpecialCharacter.UNDERSCORE
                        + ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION).length >= 2) {
                    errorData = new JSONObject()
                            .put(ProductConstants.LowerCase.FIELD,
                                    ProductConstants.LowerCase.PRODUCTS)
                            .put(ProductConstants.LowerCase.MESSAGE,
                                    ex.getMessage().split(ControllerConstants.SpecialCharacter.UNDERSCORE
                                            + ControllerConstants.CapitalizationCase.BYPASS_EXCEPTION)[0]);
                    ex = new Exception(OrderHistoryConstants.ExceptionMessageCase.INVALID_PRODUCT_ID_IN_BULK_PRODUCTS);
                }
            }
            UserControllerExceptions userControllerExceptions = new UserControllerExceptions();
            userControllerExceptions.setException(ex);
            if (!errorData.isEmpty()) {
                userControllerExceptions.setData(errorData);
            }
            return userControllerExceptions.returnResponseBasedOnException();
        }
    }
}
