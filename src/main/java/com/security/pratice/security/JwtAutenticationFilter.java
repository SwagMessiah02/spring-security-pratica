package com.security.pratice.security;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAutenticationFilter extends OncePerRequestFilter {
	
	private JwtTokenService jwtTokenService;
	private CustomUserDetailsService customUserDetailsService;
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(JwtAutenticationFilter.class);
	
	public JwtAutenticationFilter(JwtTokenService jwtTokenService, 
			CustomUserDetailsService customUserDetailsService) {
		this.jwtTokenService = jwtTokenService;
		this.customUserDetailsService = customUserDetailsService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    String header = request.getHeader(HttpHeaders.AUTHORIZATION);

	    if (header == null || !header.startsWith("Bearer ")) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    String token = header.substring(7).trim();

	    try {
	        if (!token.isEmpty()
	                && SecurityContextHolder.getContext().getAuthentication() == null
	                && jwtTokenService.isTokenValid(token)) {

	            String username = jwtTokenService.extractUsername(token);
	            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

	            var authToken = new UsernamePasswordAuthenticationToken(
	                    userDetails, null, userDetails.getAuthorities());
	            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(authToken);
	        }
	    } catch (ExpiredJwtException ex) {
            
        } catch (JwtException ex) {
         
        }

	    filterChain.doFilter(request, response);
	}
}
