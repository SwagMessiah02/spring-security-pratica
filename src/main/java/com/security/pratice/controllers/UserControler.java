package com.security.pratice.controllers;

import org.springframework.http.HttpStatusCode; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class UserControler {
	private AuthenticationManager authenticationManager;
	
	public UserControler(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequestDao loginRequest, HttpServletResponse response) {
		
		UsernamePasswordAuthenticationToken authentication 
			= UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
		
		var auth = this.authenticationManager.authenticate(authentication);
		
		if(!auth.isAuthenticated()) {
			return ResponseEntity.status(HttpStatusCode.valueOf(401)).body("Unauthenticaded");
		}
		
		return ResponseEntity.ok("Authenticated");
	}
	
	@PostMapping("/protected")
	public ResponseEntity<String> protected_route() {
		return ResponseEntity.ok("Autorizado");
	}
}
