package com.happidreampets.app.database.crud;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.database.model.InternalAuthenticationToken;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.database.repository.InternalAuthenticationTokenRepository;

@Component
public class InternalAuthenticationTokenCRUD {

    @Autowired
    private InternalAuthenticationTokenRepository internalAuthenticationTokenRepository;

    @Value("${happidreampets.app.internal.jwtSecret}")
    private String jwtSecret;

    @Value("${happidreampets.app.internal.jwtExpirationMs}")
    private int jwtExpirationMs;

    public InternalAuthenticationToken getInternalAuthenticationTokenOfUser(User user) {
        return internalAuthenticationTokenRepository.findByUser(user);
    }

    public InternalAuthenticationToken getInternalAuthenticationTokenById(Long id) {
        return internalAuthenticationTokenRepository.findById(id).orElse(null);
    }

    public InternalAuthenticationToken insertInternalAuthenticationToken(User user, JSONObject tokenDetails)
            throws Exception {
        if (tokenDetails == null || tokenDetails.isEmpty() || !tokenDetails.has(UserConstants.SnakeCase.ACCESS_TOKEN)) {
            throw new Exception();
        }
        Long currentTime = System.currentTimeMillis();
        InternalAuthenticationToken internalAuthenticationToken = new InternalAuthenticationToken();
        internalAuthenticationToken.setExpiringTime(tokenDetails.getLong(UserConstants.SnakeCase.EXPIRATION_TIME));
        internalAuthenticationToken.setCreatedTime(tokenDetails.getLong(UserConstants.SnakeCase.CREATED_TIME));
        internalAuthenticationToken.setToken(tokenDetails.get(UserConstants.SnakeCase.ACCESS_TOKEN).toString());
        internalAuthenticationToken.setAddedTime(currentTime);
        internalAuthenticationToken.setUpdatedTime(currentTime);
        internalAuthenticationToken.setUser(user);
        return internalAuthenticationTokenRepository.save(internalAuthenticationToken);
    }

    public InternalAuthenticationToken updateInternalAuthenticationToken(
            InternalAuthenticationToken internalAuthenticationToken, Long expirationTime)
            throws Exception {
        internalAuthenticationToken.setExpiringTime(expirationTime);
        internalAuthenticationToken.setUpdatedTime(System.currentTimeMillis());
        return internalAuthenticationTokenRepository.save(internalAuthenticationToken);
    }

    public Boolean checkTokenForUser(User user, String token) {
        InternalAuthenticationToken existingAuthenticationToken = getInternalAuthenticationTokenOfUser(user);
        if (existingAuthenticationToken == null) {
            return false;
        }
        if (existingAuthenticationToken.getToken() == null) {
            return false;
        }
        return existingAuthenticationToken.getToken().equals(token);
    }

    public Boolean deleteInternalAuthenticationToken(User user) throws Exception {
        InternalAuthenticationToken existingAuthenticationToken = getInternalAuthenticationTokenOfUser(user);
        if (existingAuthenticationToken == null) {
            throw new Exception(ControllerConstants.ExceptionMessageCase.AUTHENTICATION_TOKEN_DOES_NOT_EXIST);
        }
        internalAuthenticationTokenRepository.delete(existingAuthenticationToken);
        return true;
    }

}
