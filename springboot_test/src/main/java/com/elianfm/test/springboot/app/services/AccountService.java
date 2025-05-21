package com.elianfm.test.springboot.app.services;

import java.math.BigDecimal;
import java.util.List;

import com.elianfm.test.springboot.app.models.Account;

public interface AccountService {
    Account findById(Long id);
    int checkTotalTransactions(Long bankId);
    BigDecimal checkBalance(Long accountId);
    void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount, Long bankId);

    List<Account> findAll();
    Account save(Account account);
}
