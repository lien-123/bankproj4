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

        // ✔ 最正確、最安全的登入判斷
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return ResponseEntity.status(401)
                    .body(Map.of("detail", "Authentication credentials were not provided."));
        }

        User currentUser = (User) auth.getPrincipal();

        // 取得使用者帳戶
        List<Account> accounts = accountRepo.findByUserId(currentUser.getId());
        List<String> accountNos = accounts.stream()
                .map(Account::getAccountNumber)
                .collect(Collectors.toList());

        // 🔥 改成一次查詢所有交易紀錄（需你在 repo 新增 findByAccounts）
        List<Transaction> allTx = txRepo.findByAccounts(accountNos);

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
                .sorted((a, b) ->
                        b.get("completed_at").toString().compareTo(a.get("completed_at").toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
