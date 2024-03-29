package com.happidreampets.app.database.crud;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import com.happidreampets.app.constants.ProductConstants.OtherCase;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.constants.UserConstants.MessageCase;
import com.happidreampets.app.constants.UserConstants.CapitalizationCase;
import com.happidreampets.app.constants.UserConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.UserConstants.LowerCase;
import com.happidreampets.app.constants.UserConstants.SnakeCase;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.model.User.USER_ROLE;
import com.happidreampets.app.database.repository.UserRepository;
import com.happidreampets.app.email.EmailModel;
import com.happidreampets.app.email.EmailService;
import com.happidreampets.app.utils.Utils;

@Component
public class UserCRUD {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public PasswordEncoder getEncoder() {
        return encoder;
    }

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static String confirmationMailBody = "";
    private static String changePasswordOTPMailBody = "";

    public UserCRUD() throws IOException {
        confirmationMailBody = new String(
                Files.readAllBytes(
                        Paths.get(System.getProperty(OtherCase.USER_DOT_DIR)
                                + "/src/main/resources/templates/ConfirmationEmailBody.html")),
                StandardCharsets.UTF_8);
        changePasswordOTPMailBody = new String(
                Files.readAllBytes(
                        Paths.get(System.getProperty(OtherCase.USER_DOT_DIR)
                                + "/src/main/resources/templates/ForgotPasswordOTP.html")),
                StandardCharsets.UTF_8);
    }

    public boolean activateUserAsAdmin(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setConfirmationCode(null);
            user.setRole(USER_ROLE.ADMIN);
            user.setUserConfirmed(true);
            userRepository.save(user);
            return true;
        }

