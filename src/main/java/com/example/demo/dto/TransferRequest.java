package com.example.demo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Integer payee_id;
    private BigDecimal amount;
    private Integer from_account_id;
}
