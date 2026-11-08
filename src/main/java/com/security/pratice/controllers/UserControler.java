package com.security.pratice.controllers;

import java.util.Set;

import org.springframework.http.HttpStatusCode; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.security.pratice.models.Role;
import com.security.pratice.models.User;
import com.security.pratice.repository.UserRepository;
import com.security.pratice.security.JwtTokenService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
public class UserControler {
	private AuthenticationManager authenticationManager;
	private JwtTokenService jwtTokenService;
	private UserRepository repository;
	private PasswordEncoder passwordEncoder;
	
	public UserControler(AuthenticationManager authenticationManager, 
			JwtTokenService jwtTokenService,
			UserRepository repository,
			PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenService = jwtTokenService;
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDao loginRequest, HttpServletResponse response) {
		
		UsernamePasswordAuthenticationToken authentication 
			= UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
		
		var auth = this.authenticationManager.authenticate(authentication);
		
		String token = jwtTokenService.generateToken((UserDetails)auth.getPrincipal());
		
		return ResponseEntity.ok(new LoginResponseDTO(token, "Authenticated"));
	}
	
	@PostMapping("/register")
	@Transactional
	public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO dto) {
		User user = new User();
		Role role = new Role();
	
		role.setRole("USER");
		
		user.setUsername(dto.username());
		user.setEmail(dto.email());
		user.setPassword(passwordEncoder.encode(dto.password()));
		user.setRoles(Set.of(role));
		
		repository.save(user);
	
		return ResponseEntity.ok(new RegisterResponseDTO(200, "User created"));
	}
	
	@PostMapping("/protected")
	public ResponseEntity<String> protected_route() {
		return ResponseEntity.ok("Autorizado");
	}
}
