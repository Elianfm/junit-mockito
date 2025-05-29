package com.elianfm.test.springboot.app.controllers;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.elianfm.test.springboot.app.models.Account;
import com.elianfm.test.springboot.app.models.TransactionDTO;
import com.elianfm.test.springboot.app.services.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    //@ResponseStatus(OK)
    public ResponseEntity<Account> detail(@PathVariable Long id) {
        Account account = null;

        try {
            account = accountService.findById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(account);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<Account> list() {
        return accountService.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Account create(@RequestBody Account account) {
        return accountService.save(account);
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> transaction(@RequestBody TransactionDTO transactionDTO) {
        accountService.transfer(transactionDTO.getOriginId(), transactionDTO.getDestinationId(),
                transactionDTO.getAmount(), transactionDTO.getBankId());

        Map<String, Object> response = Map.of(
                "date", LocalDate.now(),
                "status", "ok",
                "message", "Transaction completed successfully",
                "transaction", transactionDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.deleteById(id);
    }

}
