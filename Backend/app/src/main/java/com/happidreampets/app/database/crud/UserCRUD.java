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

import com.happidreampets.app.constants.CartConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.UserConstants.CapitalizationCase;
import com.happidreampets.app.constants.UserConstants.ExceptionMessageCase;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.User.USERCOLUMN;
import com.happidreampets.app.database.repository.UserRepository;
import com.happidreampets.app.database.utils.DbFilter;
import com.happidreampets.app.database.utils.DbFilter.DATAFORMAT;

@Component
public class UserCRUD {

    @Autowired
    private UserRepository userRepository;

    private DbFilter dbFilter;

    public DbFilter getDbFilter() {
        return dbFilter;
    }

    public void setDbFilter(DbFilter dbFilter) {
        this.dbFilter = dbFilter;
    }

    private USERCOLUMN checkAndGetColumnName() {
        if (dbFilter != null) {
            if (EnumUtils.isValidEnum(USERCOLUMN.class, dbFilter.getSortColumn().toString())) {
                USERCOLUMN enumValue = USERCOLUMN.valueOf(dbFilter.getSortColumn().toString());
                return enumValue;
            }
        }
        return null;
    }

    private JSONObject getDataInRequiredFormat(Iterable<User> data) {
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
                List<User> responseList = new ArrayList<>();
                data.forEach(responseList::add);
                responseData.put(ProductConstants.LowerCase.DATA, responseList);
            }
        } else {
            List<User> responseList = new ArrayList<>();
            data.forEach(responseList::add);
            responseData.put(ProductConstants.LowerCase.DATA, responseList);
        }

        return responseData;
    }

    private JSONObject getPageData(Page<User> userPage) {
        JSONObject pageData = new JSONObject();
        pageData.put(ProductConstants.LowerCase.PAGE, userPage.getNumber() + 1);
        pageData.put(ProductConstants.SnakeCase.PER_PAGE, userPage.getSize());
        pageData.put(ProductConstants.LowerCase.COUNT, userPage.getContent().size());
        pageData.put(ProductConstants.SnakeCase.MORE_RECORDS, userPage.hasNext());
        return pageData;
    }

    public JSONObject getUserDetails() {
        JSONObject userData = new JSONObject();
        Sort sort = null;
        if (getDbFilter() != null && checkAndGetColumnName() != null) {
            sort = Sort.by(getDbFilter().getSortDirection(), checkAndGetColumnName().getColumnName());
        }
        Integer startIndex = getDbFilter() != null ? getDbFilter().getStartIndex() : 0;
        Integer limit = getDbFilter() != null ? getDbFilter().getLimitIndex() : 0;
        Pageable pageable = sort != null ? PageRequest.of(startIndex, limit, sort) : PageRequest.of(startIndex, limit);
        Page<User> userPage = userRepository.findAll(pageable);
        Iterable<User> userIterable = userPage.getContent();
        userData.put(ProductConstants.LowerCase.DATA,
                getDataInRequiredFormat(userIterable).get(ProductConstants.LowerCase.DATA));
        userData.put(ProductConstants.LowerCase.INFO, getPageData(userPage));
        return userData;
    }

    public User createUser(String name, String password, String email, String phone_extension, String phone_number,
            String address, String city, String state, String country, String pincode) throws Exception {
        User user = new User();
        if (name == null || password == null || email == null) {
            throw new Exception(
                    (name == null ? CapitalizationCase.NAME
                            : (password == null ? CapitalizationCase.PASSWORD : CapitalizationCase.EMAIL))
                            + CartConstants.LowerCase.GAP + CartConstants.MessageCase.SHOULD_BE_PRESENT);
        }
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        if (phone_extension != null && phone_number != null) {
            user.setPhone_extension(phone_extension);
            user.setPhone_number(phone_number);
        }
        if (address != null && city != null && state != null && country != null && pincode != null) {
            user.setAddress(address);
            user.setCity(city);
            user.setState(state);
            user.setCountry(country);
            user.setPincode(pincode);
        }
        return userRepository.save(user);
    }

    public User updateUserName(Long id, String name) throws Exception {
        return updateUser(id, name, null, null, null, null, null, null, null, null, null);
    }

    public User updateUserPassword(Long id, String password) throws Exception {
        return updateUser(id, null, password, null, null, null, null, null, null, null, null);
    }

    public User updateUserPhoneNumber(Long id, String phone_extension, String phone_number) throws Exception {
        return updateUser(id, null, null, null, phone_extension, phone_number, null, null, null, null, null);
    }

    public User updateUserAddress(Long id, String address, String city, String state, String country, String pincode)
            throws Exception {
        return updateUser(id, null, null, null, null, null, address, city, state, country, pincode);
    }

    public User updateUser(Long id, String name, String password, String email, String phone_extension,
            String phone_number, String address, String city, String state, String country, String pincode)
            throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if (null == user) {
            throw new Exception(ExceptionMessageCase.USER_NOT_FOUND);
        }
        if (name != null) {
            user.setName(name);
        }
        if (password != null) {
            user.setPassword(password);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (phone_extension != null && phone_number != null) {
            user.setPhone_extension(phone_extension);
            user.setPhone_number(phone_number);
        }
        if (address != null && city != null && state != null && country != null && pincode != null) {
            user.setAddress(address);
            user.setCity(city);
            user.setState(state);
            user.setCountry(country);
            user.setPincode(pincode);
        }
        return userRepository.save(user);
    }

    public Boolean deleteUserById(Long id) throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if (null == user) {
            throw new Exception(ExceptionMessageCase.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
        return true;
    }

    public Boolean deleteUserByName(String name) throws Exception {
        List<User> users = userRepository.findByName(name);
        if (users.isEmpty()) {
            throw new Exception(ExceptionMessageCase.USER_NOT_FOUND);
        }
        for (User user : users) {
            userRepository.delete(user);
        }
        return true;
    }
}
