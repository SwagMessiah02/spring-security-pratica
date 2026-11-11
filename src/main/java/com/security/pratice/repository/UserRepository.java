package com.security.pratice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.security.pratice.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findUserByEmail(String email);
	
	// Find User by username and populate the roles association with Role instances eagerly
	@Query("select u from User u left join fetch u.roles where u.email = :email")
	Optional<User> findUserRoleByEmail(String email);
}
