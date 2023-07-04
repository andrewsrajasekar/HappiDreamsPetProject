package com.happidreampets.app.database.crud;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.CartConstants;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.constants.UserAddressConstants;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.constants.UserConstants.MessageCase;
import com.happidreampets.app.constants.UserConstants.CapitalizationCase;
import com.happidreampets.app.constants.UserConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.UserConstants.LowerCase;
import com.happidreampets.app.constants.UserConstants.SnakeCase;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.UserAddress;
import com.happidreampets.app.database.model.User.USER_ROLE;
import com.happidreampets.app.database.repository.UserRepository;

@Component
public class UserCRUD {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public PasswordEncoder getEncoder() {
        return encoder;
    }

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public boolean authenticateUserBasedOnEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return getEncoder().matches(password, user.getPassword());
        }
        return user != null;
    }

    public User getUserBasedOnEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            return getEncoder().matches(password, user.getPassword()) ? user : null;
        }
        return user;
    }

    public User getUserBasedOnEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    public User getUserBasedOnId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user;
    }

    public User createUser(String name, String password, String email, String phone_number, USER_ROLE role)
            throws Exception {
        return createUser(name, password, email, "91", phone_number, null, role);
    }

    public User createUser(String name, String password, String email, String phone_extension, String phone_number,
            UserAddress defaultAddress, USER_ROLE role) throws Exception {
        User user = new User();
        if (name == null || password == null || email == null) {
            throw new Exception(
                    (name == null ? CapitalizationCase.NAME
                            : (password == null ? CapitalizationCase.PASSWORD : CapitalizationCase.EMAIL))
                            + CartConstants.LowerCase.GAP + CartConstants.MessageCase.SHOULD_BE_PRESENT);
        }
        user.setName(name);
        String encodedPassword = getEncoder().encode(password);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        if (phone_extension != null && phone_number != null) {
            user.setPhone_extension(phone_extension);
            user.setPhone_number(phone_number);
        }
        if (defaultAddress != null) {
            user.setDefaultAddress(defaultAddress);
        }
        user.setRole(role);
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // Check if the exception is due to a unique constraint violation
            if (ex.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex
                        .getCause();

                // Check if the exception is specifically due to a unique constraint violation
                if (constraintViolationException.getConstraintName().startsWith("UNIQUE_")) {
                    throw new Exception(UserConstants.ExceptionMessageCase.EMAIL_ALREADY_EXISTS);
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        }
    }

    public User updateUserName(Long id, String name) throws Exception {
        return updateUser(id, name, null, null, null, null, null);
    }

    public User updateUserPassword(Long id, String password) throws Exception {
        return updateUser(id, null, password, null, null, null, null);
    }

    public User updateUserPhoneNumber(Long id, String phone_extension, String phone_number) throws Exception {
        return updateUser(id, null, null, null, phone_extension, phone_number, null);
    }

    public User updateUserDefaultAddress(Long id, UserAddress defaultAddress) throws Exception {
        if (null == defaultAddress) {
            throw new Exception(UserAddressConstants.ExceptionMessageCase.INVALID_USER_ADDRESS_ID);
        }
        if (defaultAddress.getId() != id) {
            throw new Exception(UserAddressConstants.ExceptionMessageCase.INVALID_USER_ADDRESS_ID);
        }
        return updateUser(id, null, null, null, null, null, defaultAddress);
    }

    public User updateUser(Long id, String name, String password, String email, String phone_extension,
            String phone_number, UserAddress defaultAddress)
            throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if (null == user) {
            throw new Exception(ExceptionMessageCase.INVALID_USER_ID);
        }
        if (name != null) {
            user.setName(name);
        }
        if (password != null) {
            String encodedPassword = getEncoder().encode(password);
            user.setPassword(encodedPassword);
        }
        // if (email != null) {
        // user.setEmail(email);
        // }
        if (phone_extension != null) {
            user.setPhone_extension(phone_extension);

        }
        if (phone_number != null) {
            user.setPhone_number(phone_number);
        }
        if (defaultAddress != null) {
            user.setDefaultAddress(defaultAddress);
        }
        return userRepository.save(user);
    }

    public User updateDefaultAddressToNull(Long id) throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if (null == user) {
            throw new Exception(ExceptionMessageCase.INVALID_USER_ID);
        }
        user.setDefaultAddress(null);
        return userRepository.save(user);
    }

    public Boolean deleteUserById(Long id) throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if (null == user) {
            throw new Exception(ExceptionMessageCase.INVALID_USER_ID);
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

    public JSONObject checkBodyOfAuthenticateUser(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(LowerCase.EMAIL)) {
            missingField = LowerCase.EMAIL;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.EMAIL);
        } else if (!body.has(LowerCase.PASSWORD)) {
            missingField = LowerCase.PASSWORD;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.PASSWORD);
        } else {
            String email = body.get(LowerCase.EMAIL).toString();
            Matcher matcher = pattern.matcher(email);
            Boolean isValidEmail = matcher.matches();
            if (!isValidEmail) {
                code = null;
                message = MessageCase.INVALID_EMAIL_FORMAT;
                missingField = LowerCase.EMAIL;
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

    public JSONObject checkBodyOfRegisterUser(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(LowerCase.EMAIL)) {
            missingField = LowerCase.EMAIL;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.EMAIL);
        } else if (!body.has(LowerCase.PASSWORD)) {
            missingField = LowerCase.PASSWORD;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.PASSWORD);
        } else if (!body.has(LowerCase.NAME)) {
            missingField = LowerCase.NAME;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.NAME);
        } else if (!body.has(SnakeCase.PHONE_NUMBER)) {
            missingField = SnakeCase.PHONE_NUMBER;
            message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.PHONE_NUMBER);
        } else {
            String email = body.get(LowerCase.EMAIL).toString();
            Matcher matcher = pattern.matcher(email);
            Boolean isValidEmail = matcher.matches();
            if (!isValidEmail) {
                code = null;
                message = MessageCase.INVALID_EMAIL_FORMAT;
                missingField = LowerCase.EMAIL;
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

    public JSONObject checkBodyOfUpdateUser(JSONObject body, User user) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (body.has(SnakeCase.OLD_PASSWORD)) {
            if (!body.has(SnakeCase.NEW_PASSWORD)) {
                missingField = SnakeCase.NEW_PASSWORD;
                message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.NEW_PASSWORD);
            } else {
                String existingPassword = user.getPassword();
                if (!existingPassword.equals(body.get(SnakeCase.OLD_PASSWORD).toString())) {
                    code = null;
                    message = MessageCase.INVALID_OLD_PASSWORD;
                    missingField = SnakeCase.OLD_PASSWORD;
                } else {
                    isSuccess = Boolean.TRUE;
                }
            }
        } else if (body.has(SnakeCase.NEW_PASSWORD)) {
            if (!body.has(SnakeCase.OLD_PASSWORD)) {
                missingField = SnakeCase.OLD_PASSWORD;
                message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.OLD_PASSWORD);
            } else {
                isSuccess = Boolean.TRUE;
            }
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
