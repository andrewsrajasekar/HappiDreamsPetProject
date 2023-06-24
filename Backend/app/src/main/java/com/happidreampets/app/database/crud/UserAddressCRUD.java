package com.happidreampets.app.database.crud;

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

import com.happidreampets.app.constants.CartConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.TopCategoriesConstants;
import com.happidreampets.app.constants.UserAddressConstants.MessageCase;
import com.happidreampets.app.constants.UserAddressConstants.LowerCase;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.constants.UserAddressConstants.ExceptionMessageCase;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.UserAddress;
import com.happidreampets.app.database.model.UserAddress.USERADDRESS;
import com.happidreampets.app.database.repository.UserAddressRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;
import com.happidreampets.app.utils.Utils;

@Component
public class UserAddressCRUD {
    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserCRUD userCRUD;

    private DbFilter dbFilter;

    @Value("${user.address.size}")
    private Integer userAddressSize;

    private Boolean fromController = Boolean.FALSE;

    public Boolean getFromController() {
        return fromController;
    }

    public void setFromController(Boolean fromController) {
        this.fromController = fromController;
    }

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private USERADDRESS checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(USERADDRESS.class, dbFilter.getSortColumn().toString())) {
                USERADDRESS enumValue = USERADDRESS.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<UserAddress> data) {
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
                List<UserAddress> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<UserAddress> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<UserAddress> userPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, userPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, userPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, userPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, userPage.hasNext());
        return pageData;
    }

    public JSONObject getUserAddressesForUI(User user) {
        JSONObject userAddressData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<UserAddress> userAddressPage = userAddressRepository.findByUserIdAndToBeDeletedIsFalse(user.getId(),
                pageable);
        Iterable<UserAddress> userIterable = userAddressPage.getContent();
        userAddressData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(userIterable).get(ProductConstants.LowerCase.DATA));
        userAddressData.put(ProductConstants.LowerCase.INFO, getPageData(userAddressPage));
        return userAddressData;
    }

    public List<UserAddress> getUserAddresses(User user) {
        return userAddressRepository.findByUserIdAndToBeDeletedIsFalse(user.getId());
    }

    public UserAddress getUserAddress(User user, Long addressId) {
        return userAddressRepository.findByIdAndUserIdAndToBeDeletedIsFalse(addressId, user.getId());
    }

    public Boolean isValidAddressId(User user, Long addressId) {
        UserAddress userAddress = userAddressRepository.findByIdAndUserIdAndToBeDeletedIsFalse(addressId, user.getId());
        return userAddress != null;
    }

    public UserAddress checkAddressIdAndThrowException(User user, Long addressId) throws Exception {
        UserAddress userAddress = userAddressRepository.findByIdAndUserIdAndToBeDeletedIsFalse(addressId, user.getId());
        if (userAddress == null) {
            throw new Exception(ExceptionMessageCase.INVALID_USER_ADDRESS_ID);
        }
        return userAddress;
    }

    public UserAddress createUserAddress(User user, String address, String city, String state, String country,
            String pincode) throws Exception {
        List<UserAddress> existingUserAddresses = getUserAddresses(user);
        if (existingUserAddresses.size() >= userAddressSize) {
            throw new Exception(getFromController()
                    ? MessageCase.ADDRESS_COUNT_FOR_USER_IS_MAXIMUM
                    : TopCategoriesConstants.CapitalizationCase.ALREADY
                            + CartConstants.LowerCase.GAP + userAddressSize
                            + CartConstants.LowerCase.GAP
                            + MessageCase.ADDRESS_COUNT_FOR_USER_IS_MAXIMUM);
        }
        if (address == null || address.trim().length() == 0) {
            throw new Exception(MessageCase.INVALID_ADDRESS);
        }
        if (city == null || city.trim().length() == 0) {
            throw new Exception(MessageCase.INVALID_CITY);
        }
        if (state == null || state.trim().length() == 0) {
            throw new Exception(MessageCase.INVALID_STATE);
        }
        if (country == null || country.trim().length() == 0) {
            throw new Exception(MessageCase.INVALID_COUNTRY);
        }
        if (pincode == null || pincode.trim().length() == 0) {
            throw new Exception(MessageCase.INVALID_PINCODE);
        }
        UserAddress userAddress = new UserAddress();
        userAddress.setAddress(address);
        userAddress.setCity(city);
        userAddress.setCountry(country);
        userAddress.setPincode(pincode);
        userAddress.setState(state);
        userAddress.setUser(user);

        return userAddressRepository.save(userAddress);
    }

    public UserAddress updateUserAddress(User user, Long addressId, String address, String city, String state,
            String country,
            String pincode) throws Exception {
        UserAddress userAddress = getUserAddress(user, addressId);
        if (null == userAddress) {
            throw new Exception(ExceptionMessageCase.INVALID_USER_ADDRESS_ID);
        }
        if (address != null) {
            userAddress.setAddress(address);
        }
        if (city != null) {
            userAddress.setCity(city);
        }
        if (state != null) {
            userAddress.setState(state);
        }
        if (country != null) {
            userAddress.setCountry(country);
        }
        if (pincode != null) {
            userAddress.setPincode(pincode);
        }

        return userAddressRepository.save(userAddress);
    }

    public Boolean deleteUserAddress(User user, Long addressId) throws Exception {
        UserAddress userAddress = getUserAddress(user, addressId);
        if (null == userAddress) {
            throw new Exception(ExceptionMessageCase.INVALID_USER_ADDRESS_ID);
        }
        UserAddress defaultAddress = user.getDefaultAddress();
        userAddress.setToBeDeleted(Boolean.TRUE);
        userAddress.setToBeDeletedStatusChangeTime(System.currentTimeMillis());
        userAddressRepository.save(userAddress);
        if (defaultAddress.getId() == userAddress.getId()) {
            userCRUD.updateDefaultAddressToNull(user.getId());
        }

        return true;
    }

    public JSONObject checkBodyOfAddUserAddress(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(LowerCase.ADDRESS)) {
            missingField = LowerCase.ADDRESS;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.ADDRESS);
        } else if (!body.has(LowerCase.CITY)) {
            missingField = LowerCase.CITY;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.CITY);
        } else if (!body.has(LowerCase.STATE)) {
            missingField = LowerCase.STATE;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.STATE);
        } else if (!body.has(LowerCase.COUNTRY)) {
            missingField = LowerCase.COUNTRY;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.COUNTRY);
        } else if (!body.has(LowerCase.PINCODE)) {
            missingField = LowerCase.PINCODE;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.PINCODE);
        } else {
            if (!Utils.isStringLong(body.get(LowerCase.PINCODE).toString())) {
                code = null;
                message = MessageCase.INVALID_PINCODE;
                missingField = LowerCase.PINCODE;
            } else {
                isSuccess = Boolean.TRUE;
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

    public JSONObject checkBodyOfUpdateUserAddress(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (body.has(LowerCase.PINCODE) && !Utils.isStringLong(body.get(LowerCase.PINCODE).toString())) {
            code = null;
            message = MessageCase.INVALID_PINCODE;
            missingField = LowerCase.PINCODE;
        } else {
            isSuccess = Boolean.TRUE;
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
