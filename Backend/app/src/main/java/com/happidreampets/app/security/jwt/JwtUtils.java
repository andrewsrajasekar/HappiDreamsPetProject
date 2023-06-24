package com.happidreampets.app.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.happidreampets.app.security.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private static final Logger LOG = Logger.getLogger(JwtUtils.class.getName());

    @Value("${happidreampets.app.jwtSecret}")
    private String jwtSecret;

    @Value("${happidreampets.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            LOG.log(Level.SEVERE, "Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOG.log(Level.SEVERE, "JWT token is expired: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOG.log(Level.SEVERE, "JWT token is unsupported: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, "JWT claims string is empty: " + ex.getMessage());
        }

        return false;
    }
}