        return false;
    }

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

    public Boolean isUserExistsBasedOnEmail(String email) {
        User user = getUserBasedOnEmail(email);
        return user != null;
    }

    public User getUserBasedOnId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user;
    }

    public User createUser(String name, String password, String email, String phone_number, USER_ROLE role)
            throws Exception {
        return createUser(name, password, email, "91", phone_number, role);
    }

    public User createUser(String name, String password, String email, String phone_extension, String phone_number,
            USER_ROLE role) throws Exception {
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
        user.setRole(role);
        user.setAddedTime(System.currentTimeMillis());
        user.setUserConfirmed(false);
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // Check if the exception is due to a unique constraint violation
            if (ex.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex
                        .getCause();

                // Check if the exception is specifically due to a unique constraint violation
                if (constraintViolationException.getErrorCode() == 1062) {
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
        return updateUser(id, name, null, null, null, null);
    }

    public User updateUserPassword(Long id, String password) throws Exception {
        return updateUser(id, null, password, null, null, null);
    }

    public User updateUserPhoneNumber(Long id, String phone_extension, String phone_number) throws Exception {
        return updateUser(id, null, null, null, phone_extension, phone_number);
    }

    public User updateUser(Long id, String name, String password, String email, String phone_extension,
            String phone_number)
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

    public void sendConfirmationEmail(User user) {
        List<String> recipents = new ArrayList<>();
        recipents.add(user.getEmail());

        user.setConfirmationCode(generateCode(generateRandomLength(5, 9)));
        userRepository.save(user);
        EmailModel model = new EmailModel();
        model.setRecipients(recipents);
        model.setSubject("Complete Your Registration - Confirmation Code Inside");
        String body = confirmationMailBody.replace("###confirmation_code###", user.getConfirmationCode());
        model.setBody(body, true);
        emailService.sendSimpleMail(model);
    }

    public void sendChangePasswordOTPEmail(User user) {
        List<String> recipents = new ArrayList<>();
        recipents.add(user.getEmail());

        user.setForgotPasswordCode(generateCode(generateRandomLength(5, 9)));
        userRepository.save(user);
        EmailModel model = new EmailModel();
        model.setRecipients(recipents);
        model.setSubject("Forgot Password - OTP Inside");
        String body = changePasswordOTPMailBody.replace("###otp_code###", user.getForgotPasswordCode());
        model.setBody(body, true);
        emailService.sendSimpleMail(model);
    }

    public static int generateRandomLength(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    public static String generateCode(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    public Boolean confirmUser(Long id, String confirmationCode) throws Exception {
        User user = getUserBasedOnId(id);
        if (user == null) {
            throw new Exception(ExceptionMessageCase.INVALID_USER_ID);
        }
        if (user.getConfirmationCode() != null && user.getConfirmationCode().equals(confirmationCode)) {
            user.setConfirmationCode(null);
            user.setUserConfirmed(Boolean.TRUE);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
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

    public JSONObject checkBodyOfTriggerForgotPasswordOTP(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(LowerCase.EMAIL)) {
            missingField = LowerCase.EMAIL;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.EMAIL);
        } else {
            // String email = body.get(LowerCase.EMAIL).toString();
            // if (!isUserExistsBasedOnEmail(email)) {
            // code = null;
            // message = ExceptionMessageCase.INVALID_EMAIL;
            // missingField = LowerCase.EMAIL;
            // } else {
            isSuccess = Boolean.TRUE;
            // }

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

    public JSONObject checkBodyOfResendConfirmation(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(SnakeCase.USER_ID)) {
            missingField = SnakeCase.USER_ID;
            message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.USER_ID);
        } else {
            String userId = body.get(SnakeCase.USER_ID).toString();
            if (Utils.isStringLong(userId)) {
                code = null;
                message = ExceptionMessageCase.INVALID_USER_ID;
                missingField = SnakeCase.USER_ID;
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

    // public JSONObject checkBodyOfValidateChangePasswordOTP(JSONObject body) {
    // JSONObject response = new JSONObject();
    // Boolean isSuccess = Boolean.FALSE;
    // String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
    // String message =
    // ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
    // String code = ERROR_CODES.MANDATORY_MISSING.name();
    // if (!body.has(SnakeCase.USER_ID)) {
    // missingField = SnakeCase.USER_ID;
    // message = message.replace(ProductConstants.LoggerCase.ARG0,
    // SnakeCase.USER_ID);
    // } else if (!body.has(SnakeCase.FORGOT_PASSWORD_OTP)) {
    // missingField = SnakeCase.FORGOT_PASSWORD_OTP;
    // message = message.replace(ProductConstants.LoggerCase.ARG0,
    // SnakeCase.FORGOT_PASSWORD_OTP);
    // } else {
    // String userId = body.get(SnakeCase.USER_ID).toString();
    // if (!Utils.isStringLong(userId)) {
    // code = null;
    // message = ExceptionMessageCase.INVALID_USER_ID;
    // missingField = SnakeCase.USER_ID;
    // } else {
    // isSuccess = Boolean.TRUE;
    // }

    // }
    // response.put(ProductConstants.LowerCase.SUCCESS, isSuccess);
    // if (!isSuccess) {
    // response.put(ProductConstants.LowerCase.DATA,
    // new JSONObject().put(ProductConstants.LowerCase.FIELD, missingField)
    // .put(ProductConstants.LowerCase.CODE, code)
    // .put(ProductConstants.LowerCase.MESSAGE, message));
    // }
    // return response;
    // }

    public JSONObject checkBodyOfConfirmUser(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(SnakeCase.USER_ID)) {
            missingField = SnakeCase.USER_ID;
            message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.USER_ID);
        } else if (!body.has(SnakeCase.CONFIRMATION_CODE)) {
            missingField = SnakeCase.CONFIRMATION_CODE;
            message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.CONFIRMATION_CODE);
        } else {
            String userId = body.get(SnakeCase.USER_ID).toString();
            if (!Utils.isStringLong(userId)) {
                code = null;
                message = ExceptionMessageCase.INVALID_USER_ID;
                missingField = SnakeCase.USER_ID;
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

    public JSONObject checkBodyOfForgotPassword(JSONObject body) {
        JSONObject response = new JSONObject();
        Boolean isSuccess = Boolean.FALSE;
        String missingField = ProductConstants.LowerCase.EMPTY_QUOTES;
        String message = ProductConstants.MessageCase.MANDATORY_FIELD_ARG0_IS_MISSING;
        String code = ERROR_CODES.MANDATORY_MISSING.name();
        if (!body.has(LowerCase.EMAIL)) {
            missingField = LowerCase.EMAIL;
            message = message.replace(ProductConstants.LoggerCase.ARG0, LowerCase.EMAIL);
        } else {
            String email = body.get(LowerCase.EMAIL).toString();
            User user = getUserBasedOnEmail(email);
            if (user == null) {
                code = null;
                message = ExceptionMessageCase.INVALID_EMAIL;
                missingField = LowerCase.EMAIL;
            } else {
                if (body.has(SnakeCase.FORGOT_PASSWORD_OTP)) {
                    String forgotPasswordCode = user.getForgotPasswordCode();
                    if (!forgotPasswordCode.equals(body.get(SnakeCase.FORGOT_PASSWORD_OTP).toString())) {
                        code = null;
                        message = MessageCase.INVALID_FORGOT_PASSWORD_OTP;
                        missingField = SnakeCase.FORGOT_PASSWORD_OTP;
                    } else if (!body.has(SnakeCase.NEW_PASSWORD)) {
                        missingField = SnakeCase.NEW_PASSWORD;
                        message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.NEW_PASSWORD);
                    } else {
                        isSuccess = Boolean.TRUE;
                    }

                } else {
                    missingField = SnakeCase.FORGOT_PASSWORD_OTP;
                    message = message.replace(ProductConstants.LoggerCase.ARG0, SnakeCase.FORGOT_PASSWORD_OTP);
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
            } else if (body.get(SnakeCase.OLD_PASSWORD).toString()
                    .equals(body.get(SnakeCase.NEW_PASSWORD).toString())) {
                missingField = SnakeCase.NEW_PASSWORD;
                message = MessageCase.NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD_PASSWORD;
            } else {
                String existingPassword = user.getPassword();
                if (!getEncoder().matches(body.get(SnakeCase.OLD_PASSWORD).toString(), existingPassword)) {
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
            } else if (body.get(SnakeCase.OLD_PASSWORD).toString()
                    .equals(body.get(SnakeCase.NEW_PASSWORD).toString())) {
                missingField = SnakeCase.NEW_PASSWORD;
                message = MessageCase.NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD_PASSWORD;
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

    public Boolean isUserAdmin(User user) {
        return user != null ? user.getRole().equals(USER_ROLE.ADMIN) : false;
    }
}
