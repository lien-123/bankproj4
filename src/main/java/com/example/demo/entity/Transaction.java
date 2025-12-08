package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Data
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String senderAccount;

    private String receiverAccount;

    private BigDecimal amount;

    private String note;

    private LocalDateTime completedAt;
}
