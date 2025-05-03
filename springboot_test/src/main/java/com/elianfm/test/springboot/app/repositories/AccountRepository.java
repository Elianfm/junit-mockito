package com.elianfm.test.springboot.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.elianfm.test.springboot.app.models.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
    @Query("SELECT a FROM Account a WHERE a.person = ?1")
    Optional<Account> findByPerson(String person);

    // List<Account> findAll();
    // Account findById(Long id);
    // void update(Account account);
}
