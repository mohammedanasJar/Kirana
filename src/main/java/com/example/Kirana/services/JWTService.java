package com.example.Kirana.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JWTService {
    private final String SECRET_KEY="6bf3216db0a2c533e1127cfe0081ed7b8f7126758c02cd9e4d4549ba264d5e3c";
    public String generateToken(UserDetails userDetails){
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolvers){
        final Claims claims=extractAllClaim(token);
        return claimsResolvers.apply(claims);
    }
    public String extractUsername(String token){
        return  extractClaim(token,Claims::getSubject);
    }
    private Claims extractAllClaim(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey()).build()
                .parseClaimsJws(token).getBody();
    }

    private Key getSignKey() {
        byte[] key= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUsername(token);
        return (username.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return  extractClaim(token,Claims::getExpiration).before(new Date());
    }

    public String generateRefreshToken(HashMap<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24*7))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
