package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepo;

    @GetMapping("/accounts/")
    public ResponseEntity<?> getAccounts() {

        // 取得 TokenFilter 放進來的登入使用者
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(401)
                    .body(Map.of("detail", "Authentication credentials were not provided."));
        }

        User currentUser = (User) auth.getPrincipal();

        // 查詢該 user 所有帳戶（回傳 List，可以一人多帳戶）
        List<Account> accounts = accountRepo.findByUserId(currentUser.getId());

        return ResponseEntity.ok(accounts);
    }
}
