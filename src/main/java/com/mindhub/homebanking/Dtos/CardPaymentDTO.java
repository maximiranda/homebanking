package com.mindhub.homebanking.Dtos;


import java.time.LocalDate;

public class CardPaymentDTO {

    private String cardNumber, cardHolder;
    private  int cardCvv;
    private LocalDate cardExp;
    private Double amount;
    private String description;

    public CardPaymentDTO(String cardNumber, String cardHolder, int cardCvv, LocalDate cardExp, Double amount, String description) {
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.cardCvv = cardCvv;
        this.cardExp = cardExp;
        this.amount = amount;
        this.description = description;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getCardCvv() {
        return cardCvv;
    }

    public LocalDate getCardExp() {
        return cardExp;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCardHolder() {
        return cardHolder;
    }
}
