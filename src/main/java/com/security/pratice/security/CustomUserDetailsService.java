package com.security.pratice.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.security.pratice.models.Role;
import com.security.pratice.models.User;
import com.security.pratice.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	private UserRepository userRepository;
	private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
		
		List<SimpleGrantedAuthority> authorities = new ArrayList<>(Collections.emptyList());
		
		user.getRoles().stream().map(Role::getRole)
			.toList().forEach((e) -> authorities.add(new SimpleGrantedAuthority(e)));
		
		user.getRoles().stream().map(Role::getRole)
		.toList().forEach((e) -> log.info("Role: {}", e));
		
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername())
				.password(user.getPassword())
				.authorities(authorities)
				.build();
	}
}
