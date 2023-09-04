package com.happidreampets.app.security.jwt;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.happidreampets.app.constants.ControllerConstants.ExceptionMessageCase;
import com.happidreampets.app.constants.ControllerConstants;
import com.happidreampets.app.constants.UserConstants;
import com.happidreampets.app.database.crud.InternalAuthenticationTokenCRUD;
import com.happidreampets.app.database.crud.UserCRUD;
import com.happidreampets.app.database.model.InternalAuthenticationToken;
import com.happidreampets.app.database.model.User;
import com.happidreampets.app.utils.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private static final Logger LOG = Logger.getLogger(JwtUtils.class.getName());

    @Value("${happidreampets.app.internal.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private UserCRUD userCRUD;

    @Value("${happidreampets.app.internal.jwtExpirationMs}")
    private Integer jwtExpirationMs;

    @Autowired
    private InternalAuthenticationTokenCRUD internalAuthenticationTokenCRUD;

    private Boolean isInternalCall = Boolean.FALSE;

    public Boolean getIsInternalCall() {
        return isInternalCall;
    }

    public void setIsInternalCall(Boolean isInternalCall) {
        this.isInternalCall = isInternalCall;
    }

    public JSONObject getJwtToken(User user, Boolean createIfNotFound, Boolean regenerateIfExpired) throws Exception {
        InternalAuthenticationToken internalAuthenticationToken = internalAuthenticationTokenCRUD
                .getInternalAuthenticationTokenOfUser(user);
        if (internalAuthenticationToken == null) {
            if (createIfNotFound) {
                internalAuthenticationToken = internalAuthenticationTokenCRUD
                        .insertInternalAuthenticationToken(user, this.generateJwtToken(user));
            } else {
                return null;
            }
        } else {
            if (regenerateIfExpired && isTokenExpired(internalAuthenticationToken)) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.add(Calendar.MILLISECOND, jwtExpirationMs);
                internalAuthenticationToken = internalAuthenticationTokenCRUD.updateInternalAuthenticationToken(
                        internalAuthenticationToken,
                        calendar.getTime().getTime());
            }
        }
        JSONObject data = new JSONObject();
        data.put(UserConstants.SnakeCase.ACCESS_TOKEN, internalAuthenticationToken.getToken());

        Calendar tokenDateCalendar = Calendar.getInstance();
        tokenDateCalendar.setTimeInMillis(internalAuthenticationToken.getExpiringTime());
        if (!tokenDateCalendar.getTimeZone().getID().equals("UTC")) {
            tokenDateCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        data.put(UserConstants.SnakeCase.EXPIRATION_TIME, tokenDateCalendar.getTime());

        tokenDateCalendar = Calendar.getInstance();
        tokenDateCalendar.setTimeInMillis(internalAuthenticationToken.getCreatedTime());
        if (!tokenDateCalendar.getTimeZone().getID().equals("UTC")) {
            tokenDateCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        data.put(UserConstants.SnakeCase.CREATED_TIME, tokenDateCalendar.getTime());
        return data;
    }

    public JSONObject generateJwtToken(User user) {
        JSONObject data = new JSONObject();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date currentDate = calendar.getTime();
        calendar.add(Calendar.MILLISECOND, jwtExpirationMs);
        Date expirationDate = calendar.getTime();
        String token = Jwts.builder()
                .setSubject((user.getId().toString()))
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
        data.put(UserConstants.SnakeCase.ACCESS_TOKEN, token);
        data.put(UserConstants.SnakeCase.EXPIRATION_TIME, expirationDate.getTime());
        data.put(UserConstants.SnakeCase.CREATED_TIME, currentDate.getTime());
        return data;
    }

    public void removeToken(User user) throws Exception {
        internalAuthenticationTokenCRUD.deleteInternalAuthenticationToken(user);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public User getUserFromToken(String token) throws Exception {
        String subject = null;
        try {
            subject = Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException ex) {
            subject = ex.getClaims().getSubject();
        }

        if (subject == null) {
            throw new Exception(ExceptionMessageCase.INVALID_AUTHENTICATION_TOKEN);
        }
        if (!Utils.isStringLong(subject)) {
            throw new Exception(ExceptionMessageCase.INVALID_AUTHENTICATION_TOKEN);
        }
        Long userId = Long.valueOf(subject);
        User user = userCRUD.getUserBasedOnId(userId);
        if (user == null) {
            throw new Exception(ExceptionMessageCase.INVALID_AUTHENTICATION_TOKEN);
        }
        return user;
    }

    public User getUserFromTokenWithoutException(String token) {
        try {
            return getUserFromToken(token);
        } catch (Exception ex) {
            return null;
        }
    }

    public Boolean isTokenExpired(InternalAuthenticationToken internalAuthenticationToken) {
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar tokenDateCalendar = Calendar.getInstance();
        tokenDateCalendar.setTimeInMillis(internalAuthenticationToken.getExpiringTime());
        if (!tokenDateCalendar.getTimeZone().getID().equals("UTC")) {
            tokenDateCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        if (currentCalendar.after(tokenDateCalendar)) {
            return true;
        }
        return false;
    }

    private Boolean isSameDayUpdate(InternalAuthenticationToken authenticationToken) {
        Calendar lastUpdatedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Long lastUpdatedTime = authenticationToken.getUpdatedTime();
        lastUpdatedCalendar.setTimeInMillis(lastUpdatedTime);

        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        boolean sameDay = lastUpdatedCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && lastUpdatedCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)
                && lastUpdatedCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH);

        return sameDay;
    }

    public JSONObject validateJwtToken(String authToken) {
        JSONObject returnData = new JSONObject();
        returnData.put(ControllerConstants.LowerCase.STATUS, Boolean.FALSE);
        returnData.put(ControllerConstants.LowerCase.REASON,
                ControllerConstants.SnakeCase.INVALID_AUTHENTICATION_TOKEN);
        User user = null;
        try {
            user = getUserFromTokenWithoutException(authToken);
            if (user == null) {
                return returnData;
            }
            InternalAuthenticationToken authenticationToken = internalAuthenticationTokenCRUD
                    .getInternalAuthenticationTokenOfUser(user);
            Boolean isTokenExists = Boolean.FALSE;
            if (authenticationToken.getToken().equals(authToken)) {
                if (isTokenExpired(authenticationToken)) {
                    throw new ExpiredJwtException(null, null, "Given token " + authToken + " is expired.");
                } else {
                    if (!isSameDayUpdate(authenticationToken)) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.add(Calendar.MILLISECOND, jwtExpirationMs);
                        internalAuthenticationTokenCRUD.updateInternalAuthenticationToken(authenticationToken,
                                calendar.getTime().getTime());
                    }
                }
                isTokenExists = Boolean.TRUE;
                returnData.remove(ControllerConstants.LowerCase.REASON);
            } else {
                throw new MalformedJwtException("Given token " + authToken + " is invalid.");
            }
            returnData.put(ControllerConstants.LowerCase.STATUS, isTokenExists);
            returnData.put(UserConstants.SnakeCase.USER_INFO, user);
        } catch (MalformedJwtException ex) {
            LOG.log(Level.SEVERE, "Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOG.log(Level.SEVERE, "JWT token is expired: " + ex.getMessage());
            returnData.put(ControllerConstants.LowerCase.REASON,
                    ControllerConstants.SnakeCase.EXPIRED_AUTHENTICATION_TOKEN);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Exception in JWT Validation: " + ex.getMessage());
        }

        return returnData;
    }
}
