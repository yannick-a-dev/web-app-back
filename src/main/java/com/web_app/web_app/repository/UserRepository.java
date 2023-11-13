package com.web_app.web_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.web_app.web_app.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

}
