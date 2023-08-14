package com.happidreampets.app.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.controller.APIController.ERROR_CODES;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOG = Logger.getLogger(AuthEntryPoint.class.getName());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        LOG.log(Level.INFO, "Unauthorized error: " + authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> body = new HashMap<>();
        body.put(ControllerConstants.LowerCase.STATUS, HttpServletResponse.SC_UNAUTHORIZED);
        body.put(ControllerConstants.SnakeCase.ERROR_CODE, ERROR_CODES.UNAUTHORIZED_ACCESS);
        body.put(ControllerConstants.LowerCase.MESSAGE, authException.getMessage());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

}
