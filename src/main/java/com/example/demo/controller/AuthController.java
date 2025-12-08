package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.entity.AuthToken;
import com.example.demo.entity.User;
import com.example.demo.repository.AuthTokenRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthTokenRepository tokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** -------------------------
     *  登入：比對 BCrypt 密碼
     * -------------------------- */
    @PostMapping("/login/")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        User user = userRepo.findByUsername(req.getUsername());

        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "invalid credentials"));
        }

        // 產生 Token
        String tokenStr = UUID.randomUUID().toString();
        AuthToken token = new AuthToken();
        token.setToken(tokenStr);
        token.setUserId(user.getId());
        tokenRepo.save(token);

        return ResponseEntity.ok(
                Map.of("detail", "login success", "token", tokenStr));
    }

    /** -------------------------
     *  登出：刪除 Token（利用 TokenFilter 提供的 user）
     * -------------------------- */
    @PostMapping("/logout/")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {

        if (!authHeader.startsWith("Token ")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "invalid token format"));
        }

        String tokenValue = authHeader.substring(6);

        AuthToken token = tokenRepo.findByToken(tokenValue);

        if (token == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "invalid token"));
        }

        tokenRepo.delete(token);

        return ResponseEntity.ok(Map.of("detail", "logout success"));
    }
}
