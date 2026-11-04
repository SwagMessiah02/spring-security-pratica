package com.security.pratice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.security.pratice.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findUserByUsername(String username);
}
