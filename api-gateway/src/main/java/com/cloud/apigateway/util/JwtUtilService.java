package com.cloud.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

@Component
@RefreshScope
public class JwtUtilService implements Serializable {
    @Value("${jwt.secret}")
    private String secretKeyString;
    //for retrieving any information from token we will need the secret key
    public Claims getAllClaimsFromToken(String token) {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }
    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //check if the token has expired
    public boolean isValidAndNonExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.after(new Date());
    }
    public String getRolesFromToken(String token){
        var claims = getAllClaimsFromToken(token);
        return claims.get("roles", String.class);
    }
    public UserDetails getUserDetailsFromToken(String token, String roles){
        var subject = getClaimFromToken(token, Claims::getSubject);
        return User.builder().username(subject).password("").roles(roles.split(",")).build();
    }
}
