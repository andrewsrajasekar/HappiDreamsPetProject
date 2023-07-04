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

    @Value("${happidreampets.app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private UserCRUD userCRUD;

    @Value("${happidreampets.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    private InternalAuthenticationTokenCRUD internalAuthenticationTokenCRUD;

    private Boolean isInternalCall = Boolean.FALSE;

    public Boolean getIsInternalCall() {
        return isInternalCall;
    }

    public void setIsInternalCall(Boolean isInternalCall) {
        this.isInternalCall = isInternalCall;
    }

    public JSONObject getJwtToken(User user, Boolean createIfNotFound) throws Exception {
        InternalAuthenticationToken internalAuthenticationToken = internalAuthenticationTokenCRUD
                .getInternalAuthenticationTokenOfUser(user);
        if (internalAuthenticationToken == null) {
            if (createIfNotFound) {
                internalAuthenticationToken = internalAuthenticationTokenCRUD
                        .insertInternalAuthenticationToken(user, this.generateJwtToken(user));
            } else {
                return null;
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

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public User getUserFromToken(String token) throws Exception {
        String subject = Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
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

    public Boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date tokenDate = claims.getExpiration();
        Calendar tokenDateCalendar = Calendar.getInstance();
        tokenDateCalendar.setTime(tokenDate);
        if (!tokenDateCalendar.getTimeZone().getID().equals("UTC")) {
            tokenDateCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        if (currentCalendar.equals(tokenDateCalendar) || currentCalendar.before(tokenDateCalendar)) {
            return true;
        }
        return false;
    }

    private InternalAuthenticationToken regenerateExpiredAuthenticationTokenForInternalUser(User user) {
        try {
            if (getIsInternalCall() && user != null) {
                internalAuthenticationTokenCRUD
                        .deleteInternalAuthenticationToken(user);
                InternalAuthenticationToken newToken = internalAuthenticationTokenCRUD
                        .insertInternalAuthenticationToken(user, this.generateJwtToken(user));
                return newToken;
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Exception in Generating expirationToken: " + ex.getMessage());
        }
        return null;
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
            Boolean isTokenExists = internalAuthenticationTokenCRUD.checkTokenForUser(user, authToken);
            if (isTokenExists) {
                Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
                returnData.remove(ControllerConstants.LowerCase.REASON);
            }
            returnData.put(ControllerConstants.LowerCase.STATUS, isTokenExists);
            returnData.put(UserConstants.SnakeCase.USER_INFO, user);
        } catch (MalformedJwtException ex) {
            LOG.log(Level.SEVERE, "Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOG.log(Level.SEVERE, "JWT token is expired: " + ex.getMessage());
            InternalAuthenticationToken newToken = regenerateExpiredAuthenticationTokenForInternalUser(user);
            if (newToken != null) {
                returnData.put(ControllerConstants.SnakeCase.NEW_AUTHENTICATION_TOKEN,
                        newToken);
            } else {
                returnData.put(ControllerConstants.LowerCase.REASON,
                        ControllerConstants.SnakeCase.EXPIRED_AUTHENTICATION_TOKEN);
            }

        } catch (UnsupportedJwtException ex) {
            LOG.log(Level.SEVERE, "JWT token is unsupported: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "JWT claims string is empty: " + ex.getMessage());
        }

        return returnData;
    }
}