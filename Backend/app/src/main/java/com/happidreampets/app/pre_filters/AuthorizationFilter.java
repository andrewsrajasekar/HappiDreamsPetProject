package com.happidreampets.app.pre_filters;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.ControllerConstants.LowerCase;
import com.happidreampets.app.constants.ControllerConstants.SnakeCase;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.controller.APIController;
import com.happidreampets.app.controller.APIController.ERROR_CODES;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.security.jwt.JwtUtils;
import com.happidreampets.app.utils.AccessLevel;
import com.happidreampets.app.utils.AccessLevel.AccessEnum;
import com.happidreampets.app.utils.URLData;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

@Service
public class AuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    private APIController apiController;

    @Autowired
    private HandlerMapping handlerMapping;

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

    private APIController getCurrentController(HttpServletRequest request) throws Exception {
        HandlerExecutionChain executionChain = handlerMapping.getHandler(request);

        if (executionChain != null) {
            Object handler = executionChain.getHandler();

            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Object controller = handlerMethod.getBean();

                if (controller instanceof APIController) {
                    return (APIController) controller;
                }
            }
        }

        throw new Exception("Could not determine the current controller instance.");
    }

    private Method getMethod(HttpServletRequest request) throws Exception {

        String requestUri = getRequestUri(request);
        String httpMethod = request.getMethod();

        HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);
        Object handler = handlerChain.getHandler();

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return handlerMethod.getMethod();
        } else {
            throw new NoSuchMethodException("Method not found for request: " + httpMethod + " " + requestUri);
        }
    }

    private String getRequestUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath.length() > 0) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }

    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private final CharArrayWriter writer = new CharArrayWriter();

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() {
            return new PrintWriter(writer);
        }
    }

    private Boolean isInternalCall() {
        return Boolean.TRUE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            this.apiController = getCurrentController(request);
            Method method = getMethod(request);
            List<AccessEnum> accessRoles = URLData.getAccessRoleOfMethod(method);
            String jwt = parseJwt(request);
            if (jwt != null) {
                JSONObject jwtValidatedData = jwtUtils.validateJwtToken(jwt);
                Boolean validationStatus = jwtValidatedData.getBoolean(ControllerConstants.LowerCase.STATUS);
                if (validationStatus) {
                    User user = jwtValidatedData.has(UserConstants.SnakeCase.USER_INFO)
                            ? (User) jwtValidatedData.get(UserConstants.SnakeCase.USER_INFO)
                            : jwtUtils.getUserFromToken(jwt);
                    if (!accessRoles.isEmpty()) {
                        if (!accessRoles.contains(AccessLevel.Convertion.toAccessEnumFromRole(user.getRole()))) {
                            throw new Exception(ControllerConstants.ExceptionMessageCase.UNAUTHORIZED_USER);
                        }
                    }
                    continueFilter(request, response, filterChain, user);
                } else {
                    String reason = "";
                    if (jwtValidatedData.has(ControllerConstants.LowerCase.REASON)) {
                        reason = jwtValidatedData.getString(ControllerConstants.LowerCase.REASON);
                    }
                    if (reason.equals(ControllerConstants.SnakeCase.EXPIRED_AUTHENTICATION_TOKEN)) {
                        if (jwtValidatedData.has(ControllerConstants.SnakeCase.NEW_AUTHENTICATION_TOKEN)) {
                            String newToken = jwtValidatedData
                                    .getString(ControllerConstants.SnakeCase.NEW_AUTHENTICATION_TOKEN);
                            CharResponseWrapper wrappedResponse = new CharResponseWrapper(response);
                            wrappedResponse.setHeader(ControllerConstants.OtherCase.NEW_AUTHENTICATION_TOKEN, newToken);
                            response = wrappedResponse;
                        }
                        throw new Exception(ControllerConstants.ExceptionMessageCase.EXPIRED_AUTHENTICATION_TOKEN);
                    }
                    throw new Exception(ControllerConstants.ExceptionMessageCase.UNAUTHENTICATED_USER);
                }
            } else {
                if (accessRoles.isEmpty() && isInternalCall()) {
                    continueFilter(request, response, filterChain, null);
                } else {
                    throw new Exception(ControllerConstants.ExceptionMessageCase.UNAUTHENTICATED_USER);
                }
            }

        } catch (Exception ex) {
            String message = ex.getMessage() != null ? ex.getMessage() : "";
            Map<String, Object> body = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            switch (message) {
                case ControllerConstants.ExceptionMessageCase.EXPIRED_AUTHENTICATION_TOKEN:
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    body = new HashMap<>();
                    body.put(LowerCase.STATUS, LowerCase.ERROR);
                    body.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_AUTH_TOKEN);
                    body.put(LowerCase.MESSAGE,
                            "The authorization token used in the request has expired. Please obtain a new token and try again.");

                    mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), body);
                    break;
                case ControllerConstants.ExceptionMessageCase.UNAUTHORIZED_USER:
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    body = new HashMap<>();
                    body.put(LowerCase.STATUS, LowerCase.ERROR);
                    body.put(SnakeCase.ERROR_CODE, ERROR_CODES.UNAUTHORIZED_ACCESS);
                    body.put(LowerCase.MESSAGE,
                            "The User does not have permission to access this resource");

                    mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), body);
                    break;
                case ControllerConstants.ExceptionMessageCase.UNAUTHENTICATED_USER:
                default:
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                    body = new HashMap<>();
                    body.put(LowerCase.STATUS, LowerCase.ERROR);
                    body.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_AUTH_TOKEN);
                    body.put(LowerCase.MESSAGE, "Invalid Authorization Token");

                    mapper = new ObjectMapper();
                    mapper.writeValue(response.getOutputStream(), body);
                    break;
            }

        }
    }

    private void continueFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
            User user) throws Exception {
        apiController.setIsGuestUser(user == null);
        apiController.setCurrentUser(user);
        apiController.setIsInternalCall(isInternalCall());
        filterChain.doFilter(request, response);
    }

}
