package com.cloud.communitiesservice.util;

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
import java.util.function.Function;

@Component
@RefreshScope
public class JwtUtilService implements Serializable {
    @Value("${jwt.secret}")
    private String secretKeyString;
    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }
    public String getRolesFromToken(String token){
        var claims = getAllClaimsFromToken(token);
        return claims.get("roles", String.class);
    }
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    public UserDetails getUserDetailsFromToken(String token, String roles){
        var subject = getClaimFromToken(token, Claims::getSubject);
        return User.builder().username(subject).password("").roles(roles.split(",")).build();
    }
}
