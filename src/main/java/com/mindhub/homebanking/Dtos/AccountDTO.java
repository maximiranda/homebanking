package com.mindhub.homebanking.Dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountDTO {
    private long id;
    private String number;
    private double balance;
    private LocalDateTime creationDate;
    private Set<TransactionDTO> transactions;
    private AccountType accountType;
    private boolean isActive;

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.balance = account.getBalance();
        this.creationDate = account.getCreationDate();
        this.transactions = account.getTransactions().stream().map(TransactionDTO::new).collect(Collectors.toSet());
        this.accountType = account.getAccountType();
        this.isActive = account.getIsActive();
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public Set<TransactionDTO> getTransactions(){
        return transactions;
    }
}