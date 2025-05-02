package com.elianfm.test.springboot.app.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bank {
    private Long id;
    private String name;
    private int totalTransactions;
}
