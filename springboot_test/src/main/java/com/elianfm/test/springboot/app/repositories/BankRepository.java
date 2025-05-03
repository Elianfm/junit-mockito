package com.elianfm.test.springboot.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elianfm.test.springboot.app.models.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {
    List<Bank> findAll();
    //Bank findById(Long id);
    //void update(Bank bank);
}
