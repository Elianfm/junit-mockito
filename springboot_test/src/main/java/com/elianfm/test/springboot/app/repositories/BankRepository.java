package com.elianfm.test.springboot.app.repositories;

import java.util.List;

import com.elianfm.test.springboot.app.models.Bank;

public interface BankRepository {
    List<Bank> findAll();
    Bank findById(Long id);
    void update(Bank bank);
}
