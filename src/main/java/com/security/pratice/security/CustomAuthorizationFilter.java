package com.security.pratice.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.pratice.controllers.LoginRequestDao;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {
	
	private ObjectMapper objectMapper;
	private CustomUserDetailsService customUserDetailsService;
	private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	public CustomAuthorizationFilter(ObjectMapper objectMapper, CustomUserDetailsService customUserDetailsService) {
		this.objectMapper = objectMapper;
		this.customUserDetailsService = customUserDetailsService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
			LoginRequestDao body = objectMapper.readValue(request.getInputStream(), LoginRequestDao.class);	

			if(body != null && !request.getRequestURI().equals("/login")) {
				log.info("username: {}", body.username());
					
				UserDetails userDetails = customUserDetailsService.loadUserByUsername(body.username());
					
				UsernamePasswordAuthenticationToken token = 
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					
				SecurityContextHolder.getContext().setAuthentication(token);
			}
		
			filterChain.doFilter(request, response);
	}
	
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/login");
    }
}
