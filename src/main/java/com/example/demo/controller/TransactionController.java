package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository txRepo;

    @GetMapping("/transactions/")
    public ResponseEntity<?> getTransactions() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(401)
                    .body(Map.of("detail", "Authentication credentials were not provided."));
        }

        User currentUser = (User) auth.getPrincipal();

        // 取得所有使用者帳戶
        List<Account> accounts = accountRepo.findByUserId(currentUser.getId());
        List<String> accountNos = accounts.stream()
                .map(Account::getAccountNumber)
                .collect(Collectors.toList());

        // 取得所有交易紀錄（出帳 + 入帳）
        List<Transaction> allTx = new ArrayList<>();
        for (String acc : accountNos) {
            allTx.addAll(txRepo.findBySenderAccountOrReceiverAccount(acc, acc));
        }

        // 組成與 Django 一樣的輸出格式
        List<Map<String, Object>> result = allTx.stream()
                .map(tx -> {
                    Map<String, Object> map = new HashMap<>();

                    boolean outgoing = accountNos.contains(tx.getSenderAccount());
                    BigDecimal amount = outgoing ? tx.getAmount().negate() : tx.getAmount();

                    map.put("amount", amount);
                    map.put("note", tx.getNote());
                    map.put("to_account_owner_name", tx.getReceiverAccount());
                    map.put("completed_at", tx.getCompletedAt().toString());

                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
