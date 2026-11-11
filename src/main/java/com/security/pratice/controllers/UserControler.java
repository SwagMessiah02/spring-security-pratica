package com.security.pratice.controllers;

import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
public class UserControler {
	private AuthenticationManager authenticationManager;
	private JwtTokenService jwtTokenService;
	private UserRepository repository;
	private PasswordEncoder passwordEncoder;
	private JwtTokenService tokenService;
	
	public UserControler(AuthenticationManager authenticationManager, 
			JwtTokenService jwtTokenService,
			UserRepository repository,
			PasswordEncoder passwordEncoder,
			JwtTokenService tokenService) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenService = jwtTokenService;
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
		
		UsernamePasswordAuthenticationToken authentication 
			= UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.email(), loginRequest.password());
		
		var auth = this.authenticationManager.authenticate(authentication);
		
		String token = jwtTokenService.generateToken((UserDetails)auth.getPrincipal());
		
		return ResponseEntity.ok(new LoginResponseDTO(token, "Authenticated"));
	}
	
	@PostMapping("/register")
	@Transactional
	public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO dto) {
		
		if(repository.findUserByEmail(dto.email()).isPresent()) {
			return ResponseEntity.status(400).body(new RegisterResponseDTO(400, "User with same email already exists"));
		}
		
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
	
	@GetMapping("/validate")
	public ResponseEntity<ValidateResponseDTO> validate(HttpServletRequest request, HttpServletResponse response) {
		
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

	    if (header == null || !header.startsWith("Bearer ")) {
	        return ResponseEntity.status(400).body(new ValidateResponseDTO("", "Request without Authorization header or token"));
	    }

	    String token = header.substring(7).trim();
		
	    if(tokenService.isTokenValid(token)) {
	    	String username = tokenService.extractUsername(token);
	    	response.setHeader("X-Auth-User", username);
	    	
	    	return ResponseEntity.status(200).body(new ValidateResponseDTO(username, "Token validated"));
	    }
	    
	    return ResponseEntity.status(400).body(new ValidateResponseDTO("", "Invalid token"));
	}
}
