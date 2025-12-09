package com.jwt.JWTApp.Configuration;

import com.jwt.JWTApp.Globals.JWTUtil;
import com.jwt.JWTApp.Security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@RequiredArgsConstructor
@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwt;
    private final CustomUserDetailsService userDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("Inside doFilterInternal");
        String header = request.getHeader("Authorization");
        log.debug(header);
        if(null != header && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String email = jwt.extractUsername(token);

            if(email!=null) {
                log.debug("Email not null -> {}", email);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.debug(userDetails.toString());
                if (jwt.isTokenValid(token, userDetails)) {
                    log.info("Yes the token is valid. Now generating authToken from username and password.");
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    log.debug("Got authToken {}", authToken.toString());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.debug("Setting inside security context{}",authToken.toString());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        log.info("Calling doFilter from doFilterInternal with req{} and resp{}",request,response);
        filterChain.doFilter(request, response);
    }
}
