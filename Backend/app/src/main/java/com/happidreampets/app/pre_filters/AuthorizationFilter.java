package com.happidreampets.app.pre_filters;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.sql.exec.ExecutionException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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

        throw new ExecutionException("Could not determine the current controller instance.");
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
        if (!"OPTIONS".equals(request.getMethod())) {
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
                                wrappedResponse.setHeader(ControllerConstants.OtherCase.NEW_AUTHENTICATION_TOKEN,
                                        newToken);
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

            } catch (HttpRequestMethodNotSupportedException ex) {
                Map<String, Object> body = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                body = new HashMap<>();
                body.put(LowerCase.STATUS, LowerCase.ERROR);
                body.put(SnakeCase.ERROR_CODE, ERROR_CODES.RESOURCE_NOT_FOUND);
                body.put(LowerCase.MESSAGE,
                        "The Given URL is not found");

                String content = mapper.writeValueAsString(body);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, content);
            } catch (Exception ex) {
                String message = ex.getMessage() != null ? ex.getMessage() : "";
                Map<String, Object> body = new HashMap<>();
                ObjectMapper mapper = new ObjectMapper();
                String content = "";
                switch (message) {
                    case "A problem occurred in the SQL executor : Could not determine the current controller instance.":
                        body = new HashMap<>();
                        body.put(LowerCase.STATUS, LowerCase.ERROR);
                        body.put(SnakeCase.ERROR_CODE, ERROR_CODES.RESOURCE_NOT_FOUND);
                        body.put(LowerCase.MESSAGE,
                                "The Given URL is not found");

                        content = mapper.writeValueAsString(body);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, content);
                        return;
                    case ControllerConstants.ExceptionMessageCase.EXPIRED_AUTHENTICATION_TOKEN:
                        body = new HashMap<>();
                        body.put(LowerCase.STATUS, LowerCase.ERROR);
                        body.put(SnakeCase.ERROR_CODE, ERROR_CODES.EXPIRED_AUTH_TOKEN);
                        body.put(LowerCase.MESSAGE,
                                "The authorization token used in the request has expired. Please obtain a new token and try again.");

                        content = mapper.writeValueAsString(body);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, content);
                        break;
                    case ControllerConstants.ExceptionMessageCase.UNAUTHORIZED_USER:
                        body = new HashMap<>();
                        body.put(LowerCase.STATUS, LowerCase.ERROR);
                        body.put(SnakeCase.ERROR_CODE, ERROR_CODES.UNAUTHORIZED_ACCESS);
                        body.put(LowerCase.MESSAGE,
                                "The User does not have permission to access this resource");

                        content = mapper.writeValueAsString(body);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, content);
                        break;
                    case ControllerConstants.ExceptionMessageCase.UNAUTHENTICATED_USER:
                        body = new HashMap<>();
                        body.put(LowerCase.STATUS, LowerCase.ERROR);
                        body.put(SnakeCase.ERROR_CODE, ERROR_CODES.INVALID_AUTH_TOKEN);
                        body.put(LowerCase.MESSAGE,
                                "Invalid Authorization Token");

                        content = mapper.writeValueAsString(body);
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, content);
                        break;
                    default:
                        body = new HashMap<>();
                        body.put(LowerCase.STATUS, LowerCase.ERROR);
                        body.put(SnakeCase.ERROR_CODE, ERROR_CODES.INTERNAL_SERVER_ERROR);
                        body.put(LowerCase.MESSAGE, "Something went wrong.");

                        content = mapper.writeValueAsString(body);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, content);
                        break;
                }
            }
        } else {
            // filterChain.doFilter(request, response);
            CharResponseWrapper wrappedResponse = new CharResponseWrapper(response);
            wrappedResponse.setHeader("Access-Control-Allow-Origin", "*");
            wrappedResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            wrappedResponse.setHeader("Access-Control-Max-Age", "3600");
            wrappedResponse.setHeader("Access-Control-Allow-Headers", "authorization, content-type, xsrf-token");
            wrappedResponse.setStatus(HttpServletResponse.SC_OK);
            response = wrappedResponse;
        }
    }

    // private void writeErrorResponse(HttpServletResponse response, int status,
    // ERROR_CODES errorCode, String message)
    // throws IOException {
    // response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    // response.setStatus(status);

    // Map<String, Object> body = new HashMap<>();
    // body.put("status", "error");
    // body.put("error_code", errorCode);
    // body.put("message", message);

    // ObjectMapper mapper = new ObjectMapper();
    // String responseBody = mapper.writeValueAsString(body);

    // try (PrintWriter writer = response.getWriter()) {
    // writer.write(responseBody);
    // writer.flush();
    // }
    // }

    private void continueFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
            User user) throws Exception {
        apiController.setIsGuestUser(user == null);
        apiController.setCurrentUser(user);
        apiController.setIsInternalCall(isInternalCall());
        filterChain.doFilter(request, response);
    }

}
