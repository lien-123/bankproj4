package com.example.demo.entity;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payee")
@Data
public class Payee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String accountNumber;

    private Integer userId;

    private BigDecimal balance;

}