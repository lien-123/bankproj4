package com.example.demo.controller;

import com.example.demo.entity.Payee;
import com.example.demo.entity.User;
import com.example.demo.repository.PayeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PayeeController {

    @Autowired
    private PayeeRepository payeeRepo;

    @GetMapping("/payees/")
    public ResponseEntity<?> getPayees() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 未登入（Token 無效 / 沒附 Token）
        if (auth == null || auth.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(401)
                    .body(Map.of("detail", "Authentication credentials were not provided."));
        }

        User currentUser = (User) auth.getPrincipal();

        List<Payee> payees = payeeRepo.findByUserId(currentUser.getId());

        return ResponseEntity.ok(payees);
    }
}
