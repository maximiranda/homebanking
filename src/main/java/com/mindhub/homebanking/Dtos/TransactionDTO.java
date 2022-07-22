package com.mindhub.homebanking.Dtos;

import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import java.time.LocalDateTime;

public class TransactionDTO {
    private long id;
    private double amount;
    private  String description;
    private TransactionType type;
    private LocalDateTime date;

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.date = transaction.getDate();
        this.description = transaction.getDescription();
    }

    public long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
