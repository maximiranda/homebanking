package com.mindhub.homebanking.Services;

import com.mindhub.homebanking.models.ClientLoan;

import java.util.List;

public interface ClientLoanService {
    public List<ClientLoan> getClientLoans();
    public ClientLoan getClientLoanById(Long id);
    public void saveClientLoan(ClientLoan clientLoan);
}
