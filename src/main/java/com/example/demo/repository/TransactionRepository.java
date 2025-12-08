package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // 🔥 使用 IN (...) 一次查詢所有帳戶的收付款紀錄
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.senderAccount IN :accounts " +
           "OR t.receiverAccount IN :accounts")
    List<Transaction> findByAccounts(@Param("accounts") List<String> accounts);
}
