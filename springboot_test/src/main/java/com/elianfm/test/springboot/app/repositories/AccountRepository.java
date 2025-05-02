package com.elianfm.test.springboot.app.repositories;

import java.util.List;

import com.elianfm.test.springboot.app.models.Account;

public interface AccountRepository {
    List<Account> findAll();
    Account findById(Long id);
    void update(Account account);
}
