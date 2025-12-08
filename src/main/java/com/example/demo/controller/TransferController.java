package com.example.demo.controller;

import com.example.demo.dto.TransferRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransferController {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private PayeeRepository payeeRepo;

    @Autowired
    private TransactionRepository txRepo;

    /** -----------------------------
     *   轉帳 API（使用 TokenFilter）
     * ----------------------------- */
    @PostMapping("/transfer/")
    @Transactional
    public ResponseEntity<?> transfer(@RequestBody TransferRequest req) {

        // 取得目前登入的 User（由 TokenFilter 設定）
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(401)
                    .body(Map.of("detail", "Authentication credentials were not provided."));
        }

        User currentUser = (User) auth.getPrincipal();

        // 1️⃣ 查詢轉出帳戶（必須是自己的）
        Account sender = accountRepo.findById(req.getFrom_account_id()).orElse(null);
        if (sender == null || !sender.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "Sender account not found or does not belong to you"));
        }

        // 2️⃣ 查詢 Payee（必須是自己的常用收款人）
        Payee payee = payeeRepo.findById(req.getPayee_id()).orElse(null);
        if (payee == null || !payee.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "Payee not found or does not belong to you"));
        }

        // 3️⃣ 查詢 Receiver 的 Account（Payee 指向的帳戶）
        Account receiver = accountRepo.findByAccountNumber(payee.getAccountNumber());
        if (receiver == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "Receiver account not found"));
        }

        // 4️⃣ 檢查餘額
        BigDecimal amount = req.getAmount();
        if (sender.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("detail", "Insufficient balance"));
        }

        // 5️⃣ 執行轉帳（原子性）
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepo.save(sender);
        accountRepo.save(receiver);

        // 6️⃣ 紀錄交易
        Transaction tx = new Transaction();
        tx.setSenderAccount(sender.getAccountNumber());
        tx.setReceiverAccount(receiver.getAccountNumber());
        tx.setAmount(amount);
        tx.setNote("轉帳給 " + payee.getName());
        tx.setCompletedAt(LocalDateTime.now());

        txRepo.save(tx);

        // 7️⃣ 回傳與 Django 一樣格式
        return ResponseEntity.ok(Map.of(
                "detail", "transfer success",
                "new_balance", sender.getBalance()
        ));
    }
}
