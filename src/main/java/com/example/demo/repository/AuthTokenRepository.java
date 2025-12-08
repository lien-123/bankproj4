package com.example.demo.repository;


import com.example.demo.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Integer> {
    AuthToken findByToken(String token);
}
