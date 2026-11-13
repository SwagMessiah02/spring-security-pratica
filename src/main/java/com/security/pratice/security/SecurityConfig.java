package com.security.pratice.security;

import org.springframework.context.annotation.Bean;  
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import com.security.pratice.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private UserRepository userRepository;
	
	public SecurityConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
	    .csrf(c -> c.disable())
	    .sessionManagement(session ->
	        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
	    .authorizeHttpRequests(request -> request
	        .requestMatchers("/login", "/register", "/validate", "/callback").permitAll()
	        .requestMatchers("/", "/public/**").permitAll()
	        .requestMatchers("/protected").hasAuthority("USER")
	        .anyRequest().authenticated())
	    	.oauth2Login(o -> o
	                .defaultSuccessUrl("http://localhost/api/protected", true))
	    	.oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
	    	.logout(logout -> logout.logoutSuccessUrl("/"));

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
		return new ProviderManager(authenticationProvider);
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		var dao = new DaoAuthenticationProvider(userDetailsService);
		dao.setPasswordEncoder(passwordEncoder);
		
		return dao;
	}
	
	@Bean
	public UserDetailsService userDetailsService(UserRepository userRepository) {
		return new CustomUserDetailsService(this.userRepository);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	OAuth2AuthorizedClientRepository authorizedClientRepository() {
	    return new HttpSessionOAuth2AuthorizedClientRepository();
	}		
}
