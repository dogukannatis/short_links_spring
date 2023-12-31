package com.linkshortener.security.jwt;

import com.linkshortener.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${linkshortener.app.jwtSecret}")
    private String jwtSecret;

    @Value("${linkshortener.app.jwtMailSecret}")
    private String jwtMailSecret;

    @Value("${linkshortener.app.jwtResetPasswordSecret}")
    private String jwtResetPasswordSecret;

    @Value("${linkshortener.app.jwtExpirationMs}")
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





    public String generateMailJwtToken(String id, String username, String email) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", id);
        claims.put("username", username);
        claims.put("email", email);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(mailKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateResetPasswordJwtToken(String id, String username, String email) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", id);
        claims.put("username", username);
        claims.put("email", email);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(resetPasswordKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private Key mailKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtMailSecret));
    }

    private Key resetPasswordKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtResetPasswordSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getUserNameFromMailJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(mailKey()
                ).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getUserNameFromResetPasswordJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(resetPasswordKey()
                ).build()
                .parseClaimsJws(token).getBody().getSubject();
    }


    public Claims parseJwt(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody();
    }



    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public boolean validateMailJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(mailKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public boolean validateResetPasswordJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(resetPasswordKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
