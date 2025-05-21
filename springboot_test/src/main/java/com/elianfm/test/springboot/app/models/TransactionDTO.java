package com.elianfm.test.springboot.app.models;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransactionDTO {
    private Long originId;
    private Long destinationId;
    private BigDecimal amount;
    private Long bankId;
}
