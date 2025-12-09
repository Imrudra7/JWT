package com.jwt.JWTApp.Globals;

import com.jwt.JWTApp.DTO.UserDTO;
import com.jwt.JWTApp.Model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.util.Date;
@Slf4j
@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-ms}")
    private Long expiredIn;
    @Value("${jwt.refresh-expiration-ms}")
    private String refreshExpiration;

    public String generateToken (User user) {
        log.debug("Inside generateToken with userDetails {}",user.toString() );
        return Jwts.builder()
                .setSubject(user.getUserEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiredIn))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        log.debug("Inside extractUsername with token{}",token );
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Inside isTokenValid with token{} and userDetails{}",token,userDetails.toString());
        try {
            final String username = extractUsername(token);
            log.info("Got extracted username {}",username);
            return username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token);
        } catch (JwtException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        log.debug("Inside isTokenExxpired with token {}",token);
        Date exp = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        log.info("Got the expiry date {} and current date is {}",exp, new Date());
        return exp.before(new Date());
    }
}
