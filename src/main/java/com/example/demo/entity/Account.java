package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Data
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String accountNumber;

    private BigDecimal balance;

    private Integer userId;
}
