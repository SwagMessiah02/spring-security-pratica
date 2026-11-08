package com.security.pratice.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenService {
	
	@Value("${jwt.secret}")
	private String crypt;
	private long expirationMs = 60000 * 60 * 24; // 24 horas
	
	public String generateToken(UserDetails userDetails) {
		
		Map<String, Object> claims = new HashMap<>();
		
		claims.put("roles", userDetails
								.getAuthorities()
								.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()));
		
		return Jwts.builder()
				.claims(claims)
				.subject(userDetails.getUsername())
				.issuer("auth-api")
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expirationMs))
				.signWith(getKey(crypt))
				.compact();
	}
	
	public boolean isTokenValid(String token) {
		Assert.notNull(token, "Token is null");
		
		Claims claims = parseClaims(token);
		
		if(!claims.isEmpty()) return true;
		
		return false;		
	}
	
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
	
	private Claims parseClaims(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(getKey(crypt))
				.build()
				.parseSignedClaims(token)
				.getPayload();
		
		return claims;
	}
	
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseClaims(token));
    }
	
	private SecretKey getKey(String crypt) {
		byte[] keyBytes = Decoders.BASE64.decode(crypt);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
