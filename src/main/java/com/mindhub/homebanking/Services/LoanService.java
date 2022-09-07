package com.mindhub.homebanking.Services;

import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    public List<Loan> getLoans();
    public Loan getLoanById(Long id);
    public void saveLoan(Loan loan);
}
