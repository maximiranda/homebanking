package com.mindhub.homebanking.Services;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    public List<Transaction> getTransactions();
    public List<Transaction> getTransactionsByAccount(Account account);
    public List<Transaction> getTransactionsByAccountAndDate(Account account, LocalDateTime startDate, LocalDateTime endDate);
    public Transaction getTransactionById(Long id);
    public void saveTransaction(Transaction transaction);
}
