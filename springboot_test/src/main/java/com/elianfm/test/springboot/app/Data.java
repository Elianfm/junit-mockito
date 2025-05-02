package com.elianfm.test.springboot.app;

import java.math.BigDecimal;

import com.elianfm.test.springboot.app.models.Account;
import com.elianfm.test.springboot.app.models.Bank;

public class Data {
/*     public static final Account ACCOUNT_1 = Account.builder()
            .id(1L)
            .person("Elian")
            .balance(new BigDecimal("1000.00"))
            .build();

    public static final Account ACCOUNT_2 = Account.builder()
            .id(2L)
            .person("Julián")
            .balance(new BigDecimal("2000.00"))
            .build();

    public static final Bank BANK_1 = Bank.builder()
            .id(1L)
            .name("Bank 1")
            .totalTransactions(0)
            .build();
 */
    public static Account createAccount001() {
        return Account.builder()
                .id(1L)
                .person("Elian")
                .balance(new BigDecimal("1000.00"))
                .build();
    }

    public static Account createAccount002() {
        return Account.builder()
                .id(2L)
                .person("Julián")
                .balance(new BigDecimal("2000.00"))
                .build();
    }

    public static Bank createBank001() {
        return Bank.builder()
                .id(1L)
                .name("Bank 1")
                .totalTransactions(0)
                .build();
    }

}


    

