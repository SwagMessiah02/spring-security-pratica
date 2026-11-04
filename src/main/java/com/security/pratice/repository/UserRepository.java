package com.security.pratice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.security.pratice.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findUserByUsername(String username);
	
	@Query("select u from User u left join fetch u.roles where u.username = :username")
	Optional<User> findUserRoleByUsername(String username);
}
