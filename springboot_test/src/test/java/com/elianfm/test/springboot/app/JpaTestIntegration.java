package com.elianfm.test.springboot.app;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import com.elianfm.test.springboot.app.models.Account;
import com.elianfm.test.springboot.app.repositories.AccountRepository;

// @DataJpaTest es una anotación de Spring Boot que se utiliza para realizar pruebas 
// de integración en la capa de persistencia (JPA) de una aplicación Spring.
// Esta anotación configura un contexto de prueba que incluye una base de datos en memoria (como H2) 
// y escanea los repositorios JPA definidos en el paquete especificado.

// Los datos del import.sql se resetearán cada vez que se ejecute un @Test,
// y cuando se finalicen todas las pruebas, se eliminará la base de datos en memoria.
@Tag("integration_jpa")
@DataJpaTest
public class JpaTestIntegration {
    
    @Autowired
    AccountRepository accountRepository;

    @Test
    void testFindById() {
        Optional<Account> account = accountRepository.findById(1L);
        assertTrue(account.isPresent(), "Account should be present");
        assertEquals("John Doe", account.get().getPerson(), "Account person should be John Doe");
    }

    @Test
    void testFindByPerson() {
        Optional<Account> account = accountRepository.findByPerson("John Doe");
        assertTrue(account.isPresent(), "Account should be present");
        assertEquals(1L, account.get().getId(), "Account ID should be 1");
        assertEquals("1000.00" , account.get().getBalance().toPlainString(), "Account balance should be 1000");
    }

    @Test
    void testFindByPersonNotFound() {
        Optional<Account> account = accountRepository.findByPerson("Jane Doe");
        assertThrows(NoSuchElementException.class, account::orElseThrow, "Account should not be present");
        assertFalse(account.isPresent(), "Account should not be present");
    }

    @Test
    void testFindAll() {
        List<Account> accounts = accountRepository.findAll();
        assertFalse(accounts.isEmpty(), "Accounts should not be empty");
        assertEquals(4, accounts.size(), "There should be 4 accounts in the database");
        assertEquals("John Doe", accounts.get(0).getPerson(), "First account person should be John Doe");
        assertEquals("Jane Smith", accounts.get(1).getPerson(), "Second account person should be Jane Smith");
    }

    @Test 
    void testSave() {
        // Given
        new Account();
        Account account = Account.builder()
                .person("Elian")
                .balance(new BigDecimal("1000.00"))
                .build();
        accountRepository.save(account);

        // When
        // Recomendable no usar el id, ya que es autoincremental y no se puede garantizar que sea el mismo
        accountRepository.findByPerson("Elian").orElseThrow(() -> new NoSuchElementException("Account not found"));

        // Then
        assertEquals("Elian", account.getPerson(), "Account person should be Elian");
        assertEquals("1000.00", account.getBalance().toPlainString(), "Account balance should be 1000.00");

    }

    @Test
    void testUpdate() {
        // Given
        Account account = accountRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Account not found"));
        account.setPerson("John Smith");
        account.setBalance(new BigDecimal("2000.00"));
        accountRepository.save(account);

        // When
        Account updatedAccount = accountRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Account not found"));

        // Then
        assertEquals("John Smith", updatedAccount.getPerson(), "Account person should be John Smith");
        assertEquals("2000.00", updatedAccount.getBalance().toPlainString(), "Account balance should be 2000.00");
    }
    
    @Test
    void testDelete() {
        // Given
        Account account = accountRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("Account not found"));
        accountRepository.delete(account);

        // When
        Optional<Account> deletedAccount = accountRepository.findById(1L);

        // Then
        assertFalse(deletedAccount.isPresent(), "Account should not be present after deletion");
        assertThrows(NoSuchElementException.class, deletedAccount::orElseThrow, "Account should not be present after deletion");
        assertEquals(3, accountRepository.findAll().size(), "There should be 3 accounts in the database after deletion");
    }

}
