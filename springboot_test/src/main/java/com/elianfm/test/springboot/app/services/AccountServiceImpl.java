package com.elianfm.test.springboot.app.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elianfm.test.springboot.app.models.Account;
import com.elianfm.test.springboot.app.models.Bank;
import com.elianfm.test.springboot.app.repositories.AccountRepository;
import com.elianfm.test.springboot.app.repositories.BankRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private BankRepository bankRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public int checkTotalTransactions(Long bankId) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Bank not found with id: " + bankId));
        if (bank == null)
            throw new IllegalArgumentException("Bank not found with id: " + bankId);

        return bank.getTotalTransactions();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal checkBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        if (account == null)
            throw new IllegalArgumentException("Account not found with id: " + accountId);

        return account.getBalance();
    }

    @Override
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount,
            Long bankId) {

        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new IllegalArgumentException("Bank not found with id: " + bankId));
        if (bank == null)
            throw new IllegalArgumentException("Bank not found with id: " + fromAccountId);

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("From account not found with id: " + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("To account not found with id: " + toAccountId));

        if (fromAccount == null)
            throw new IllegalArgumentException("From account not found with id: " + fromAccountId);

        if (toAccount == null)
            throw new IllegalArgumentException("To account not found with id: " + toAccountId);

        fromAccount.debit(amount);
        toAccount.credit(amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        int totalTransactions = bank.getTotalTransactions();
        bank.setTotalTransactions(++totalTransactions);
        bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

}
