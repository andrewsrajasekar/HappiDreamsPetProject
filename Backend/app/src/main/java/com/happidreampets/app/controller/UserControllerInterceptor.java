package com.happidreampets.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.database.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class UserControllerInterceptor extends APIController implements HandlerInterceptor {

    @Autowired
    private UserController userController;

    private Boolean isUserIdPresent = Boolean.FALSE;

    @SuppressWarnings("unchecked")
    private Map<String, Object> getPathVariables(HttpServletRequest request) {
        Object attribute = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attribute instanceof Map) {
            return (Map<String, Object>) attribute;
        }
        return new HashMap<String, Object>();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            RequestMapping mappingInfo = handlerMethod.getMethod().getAnnotation(RequestMapping.class);

            String[] urlPatterns = mappingInfo.value();

            // Process the API structure as per your requirement
            for (String pattern : urlPatterns) {
                if (pattern.contains("{userId}")) {
                    isUserIdPresent = Boolean.TRUE;
                }
            }
        }

        Boolean isPathVariablePassed = checkPathVariables(request, response, handler);
        return isPathVariablePassed;
    }

    private Boolean checkPathVariables(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        Map<String, Object> pathVariables = getPathVariables(request);

        FailureResponse failureResponse = new FailureResponse();

        Object userIdObj = pathVariables.getOrDefault("userId", null);

        if (isUserIdPresent) {
            if (userIdObj != null) {
                Long userId = Long.valueOf(userIdObj.toString());
                User user = getUserCRUD().getUserBasedOnId(userId);
                if (null == user) {
                    failureResponse.setApiResponseStatus(HttpStatus.NOT_FOUND);
                    failureResponse.setData(
                            new JSONObject().put(ProductConstants.LowerCase.FIELD, "user").put(LowerCase.MESSAGE,
                                    "User Id is Invalid"));
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                    response.setContentType("application/json");
                    response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                    response.getWriter().flush();
                    return false;
                }
                userController.setCurrentUser(user);
            } else {
                failureResponse.setApiResponseStatus(HttpStatus.BAD_REQUEST);
                failureResponse
                        .setData(new JSONObject().put(ProductConstants.LowerCase.FIELD, "user").put(LowerCase.MESSAGE,
                                "user Id is Invalid"));
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setContentType("application/json");
                response.getWriter().write(failureResponse.throwInvalidPathVariable().getBody().toString());
                response.getWriter().flush();
                return false;
            }
        }

        return true;
    }

}
