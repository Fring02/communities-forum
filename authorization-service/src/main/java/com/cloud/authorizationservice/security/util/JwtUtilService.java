package com.cloud.authorizationservice.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtilService implements Serializable {
    private final UserDetailsService userDetailsService;
    @Serial
    private static final long serialVersionUID = -2550185165626007488L;
    @Value("${jwt.expirationDuration}")
    private long expirationDuration;
    @Value("${jwt.secret}")
    private String secretKeyString;
    public JwtUtilService(UserDetailsService userDetailsService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
    }
    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
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
    private boolean isExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    //generate token for user
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        var roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority() + ",")
                .collect(Collectors.joining());
        roles = StringUtils.chop(roles);
        claims.put("roles", roles);
        return generateAccessToken(claims, userDetails.getUsername());
    }
    public String generateRefreshToken(){
        var bytes = new byte[16];
        new Random().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String generateAccessToken(Map<String, Object> claims, String subject) {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationDuration * 1000))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }
    //validate token
    public UserDetails validateTokenAndGetUser(String token) {
        if(isExpired(token)) throw new JwtException("Given token is expired");
        String username = getUsernameFromToken(token);
        if(username == null) throw new JwtException("Failed to retrieve username from token");
        return userDetailsService.loadUserByUsername(username);
    }
}
